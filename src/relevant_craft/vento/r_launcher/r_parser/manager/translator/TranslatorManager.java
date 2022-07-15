package relevant_craft.vento.r_launcher.r_parser.manager.translator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import relevant_craft.vento.r_launcher.r_parser.utils.HashUtils;
import relevant_craft.vento.r_launcher.r_parser.utils.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslatorManager {

    private static final String GOOGLE_URL = "https://translation.googleapis.com/language/translate/v2";
    private static final String GOOGLE_API = "";

    private static final String YANDEX_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private static final String YANDEX_API = "";

    private static final File translated_cache = new File("translated-cache");

    public static void initTranslatorCache() {
        if (!translated_cache.exists()) {
            translated_cache.mkdir();
        }
    }

    public static boolean hasTranslatedCache(String hash) {
        return new File(translated_cache + File.separator + hash).exists();
    }

    private static String loadTranslatedCache(String hash, String text) {
        try (BufferedReader br = new BufferedReader(new FileReader(translated_cache + File.separator + hash))) {
            StringBuilder result = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(line);
            }

            return result.toString();
        } catch (Exception e) {
            return text;
        }
    }

    private static void saveTranslatedCache(String hash, String text) {
        try (FileWriter writer = new FileWriter(translated_cache + File.separator + hash)) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkTranslatingText(String text) {
        if (text == null || text.isEmpty() || text.equals(" ")) {
            return false;
        }

        Pattern pattern = Pattern.compile("\\w\\D");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public static String translateText(String text) {
        if (!checkTranslatingText(text)) {
            return text;
        }

        String hash = HashUtils.md5(text);
        if (hash == null) {
            return text;
        }
        hash = "R-Parser" + "_" + text.length() + "_" + hash;

        if (hasTranslatedCache(hash)) {
            return loadTranslatedCache(hash, text);
        }

        String translatedText;
        translatedText = translateWithGoogle(text);
        if (translatedText.equals(text)) {
            translatedText = translateWithYandex(text);
        }
        translatedText = translatedText.trim();
        if (!translatedText.equals(text)) {
            saveTranslatedCache(hash, translatedText);
        }

        return translatedText;
    }

    public static String translateTextFree(String text) {
        if (!checkTranslatingText(text)) {
            return text;
        }

        String hash = HashUtils.md5(text);
        if (hash == null) {
            return text;
        }
        hash = "R-Parser" + "_" + text.length() + "_" + hash;

        if (hasTranslatedCache(hash)) {
            return loadTranslatedCache(hash, text);
        }

        String translatedText;
        int attempts = 0;
        do {
            attempts++;
            translatedText = WebGoogleTranslator.translate(text);
        } while (translatedText.equals(text) && attempts <= 3);

        translatedText = translatedText.trim();
        if (!translatedText.equals(text)) {
            saveTranslatedCache(hash, translatedText);
        }

        return translatedText;
    }

    private static String translateWithGoogle(String text) {
        try {
            StringBuilder params = new StringBuilder();
            params.append("q").append("=").append(URLEncoder.encode(text, StandardCharsets.UTF_8.toString()));
            params.append("&");
            params.append("target").append("=").append("ru");
            params.append("&");
            params.append("source").append("=").append("en");
            params.append("&");
            params.append("key").append("=").append(GOOGLE_API);

            JsonObject jsonResponse;
            try {
                jsonResponse = httpPost(GOOGLE_URL, params);
            } catch (Exception e) {
                Logger.log("Google Translate error (response code: " + e.getMessage() + ").");
                throw new Exception();
            }
            JsonObject data = jsonResponse.get("data").getAsJsonObject();
            JsonArray translations = data.getAsJsonArray("translations");
            StringBuilder result = new StringBuilder();
            if (translations.size() > 0) {
                for (JsonElement element : translations) {
                    JsonObject translation = element.getAsJsonObject();
                    result.append(translation.get("translatedText").getAsString()).append(" ");
                }

                return result.toString();
            } else {
                Logger.log("Google Translate error.");
                throw new Exception();
            }
        } catch (Exception e) {
            return text;
        }
    }

    private static String translateWithYandex(String text) {
        try {
            StringBuilder params = new StringBuilder();
            params.append("key").append("=").append(YANDEX_API);
            params.append("&");
            params.append("text").append("=").append(URLEncoder.encode(text, StandardCharsets.UTF_8.toString()));
            params.append("&");
            params.append("lang").append("=").append("en-ru");

            JsonObject jsonResponse;
            try {
                jsonResponse = httpPost(YANDEX_URL, params);
            } catch (Exception e) {
                Logger.log("Yandex Translate error (response code: " + e.getMessage() + ").");
                throw new Exception();
            }

            int code = jsonResponse.get("code").getAsInt();
            if (code == 200) {
                JsonArray lines = jsonResponse.get("text").getAsJsonArray();
                StringBuilder result = new StringBuilder();
                for (JsonElement line : lines) {
                    result.append(line.getAsString()).append(" ");
                }

                return result.toString();
            } else {
                Logger.log("Yandex Translate error code: " + code + ".");
                throw new Exception();
            }

        } catch (Exception e) {
            return text;
        }
    }

    private static JsonObject httpPost(String web,  StringBuilder params) throws Exception {
        URL url = new URL(web);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setDoOutput(true);
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConnection.setRequestProperty("Accept", "application/json");

        DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
        wr.write(params.toString().getBytes(StandardCharsets.UTF_8));
        int responseCode = httpConnection.getResponseCode();

        BufferedReader bufferedReader;

        if (responseCode > 199 && responseCode < 300) {
            bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        } else {
            throw new Exception(String.valueOf(responseCode));
        }

        StringBuilder response = new StringBuilder();
        String response_line;
        while ((response_line = bufferedReader.readLine()) != null) {
            response.append(response_line);
        }
        bufferedReader.close();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response.toString());
        return element.getAsJsonObject();
    }
}

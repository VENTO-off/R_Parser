package relevant_craft.vento.r_launcher.r_parser.manager.description;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import relevant_craft.vento.r_launcher.r_parser.CF_ProjectURL;
import relevant_craft.vento.r_launcher.r_parser.manager.translator.TranslatorManager;
import relevant_craft.vento.r_launcher.r_parser.utils.FileUtils;
import relevant_craft.vento.r_launcher.r_parser.utils.PageUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static relevant_craft.vento.r_launcher.r_parser.manager.parser.CF_ParserManager.sleep;

public class DescriptionManager {

    public static final File PROJECT_DETAILS = new File("ProjectDetails");
    private static final int PROJECT_DETAILS_TIMEOUT = 1000 * 60 * 60 * 24 * 30;    //30 days

    public static boolean getFullDescription(String project_href, CF_ProjectURL cf_project, String name) {
        if (!PROJECT_DETAILS.exists()) {
            PROJECT_DETAILS.mkdir();
        }
        File file = getFileDetails(project_href, cf_project, false);
        if (file.exists()) {
            if (FileUtils.getCreationTime(file) <= (System.currentTimeMillis() - PROJECT_DETAILS_TIMEOUT)) {
                return true;
            }
        }
        File file_ru = getFileDetails(project_href, cf_project, true);

        Document doc = PageUtils.loadDocument(project_href);
        if (doc == null) {
            return false;
        }

        Element project_detail = doc.selectFirst("section[class='flex flex-col project-detail']");
        Element content = project_detail.selectFirst("div[class='box p-4 pb-2 project-detail__content']");

        fixURLs(content);
        fixImages(content);
        fixSpoilers(content);

        writeFile(file, content.toString());

        translateText(content, name);

        writeFile(file_ru, content.toString());

        return true;
    }

    private static void writeFile(File file, String content) {
        if (file.exists()) {
            file.delete();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.append("<head>" +
                    "<meta charset='UTF-8'>" +
                    "<script>" +
                    "function toggleSpoiler(id) { style = document.getElementById(id).style; style.display = (style.display == 'block') ? 'none' : 'block'; }" +
                    "</script>" +
                    "</head>");
            writer.append(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getFileDetails(String project_href, CF_ProjectURL cf_project, boolean isRussian) {
        return new File(PROJECT_DETAILS + File.separator + cf_project.getProject_url().substring(cf_project.getProject_url().lastIndexOf('/') + 1) + "_" + project_href.substring(project_href.lastIndexOf('/') + 1) + (isRussian ? "_ru" : "") + ".html");
    }

    private static void fixURLs(Element content) {
        Elements links = content.select("a[href]");
        for (Element link : links) {
            if (!link.attr("href").toLowerCase().startsWith("/linkout?remoteUrl=")) {
                try {
                    String url = link.attr("href");
                    url = url.substring(url.indexOf("=") + 1);
                    url = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
                    url = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
                    link.attr("href", url);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static void fixImages(Element content) {
        Elements imgs = content.select("img");
        for (Element img : imgs) {
            if (img.hasAttr("height")) {
                img.removeAttr("height");
            }
        }
    }

    private static void fixSpoilers(Element content) {
        Elements spoilers = content.select("div[class='spoiler']");
        if (spoilers.isEmpty()) {
            return;
        }

        Random rand = new Random();
        for (Element spoiler : spoilers) {
            String id = "spoiler_" + (rand.nextInt(900) + 100);
            spoiler.attr("id", id);
            spoiler.attr("style", "display: none;");
            spoiler.before("<a class='spoiler_btn' onclick=\"toggleSpoiler('" + id + "'); return false;\">Spoiler (click to show)</a>");
        }
    }

    private static void translateText(Element content, String name) {
        String blacklist = content.select("blockquote").toString();

        Elements paragraphs = content.select("*");
        for (Element paragraph : paragraphs) {
            for (TextNode text : paragraph.textNodes()) {
                if (text.text() == null || text.text().isEmpty() || text.text().equals(" ")) {
                    continue;
                }
                if (blacklist.contains(text.toString())) {
                    continue;
                }
                if (text.text().contains(name)) {
                    text.text(text.text().replace(name, "tag_mod"));
                }

                //String translatedText = TranslatorManager.translateText(text.text());
                String translatedText = TranslatorManager.translateTextFree(text.text());

                translatedText = translatedText.replace("tag_mod", name);
                text.text((!translatedText.startsWith(" ") && text.text().startsWith(" ") ? " " : "") + translatedText + (!translatedText.endsWith(" ") && text.text().endsWith(" ") ? " " : ""));
                sleep(500);
            }
        }
    }
}

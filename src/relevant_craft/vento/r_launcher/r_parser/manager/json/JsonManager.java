package relevant_craft.vento.r_launcher.r_parser.manager.json;

import com.google.gson.Gson;
import relevant_craft.vento.r_launcher.r_parser.CF_ProjectURL;
import relevant_craft.vento.r_launcher.r_parser.manager.cf_project.CF_Project;

import java.io.*;

public class JsonManager {

    private File dir;
    private String versionName;

    private FileOutputStream fos;
    private Writer writer;

    private boolean isFirst;

    public JsonManager(CF_ProjectURL cf_projectURL, String versionName) {
        this.dir = new File(cf_projectURL.toString());
        if (!dir.exists()) { dir.mkdir(); }

        this.versionName = versionName;

        this.isFirst = true;

        try {
            this.fos = new FileOutputStream(dir.getAbsolutePath() + File.separator + versionName + ".json.parsing");
            this.writer = new BufferedWriter(new OutputStreamWriter(fos, "Cp1251"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeToJson(CF_Project cf_project) {
        Gson gson = new Gson();

        try {
            if (!isFirst) {
                writer.append(",");
            }
            writer.append(gson.toJson(cf_project));
            writer.flush();

            isFirst = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startJson() {
        try {
            writer.append("{\"_comment\": \"Parsed from CurseForge.COM by R-Launcher.SU\",\"cf-projects\":[");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finalizeJson() {
        File json = new File(dir + File.separator + versionName + ".json.parsing");
        if (!json.exists()) {
            return;
        }
        File finalJson = new File(dir + File.separator + versionName + ".json");

        try {
            writer.append("]}");
            writer.flush();
            writer.close();

            boolean renamed = json.renameTo(finalJson);
//            if (renamed) {
//                try {
//                    TarGzUtils.createTarGZ(finalJson, new File(dir + File.separator + versionName + ".tar.gz"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package relevant_craft.vento.r_launcher.r_parser.manager.version;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import relevant_craft.vento.r_launcher.r_parser.CF_ProjectURL;
import relevant_craft.vento.r_launcher.r_parser.utils.Logger;
import relevant_craft.vento.r_launcher.r_parser.utils.PageUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static relevant_craft.vento.r_launcher.r_parser.manager.parser.CF_ParserManager.sleep;

public class VersionManager {

    public static List<Version> versions = new ArrayList<>();

    public static void getVersions() {
        Logger.log("Loading Minecraft versions...");
        long start = System.currentTimeMillis();

        Document doc;
        do {
            doc = PageUtils.loadDocument(CF_ProjectURL.values()[0].getProject_url());
            sleep(500);
        } while (doc == null);

        boolean doSkip = false;
        Elements versions = doc.getElementById("filter-game-version").select("option");
        for (Element version : versions) {
            String name = version.text();
            String value = version.attr("value");
            String id = version.attr("id");
            if (id.contains("gameversiontype")) {
                if (!name.startsWith("Minecraft")) {
                    doSkip = true;
                } else {
                    doSkip = false;
                    continue;
                }
            }

            if (doSkip) {
                continue;
            }

            if (name.equals("All Versions") || name.endsWith("Snapshot")) {
                continue;
            }

            VersionManager.versions.add(new Version(name, value));
        }
        Logger.log("Successfully loaded Minecraft versions (took " + (System.currentTimeMillis() - start) + " ms).");
    }

    private static Version getVersionByName(String name) {
        for (Version ve : versions) {
            if (ve.getName().equals(name)) {
                return ve;
            }
        }

        return null;
    }

    private static Version getVersionByValue(String value) {
        for (Version ve : versions) {
            if (ve.getValue().equals(value)) {
                return ve;
            }
        }

        return null;
    }

    public static String applyVersionFilterByName(String url, String version) {
        Version ve = getVersionByName(version);
        return url + "?filter-game-version=" + ve.getValue().replace(":", "%3A");
    }

    public static void writeAllVersions(CF_ProjectURL cf_projectURL) {
        File dir = new File(cf_projectURL.toString());
        if (!dir.exists()) { dir.mkdir(); }

        try (FileWriter writer = new FileWriter(dir.getAbsolutePath() + File.separator + "versions.txt", true)) {
            for (Version ve : versions) {
                writer.append(ve.getName() + "<::>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

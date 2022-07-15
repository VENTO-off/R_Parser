package relevant_craft.vento.r_launcher.r_parser.manager.parser;

import com.google.gson.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import relevant_craft.vento.r_launcher.r_parser.CF_ProjectURL;
import relevant_craft.vento.r_launcher.r_parser.manager.cf_project.CF_Project;
import relevant_craft.vento.r_launcher.r_parser.manager.dependence.Dependence;
import relevant_craft.vento.r_launcher.r_parser.manager.dependence.DependenceManager;
import relevant_craft.vento.r_launcher.r_parser.manager.downloads.Downloads;
import relevant_craft.vento.r_launcher.r_parser.manager.downloads.DownloadsManager;
import relevant_craft.vento.r_launcher.r_parser.manager.images.Images;
import relevant_craft.vento.r_launcher.r_parser.manager.images.ImagesManager;
import relevant_craft.vento.r_launcher.r_parser.manager.json.JsonManager;
import relevant_craft.vento.r_launcher.r_parser.manager.translator.TranslatorManager;
import relevant_craft.vento.r_launcher.r_parser.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.r_parser.utils.FileUtils;
import relevant_craft.vento.r_launcher.r_parser.utils.Logger;
import relevant_craft.vento.r_launcher.r_parser.utils.PageUtils;
import relevant_craft.vento.r_launcher.r_parser.utils.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CF_ParserManager {

    private static CF_ProjectURL current_cf_project = null;

    public static void parseProject(CF_ProjectURL cf_projectURL, String versionName) {
        System.gc();

        long start = System.currentTimeMillis();
        Document doc;
        do {
            doc = PageUtils.loadDocument(VersionManager.applyVersionFilterByName(cf_projectURL.getProject_url(), versionName));
            sleep(500);
        } while (doc == null);

        if (current_cf_project == null || current_cf_project != cf_projectURL) {
            current_cf_project = cf_projectURL;
            FileUtils.checkDirectory(cf_projectURL);
            PageUtils.writeAllCategories(doc, cf_projectURL);
            VersionManager.writeAllVersions(cf_projectURL);
        }

        Logger.emptyLog();
        Logger.log("Starting parsing of " + cf_projectURL.toString() + " " + versionName + "...");

        JsonManager json = new JsonManager(cf_projectURL, versionName);

        json.startJson();

        //Load old json
        List<CF_Project> cf_projects = loadJson(cf_projectURL, versionName);

        //Stats
        int parsed = 0;
        int cached = 0;
        int bad = 0;
        int total = 0;

        sleep(100);

        int pages = PageUtils.getPages(doc);

        for (int page = 1; page <= pages; page++) {
            do {
                doc = PageUtils.loadDocument(VersionManager.applyVersionFilterByName(cf_projectURL.getProject_url(), versionName) + "&page=" + page);
                sleep(500);
            } while (doc == null);

            Elements projects = PageUtils.getProjects(doc);
            project:for (Element project : projects) {
                total++;    //stats

                String avatar = PageUtils.getAvatar(project);

                String title = PageUtils.getTitle(project);

                Element info_stats = PageUtils.getInfoStats(project);

                String downloads = PageUtils.getDownloads(info_stats);

                String date = PageUtils.getDate(info_stats);

                String description = PageUtils.getDescription(project);

                String description_ru = TranslatorManager.translateText(description);

                String href = PageUtils.getHref(project);

                List<String> categories = PageUtils.getCategories(project);

                //Check from old json
                CF_Project oldProject = getProjectByHref(cf_projects, href);
                if (oldProject != null) {
                    if (dateToMillis(date) == dateToMillis(oldProject.getDate())) {
                        json.writeToJson(oldProject);
                        cached++;    //stats
                        continue project;
                    }
                }

                int attempts = 0;

//                boolean fullDescription;
//                do {
//                    attempts++;
//                    fullDescription = DescriptionManager.getFullDescription(href, current_cf_project, title);
//                    if (attempts >= 10) fullDescription = true;
//                    sleep(50);
//                } while (!fullDescription);
//
//                attempts = 0;

                List<Images> images;
                do {
                    images = ImagesManager.getImages(href);
                    attempts++;
                    if (attempts >= 10) {
                        bad++;    //stats
                        continue project;
                    }
                    sleep(500);
                } while (images == null);

                attempts = 0;

                List<Dependence> dependencies;
                do {
                    if (current_cf_project == CF_ProjectURL.ModPacks) {
                        dependencies = DependenceManager.getMods(href);
                    } else {
                        dependencies = DependenceManager.getDependencies(href);
                    }
                    attempts++;
                    if (attempts >= 10) {
                        bad++;    //stats
                        continue project;
                    }
                    sleep(500);
                } while (dependencies == null);

                attempts = 0;

                List<Downloads> files;
                do {
                    files = DownloadsManager.getDownloads(href, versionName);
                    attempts++;
                    if (attempts >= 10) {
                        bad++;    //stats
                        continue project;
                    }
                    sleep(500);
                } while (files == null);

                CF_Project cf_project = new CF_Project(avatar, title, downloads, date, description, description_ru, href, categories, images, dependencies, files);
                json.writeToJson(cf_project);
                parsed++;    //stats
            }
            Logger.log("Parsing progress of " + cf_projectURL.toString() + " " + versionName + ": " + (page * 100 / pages) + "%");
        }
        json.finalizeJson();
        Logger.log("Finished parsing of " + cf_projectURL.toString() + " " + versionName + " (took " + TimeUtils.formatTime(System.currentTimeMillis() - start) + ").");
        Logger.log("Parsed: " + parsed + " (" + (total == 0 ? 0 : (parsed * 100 / total)) + "%)" + " | " +
                   "Cached: " + cached + " (" + (total == 0 ? 0 : (cached * 100 / total)) + "%)" + " | " +
                   "Bad: "    + bad    + " (" + (total == 0 ? 0 : (bad    * 100 / total)) + "%)" + " | " +
                   "Total: "  + total);

        cf_projects = null;
        sleep(5000);
        System.gc();
    }

    private static List<CF_Project> loadJson(CF_ProjectURL cf_projectURL, String versionName) {
        List<CF_Project> projects = new ArrayList<>();

        File json = new File(cf_projectURL.toString() + "_old" + File.separator + versionName + ".json");
        if (!json.exists()) {
            return projects;
        }

        try {
            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(json), "Windows-1251");
            JsonParser parser = new JsonParser();
            Gson gson = new Gson();
            JsonElement element = parser.parse(fileReader);
            fileReader.close();

            JsonObject jsonObject = element.getAsJsonObject();
            JsonArray cf_projects = jsonObject.getAsJsonArray("cf-projects");
            for (JsonElement current_element : cf_projects) {
                JsonObject cf_project = current_element.getAsJsonObject();
                projects.add(gson.fromJson(cf_project.toString(), CF_Project.class));
            }
        } catch (Exception ignored) {}

        return projects;
    }

    private static long dateToMillis(String cf_projectDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            return formatter.parse(cf_projectDate).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    private static String lastProjectHref = null;

    private static CF_Project getProjectByHref(List<CF_Project> cf_projects, String href) {
        for (CF_Project cf_project : cf_projects) {
            if (lastProjectHref != null) {
                if (cf_project.getHref().equalsIgnoreCase(lastProjectHref)) {
                    cf_projects.remove(cf_project);
                    break;
                }
            }
        }

        for (CF_Project cf_project : cf_projects) {
            if (cf_project.getHref().equalsIgnoreCase(href)) {
                lastProjectHref = cf_project.getHref();
                return cf_project;
            }
        }

        return null;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
}

package relevant_craft.vento.r_launcher.r_parser.manager.downloads;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import relevant_craft.vento.r_launcher.r_parser.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.r_parser.utils.PageUtils;

import java.util.ArrayList;
import java.util.List;

public class DownloadsManager {

    public static List<Downloads> getDownloads(String project_href, String version) {
        List<Downloads> downloads = new ArrayList<>();

        Document doc = PageUtils.loadDocument(VersionManager.applyVersionFilterByName(project_href + "/files/all", version));
        if (doc == null) {
            return null;
        }

        if (doc.body().toString().contains("Internal Server Error")) {
            return null;
        }

        Element files_table;
        try {
            files_table = doc.selectFirst("table[class='listing listing-project-file project-file-listing b-table b-table-a']");
        } catch (NullPointerException e) {
            return downloads;
        }
        if (files_table == null) {
            return downloads;
        }

        Elements files;
        try {
            files = files_table.selectFirst("tbody").select("tr");
        } catch (NullPointerException e) {
            return downloads;
        }
        if (files == null) {
            return downloads;
        }

        if (!files.isEmpty()) {
            for (Element file : files) {
                Elements columns = file.select("td");

                String status = columns.get(0).text();
                switch (status) {
                    case "A": status = "Alpha"; break;
                    case "B": status = "Beta"; break;
                    case "R": status = "Release"; break;
                }

                String name = columns.get(1).selectFirst("a").text();

                String link = columns.get(1).selectFirst("a").absUrl("href");
                String file_id = link.substring(link.lastIndexOf('/') + 1);
                link = link.substring(0, link.lastIndexOf('/'));
                link = link.substring(0, link.lastIndexOf('/'));
                link += "/download/" + file_id + "/file";

                String size = columns.get(2).text();

                String uploaded = columns.get(3).text();

                downloads.add(new Downloads(status, name, size, uploaded, link));
            }
        }

        return downloads;
    }
}

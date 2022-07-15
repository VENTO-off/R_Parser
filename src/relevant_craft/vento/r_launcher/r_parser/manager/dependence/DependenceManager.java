package relevant_craft.vento.r_launcher.r_parser.manager.dependence;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import relevant_craft.vento.r_launcher.r_parser.utils.PageUtils;

import java.util.ArrayList;
import java.util.List;

import static relevant_craft.vento.r_launcher.r_parser.manager.parser.CF_ParserManager.sleep;

public class DependenceManager {

    private static final String dependencies_filter = "/relations/dependencies?filter-related-dependencies=3";
    private static final String mods_filter = "/relations/dependencies?filter-related-dependencies=6";

    public static List<Dependence> getDependencies(String project_href) {
        List<Dependence> dependencies = new ArrayList<>();

        Document doc = PageUtils.loadDocument(project_href + dependencies_filter);
        if (doc == null) {
            return null;
        }

        if (doc.body().toString().contains("Internal Server Error")) {
            return null;
        }

        getProjects(doc, dependencies, true);

        return dependencies;
    }

    public static List<Dependence> getMods(String project_href) {
        List<Dependence> mods = new ArrayList<>();

        Document doc = PageUtils.loadDocument(project_href + mods_filter);
        if (doc == null) {
            return null;
        }

        if (doc.body().toString().contains("Internal Server Error")) {
            return null;
        }

        int pages = PageUtils.getPages(doc);
        for (int page = 1; page <= pages; page++) {
            do {
                doc = PageUtils.loadDocument(project_href + mods_filter + "&page=" + page);
                sleep(500);
            } while (doc == null);

            getProjects(doc, mods, false);
        }

        return mods;
    }

    private static void getProjects(Document doc, List<Dependence> mods, boolean includeHref) {
        Elements projects = PageUtils.getProjects(doc);
        if (projects == null) {
            return;
        }

        if (!projects.isEmpty()) {
            for (Element project : projects) {

                String title = PageUtils.getTitle(project);
                String href = PageUtils.getHref(project);

                mods.add(new Dependence(title, (includeHref ? href : "")));
            }
        }
    }
}

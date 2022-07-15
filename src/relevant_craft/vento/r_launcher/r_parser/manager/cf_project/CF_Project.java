package relevant_craft.vento.r_launcher.r_parser.manager.cf_project;

import relevant_craft.vento.r_launcher.r_parser.manager.dependence.Dependence;
import relevant_craft.vento.r_launcher.r_parser.manager.downloads.Downloads;
import relevant_craft.vento.r_launcher.r_parser.manager.images.Images;

import java.util.List;

public class CF_Project {
    private String avatar;
    private String title;
    private String downloads;
    private String date;
    private String description;
    private String description_ru;
    private String href;
    private List<String> categories;
    private List<Images> images;
    private List<Dependence> dependencies;
    private List<Downloads> files;

    public CF_Project(String avatar, String title, String downloads, String date, String description, String description_ru, String href, List<String> categories, List<Images> images, List<Dependence> dependencies, List<Downloads> files) {
        this.avatar = avatar;
        this.title = title;
        this.downloads = downloads;
        this.date = date;
        this.description = description;
        this.description_ru = description_ru;
        this.href = href;
        this.categories = categories;
        this.images = images;
        this.dependencies = dependencies;
        this.files = files;
    }

    public String getDate() {
        return date;
    }

    public String getHref() {
        return href;
    }
}

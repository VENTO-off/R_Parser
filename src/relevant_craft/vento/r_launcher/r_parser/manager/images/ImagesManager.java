package relevant_craft.vento.r_launcher.r_parser.manager.images;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import relevant_craft.vento.r_launcher.r_parser.utils.PageUtils;

import java.util.ArrayList;
import java.util.List;

public class ImagesManager {

    public static List<Images> getImages(String project_href) {
        List<Images> images = new ArrayList<>();

        Document doc = PageUtils.loadDocument(project_href + "/screenshots");
        if (doc == null) {
            return null;
        }

        if (doc.body().toString().contains("Internal Server Error")) {
            return null;
        }

        Elements pictures;
        try {
            pictures = doc.selectFirst("div[class='project-screenshot-page']").getElementsByClass("flex flex-col");
        } catch (NullPointerException e) {
            return images;
        }
        if (pictures == null) {
            return images;
        }

        if (!pictures.isEmpty()) {
            for (Element picture : pictures) {
                String thumbnail = picture.select("img").first().absUrl("src");

                String image = thumbnail.substring(0, thumbnail.lastIndexOf('/'));
                image = image.substring(0, image.lastIndexOf('/'));
                image = image.substring(0, image.lastIndexOf('/'));
                image = image.replace("/thumbnails", "");
                image = image + thumbnail.substring(thumbnail.lastIndexOf('/'));
                images.add(new Images(thumbnail, image));
            }
        }

        return images;
    }
}

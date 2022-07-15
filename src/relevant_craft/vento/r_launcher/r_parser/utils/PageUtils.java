package relevant_craft.vento.r_launcher.r_parser.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import relevant_craft.vento.r_launcher.r_parser.CF_ProjectURL;
import relevant_craft.vento.r_launcher.r_parser.manager.cloudflare.CloudFlareManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageUtils {

    public static CookieManager cookieManager = null;
    public static long activeCookies = 0;
    public static String lastUrl = null;

    public static Document loadDocument(String url) {
        try {
            lastUrl = url;

            if (activeCookies < System.currentTimeMillis()) {
                CloudFlareManager.authCloudflare();
            }

            Connection.Response response = Jsoup.connect(url)
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("accept-encoding", "gzip, deflate, sdch, br")
                    .header("accept-language", "en-US,en;q=0.8")
                    .header("cache-control", "max-age=0")
                    .header("user-agent", BrowserVersion.CHROME.getUserAgent())
                    .header("sec-fetch-user", "?1")
                    .header("upgrade-insecure-requests", "1")
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .cookies(getCookies())
                    .method(Connection.Method.GET)
                    .timeout(5000)
                    .execute();

            Document doc = response.parse();
            if (!doc.title().contains("CurseForge")) {
                activeCookies = System.currentTimeMillis();
                return null;
            }

            return doc;
        } catch (Exception e) {
            return null;
        }
    }

    private static Map<String, String> getCookies() {
        Map<String, String> cookies = new HashMap<>();
        for (Cookie cookie : cookieManager.getCookies()) {
            cookies.put(cookie.getName(), cookie.getValue());
        }

        return cookies;
    }

    public static int getPages(Document doc) {
        try {
            Element pages = doc.selectFirst("div[class='pagination pagination-top flex items-center']");
            return Integer.parseInt(pages.select("a[class='pagination-item']").last().text());
        } catch (Exception e) {
            return 1;
        }
    }

    public static void writeAllCategories(Document doc, CF_ProjectURL cf_projectURL) {
        Logger.emptyLog();
        Logger.log("Parsing categories of " + cf_projectURL.toString() + "...");
        long start = System.currentTimeMillis();

        File dir = new File(cf_projectURL.toString());
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + File.separator + "categories.txt");
            Writer writer = new BufferedWriter(new OutputStreamWriter(fos, "Cp1251"));
            Element nav = doc.selectFirst("nav[class='box flex flex-col pb-1 my-2']");
            Elements categories = nav.select("div[class='category-list-item relative']");
            for (Element category : categories) {
                String url = category.selectFirst("div[class='px-2']").selectFirst("a").absUrl("href");
                String id = url.substring(url.lastIndexOf('/') + 1);
                if (id.isEmpty()) {
                    continue;
                }
                writer.append(id).append("=").append(Translator.translateToRussian(id)).append("<::>");
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.log("Finished parsing categories of " + cf_projectURL.toString() + " (took " + (System.currentTimeMillis() - start) + " ms).");
    }

    public static Elements getProjects(Document doc) {
        return doc.getElementsByClass("project-listing-row");
    }

    public static String getAvatar(Element project) {
        try {
            return project.getElementsByClass("project-avatar").first().select("img").first().absUrl("src");
        } catch (NullPointerException e) {
            return "none";
        }
    }

    public static String getTitle(Element project) {
        try {
            return project.selectFirst("h3[class='text-primary-500 font-bold text-lg']").text();
        } catch (Exception e) {
            return "";
        }
    }

    public static Element getInfoStats(Element project) {
        return project.getElementsByClass("flex my-1").first();
    }

    public static String getDownloads(Element info_stats) {
        try {
            String downloads = info_stats.getElementsByClass("mr-2 text-xs text-gray-500").get(0).text();
            return downloads.substring(0, downloads.indexOf(" "));
        } catch (Exception e) {
            return "-";
        }
    }

    public static String getDate(Element info_stats) {
        try {
            String date = info_stats.getElementsByClass("mr-2 text-xs text-gray-500").get(1).text();
            return date.substring(date.indexOf(" ") + 1);
        } catch (Exception e) {
            return "-";
        }
    }

    public static String getDescription(Element project) {
        try {
            return project.getElementsByClass("text-sm leading-snug").first().select("p").first().text();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getHref(Element project) {
        return project.getElementsByClass("lg:flex items-end hidden").first().select("a").first().absUrl("href");
    }

    public static List<String> getCategories(Element project) {
        List<String> categories_list = new ArrayList<>();
        Elements categories = project.selectFirst("div[class='flex -mx-1']").select("div[class='px-1']");
        for (Element category : categories) {
            String link = category.selectFirst("a").absUrl("href");
            categories_list.add(link.substring(link.lastIndexOf('/') + 1));
        }

        return categories_list;
    }

    public static String getImageSize(String url) {
        try {
            BufferedImage img = ImageIO.read(new URL(url));
            return img.getWidth() + "x" + img.getHeight();
        } catch (IOException e) {
            return null;
        }
    }
}

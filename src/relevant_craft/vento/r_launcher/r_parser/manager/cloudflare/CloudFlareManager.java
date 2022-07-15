package relevant_craft.vento.r_launcher.r_parser.manager.cloudflare;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import relevant_craft.vento.r_launcher.r_parser.CF_ProjectURL;
import relevant_craft.vento.r_launcher.r_parser.manager.proxy.ProxyManager;
import relevant_craft.vento.r_launcher.r_parser.utils.Logger;
import relevant_craft.vento.r_launcher.r_parser.utils.PageUtils;
import relevant_craft.vento.r_launcher.r_parser.utils.TimeUtils;

import static relevant_craft.vento.r_launcher.r_parser.VENTO.CLOUDFLARE_UPDATE;
import static relevant_craft.vento.r_launcher.r_parser.manager.parser.CF_ParserManager.sleep;
import static relevant_craft.vento.r_launcher.r_parser.utils.PageUtils.activeCookies;
import static relevant_craft.vento.r_launcher.r_parser.utils.PageUtils.cookieManager;

public class CloudFlareManager {

    private static Thread pinger;
    private static boolean firstConnect = true;

    public static void initPinger() {
        pinger = new Thread(() -> {
            while (true) {
                sleep(1000 * 60 * CLOUDFLARE_UPDATE);

                authCloudflare();
            }
        });

        authCloudflare();

        pinger.start();
    }

    public static void stopPinger() {
        pinger.stop();
    }

    public static void authCloudflare() {
        if (firstConnect) {
            Logger.log("Establishing connection...");
        }
        long start = System.currentTimeMillis();
        WebClient client = new WebClient(BrowserVersion.CHROME);
        if (ProxyManager.hasProxy()) {
            client.getOptions().setProxyConfig(new ProxyConfig(ProxyManager.getProxy().getHost(), ProxyManager.getProxy().getPortInt()));
            if (ProxyManager.hasAuthentication()) {
                client.getCredentialsProvider().setCredentials(AuthScope.ANY, new NTCredentials(ProxyManager.getProxy().getUser(), ProxyManager.getProxy().getPassword(), "", ""));
            }
        }
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setRedirectEnabled(true);
        client.getCache().setMaxSize(0);
        if (cookieManager != null) {
            client.setCookieManager(cookieManager);
        }
//        client.waitForBackgroundJavaScript(1000);
//        client.setJavaScriptTimeout(1000);
//        client.waitForBackgroundJavaScriptStartingBefore(1000);
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

        try {
            Page page = client.getPage(PageUtils.lastUrl == null ? CF_ProjectURL.values()[0].getProject_url() : PageUtils.lastUrl);

            if (page instanceof HtmlPage) {
                if (((HtmlPage) page).getBody().asText().contains("Cloudflare")) {
                    synchronized (page) {
                        page.wait(6000);
                    }
                }
            }

            cookieManager = client.getCookieManager();
            activeCookies = System.currentTimeMillis() + 1024 * 60 * CLOUDFLARE_UPDATE;

            if (firstConnect) {
                firstConnect = false;
                Logger.log("Connection established (took " + TimeUtils.formatTime(System.currentTimeMillis() - start) + ").");
                Logger.emptyLog();
            }
        } catch (Exception ignored) {
            Logger.log("Connection error!");
        }
    }
}

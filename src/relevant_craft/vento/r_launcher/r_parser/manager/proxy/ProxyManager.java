package relevant_craft.vento.r_launcher.r_parser.manager.proxy;

import relevant_craft.vento.r_launcher.r_parser.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyManager {

    private static Proxy proxy = null;

    public static void initProxy() {
        proxy = loadProxy();
        if (proxy == null) {
            Logger.log("Proxy not configured.");
            Logger.emptyLog();
            return;
        }

        Logger.log("Connecting via proxy...");

        System.setProperty("https.proxyHost", proxy.getHost());
        System.setProperty("https.proxyPort", proxy.getPort());

        if (hasAuthentication()) {
            Authenticator.setDefault(
                    new Authenticator() {
                        @Override
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(proxy.getUser(), proxy.getPassword().toCharArray());
                        }
                    }
            );

            System.setProperty("https.proxyUser", proxy.getUser());
            System.setProperty("https.proxyPassword", proxy.getPassword());
        }

        Logger.log("Connection via proxy established.");
        Logger.emptyLog();
    }

    public static boolean hasProxy() {
        return proxy != null;
    }

    public static boolean hasAuthentication() {
        return proxy.getUser() != null && proxy.getPassword() != null;
    }

    public static Proxy getProxy() {
        return proxy;
    }

    private static Proxy loadProxy() {
        File proxy_cfg = new File("proxy");
        try (BufferedReader br = new BufferedReader(new FileReader(proxy_cfg))) {
            String line;
            Proxy proxy = new Proxy();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("host")) {
                    proxy.setHost(getStringFromCfg(line));
                } else if (line.startsWith("port")) {
                    proxy.setPort(getStringFromCfg(line));
                } else if (line.startsWith("user")) {
                    proxy.setUser(getStringFromCfg(line));
                } else if (line.startsWith("password")) {
                    proxy.setPassword(getStringFromCfg(line));
                }
            }

            return proxy;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getStringFromCfg(String line) {
        try {
            return line.split("=")[1];
        } catch (Exception e) {
            return null;
        }
    }
}

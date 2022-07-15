package relevant_craft.vento.r_launcher.r_parser;

import relevant_craft.vento.r_launcher.r_parser.manager.cloudflare.CloudFlareManager;
import relevant_craft.vento.r_launcher.r_parser.manager.ftp.FtpManager;
import relevant_craft.vento.r_launcher.r_parser.manager.parser.CF_ParserManager;
import relevant_craft.vento.r_launcher.r_parser.manager.proxy.ProxyManager;
import relevant_craft.vento.r_launcher.r_parser.manager.translator.TranslatorManager;
import relevant_craft.vento.r_launcher.r_parser.manager.version.Version;
import relevant_craft.vento.r_launcher.r_parser.manager.version.VersionManager;
import relevant_craft.vento.r_launcher.r_parser.utils.Logger;
import relevant_craft.vento.r_launcher.r_parser.utils.TimeUtils;

import java.io.File;
import java.util.HashMap;

public class VENTO {

    private static final String VERSION = "v2.2";

    public static final String[] ftp = new String[] { "host", "user", "pass" };

    public static final int CLOUDFLARE_UPDATE = 30;

    private static HashMap<CF_ProjectURL, Long> took_time = new HashMap<>();

    public static void main(String[] args) {
        Logger.log("**********************************"           );
        Logger.log("*         R-Parser " + VERSION + "          *");
        Logger.log("*  Powered by Relevant-Craft.SU  *"           );
        Logger.log("**********************************"           );

        long start_global = System.currentTimeMillis();

        ProxyManager.initProxy();

        TranslatorManager.initTranslatorCache();
//        WebGoogleTranslator.initWebTranslator();

        CloudFlareManager.initPinger();

        VersionManager.getVersions();

        for (CF_ProjectURL cf_project : CF_ProjectURL.values()) {
            long start_current = System.currentTimeMillis();

            for (Version version : VersionManager.versions) {
                CF_ParserManager.parseProject(cf_project, version.getName());
            }

            FtpManager ftpManager = new FtpManager();
            ftpManager.connect();
            File[] files = new File(cf_project.toString()).listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    ftpManager.uploadFile(file.getAbsolutePath(), file.getName(), cf_project.toString());
                }
            }
            ftpManager.disconnect();

            took_time.put(cf_project, (System.currentTimeMillis() - start_current));
        }

//        WebGoogleTranslator.stop();

//        FtpManager ftpManager = new FtpManager();
//        ftpManager.connect();
//        File[] files = DescriptionManager.PROJECT_DETAILS.listFiles();
//        if (files != null && files.length > 0) {
//            for (File file : files) {
//                ftpManager.uploadFile(file.getAbsolutePath(), file.getName(), DescriptionManager.PROJECT_DETAILS.getName());
//            }
//        }
//        ftpManager.disconnect();

        Logger.emptyLog();
        for (CF_ProjectURL cf_project : took_time.keySet()) {
            Logger.log("'" + cf_project.toString() + "'" + " time taken: " + TimeUtils.formatTime(took_time.get(cf_project)) + ".");
        }

        FtpManager ftpManager = new FtpManager();
        ftpManager.connect();
        ftpManager.createEmptyFile("Done");
        ftpManager.disconnect();

        CloudFlareManager.stopPinger();

        Logger.log("Total time taken: " + TimeUtils.formatTime(System.currentTimeMillis() - start_global) + ".");
    }
}

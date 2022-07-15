package relevant_craft.vento.r_launcher.r_parser.utils;

import relevant_craft.vento.r_launcher.r_parser.CF_ProjectURL;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    public static void checkDirectory(CF_ProjectURL cf_projectURL) {
        File dir = new File(cf_projectURL.toString());
        File old = new File(dir + "_old");

        if (old.exists()) {
            removeDirectory(old);
        }

        if (dir.exists()) {
            dir.renameTo(old);
        }
        dir.mkdir();

        Logger.emptyLog();
        Logger.log("Purged directory '" + cf_projectURL.toString() + "'.");
    }

    public static long getCreationTime(File file) {
        if (!file.exists()) {
            return 0;
        }

        try {
            Path p = Paths.get(file.getAbsolutePath());
            BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
            return view.creationTime().toMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean removeDirectory(File directory) {
        if (directory == null)
            return false;
        if (!directory.exists())
            return true;
        if (!directory.isDirectory())
            return false;

        String[] list = directory.list();

        if (list != null) {
            for (String file : list) {
                File entry = new File(directory, file);

                if (entry.isDirectory()) {
                    if (!removeDirectory(entry))
                        return false;
                } else {
                    if (!entry.delete())
                        return false;
                }
            }
        }

        return directory.delete();
    }
}

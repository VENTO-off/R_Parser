package relevant_craft.vento.r_launcher.r_parser.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private static final String LOGGER_FILE = getLoggerFile();

    public static void log(String log) {
        System.out.println("[R-Parser] " + log);
        logToFile(log);
    }

    public static void emptyLog() {
        System.out.println();
        logToFile(null);
    }

    private static void logToFile(String log) {
        File logs = new File("logs");
        if (!logs.exists()) { logs.mkdir(); }

        try (FileWriter writer = new FileWriter(logs + File.separator + LOGGER_FILE, true)) {
            if (log == null) {
                writer.append("\n");
            } else {
                writer.append("[R-Parser " + TimeUtils.getCurrentTime() + "] " + log + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getLoggerFile() {
        File logs = new File("logs");
        if (!logs.exists()) { logs.mkdir(); }

        int counter = 1;
        File log;
        do {
            log = new File(logs + File.separator + TimeUtils.getCurrentDate() + " #" + counter + ".log");
            counter++;
        } while (log.exists());

        return log.getName();
    }
}

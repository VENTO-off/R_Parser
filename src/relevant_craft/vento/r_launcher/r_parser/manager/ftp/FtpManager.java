package relevant_craft.vento.r_launcher.r_parser.manager.ftp;

import org.apache.commons.net.ftp.FTPClient;
import relevant_craft.vento.r_launcher.r_parser.VENTO;
import relevant_craft.vento.r_launcher.r_parser.utils.HashUtils;
import relevant_craft.vento.r_launcher.r_parser.utils.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpManager {

    private FTPClient client;
    private FileInputStream fis;
    private String currentDirectory;

    public FtpManager() {
        client = new FTPClient();
        fis = null;
        currentDirectory = null;
    }

    public void connect() {
        try {
            client.connect(VENTO.ftp[0]);
            client.login(VENTO.ftp[1], HashUtils.md5(VENTO.ftp[2]));
        } catch (IOException e) {
            Logger.log("Error while logging to FTP.");
        }
    }

    public void uploadFile(String localFile, String externalFile, String folder) {
        if (!new File(localFile).exists()) {
            return;
        }
        try {
            if (currentDirectory == null) {
                if (client.cwd(folder) == 550) {
                    client.makeDirectory(folder);
                }
                client.changeWorkingDirectory(folder);
                currentDirectory = folder;
            }
            client.storeFile(externalFile, new FileInputStream(localFile));
        } catch (IOException e) {
            Logger.log("Error while uploading file to FTP.");
        }
    }

    public void createEmptyFile(String name) {
        try {
            client.storeFile(name, new ByteArrayInputStream(new byte[0]));
        } catch (IOException e) {
            Logger.log("Error while creating file to FTP.");
        }
    }

    public void disconnect() {
        try {
            client.logout();
            if (fis != null) {
                fis = null;
            }
            client.disconnect();
        } catch (IOException e) {
            Logger.log("Error while disconnecting from FTP.");
        }
    }
}

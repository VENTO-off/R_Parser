package relevant_craft.vento.r_launcher.r_parser.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TarGzUtils {

    public static void extractTarGZ(File archive, File file) throws Exception {
        FileInputStream fileInput = new FileInputStream(archive);
        GzipCompressorInputStream gzipInput = new GzipCompressorInputStream(fileInput);
        TarArchiveInputStream tarInput = new TarArchiveInputStream(gzipInput);
        FileOutputStream fileOutput = new FileOutputStream(file);

        while (tarInput.getNextTarEntry() != null) {
            int length;
            byte[] data = new byte[1024];

            while ((length = tarInput.read(data, 0, data.length)) != -1) {
                fileOutput.write(data, 0, length);
            }
        }

        fileOutput.close();
        tarInput.close();
        gzipInput.close();
        fileInput.close();
    }

    public static void createTarGZ(File file, File archive) throws Exception {
        FileOutputStream fileOutput = new FileOutputStream(archive);
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
        GzipCompressorOutputStream gzipOutput = new GzipCompressorOutputStream(bufferedOutput);
        TarArchiveOutputStream tarArchiveOutput = new TarArchiveOutputStream(gzipOutput);

        TarArchiveEntry tarEntry = new TarArchiveEntry(file, file.getName());
        tarArchiveOutput.putArchiveEntry(tarEntry);
        tarArchiveOutput.write(IOUtils.toByteArray(new FileInputStream(file)));
        tarArchiveOutput.closeArchiveEntry();

        tarArchiveOutput.finish();
        tarArchiveOutput.close();
        gzipOutput.finish();
        gzipOutput.close();
        bufferedOutput.close();
        fileOutput.close();
    }
}

package ai.elimu.appstore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {

    /**
     * Move file to a new location
     *
     * @param src The source location of the file
     * @param dst The destination where the file should be moved to
     * @throws IOException if any I/O error occurs.
     */
    public static void moveFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        if(!dst.exists()){
            dst.createNewFile();
        }
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);

            //Delete source file once byte writing completes
            src.delete();
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

}

import objectsdetection.helpers.Helper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class TestHelper {

    public static final String IMAGES_SOURCE_DIR = "C:\\Users\\wenbo\\Desktop\\frame\\";
    public static final String IMAGES_DEST_DIR = Helper.getConfigProperties().get("TARGET_IMAGE_DIR") + "\\";
    public static final String LOGS_DEST_DIR = "logs\\";

    public static void copyAllImages(int s, int e) throws IOException {

        File source = new File(IMAGES_SOURCE_DIR);
        File dest = new File(IMAGES_DEST_DIR);

        FileUtils.copyDirectory(source, dest);

    }

    public static void copy25Images() {

        System.out.println("RUNNING copy25Image");

        List<File> files = (List<File>) FileUtils.listFiles(new File(IMAGES_SOURCE_DIR), null, true);
        int size = files.size();

        for (int i = 0; i < 25; i++) {
            File f = files.get(new Random().nextInt(size));
            String name = files.get(new Random().nextInt(size)).getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("\\") + 1);
            try {
                FileUtils.copyFile(files.get(new Random().nextInt(size)), new File(IMAGES_DEST_DIR + name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeExistingImages() {

        System.out.println("REMOVING EXISTING IMAGES");

        List<File> files = (List<File>) FileUtils.listFiles(new File(IMAGES_DEST_DIR), null, true);

        for (File f : files)
            System.out.println(f.toString() + " " + f.delete());


    }

    public static void removeExistingLogs() {

        System.out.println("REMOVING EXISTING LOGS");

        List<File> files = (List<File>) FileUtils.listFiles(new File(LOGS_DEST_DIR), null, true);

        for (File f : files)
            System.out.println(f.toString() + " " + f.delete());


    }

    public static void terminateVLC() {

        String cmd = "taskkill /IM vlc.exe /F";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;
import java.util.Random;

public class TestHelper {

    public static final String IMAGES_SOURCE_DIR = "C:\\Users\\wenbo\\Desktop\\frame\\";
    public static final String IMAGES_DEST_DIR = Helper.getConfigProperties().get("TARGET_IMAGE_DIR") + "\\";

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
            String name = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("\\") + 1);
            try {
                FileUtils.copyFile(files.get(new Random().nextInt(size)), new File(IMAGES_DEST_DIR + name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;

public class Producer implements Runnable {

    private final HashMap<String, String> config;
    private Path path;
    private List<String> imagesList;

    private String deliminator;

    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public Producer(List<String> imagesList) {
        this.config = Helper.getConfigProperties();
        this.imagesList = imagesList;
        this.deliminator = setDeliminator();
        this.path = Paths.get(config.get("TARGET_IMAGE_DIR"));
    }

    @Override
    public void run() {
        loadExistingImages();
        watchAndLoadImageDirectory();
    }

    public void loadExistingImages() {
        try {
            Files.newDirectoryStream(this.path)
                    .forEach(image -> {
                        String fileName = image.toString();
                        logger.info("Loading Existing File " + fileName);
                        if (checkValidFile(fileName))
                            imagesList.add(formatImageFilePath(fileName));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void watchAndLoadImageDirectory() {

        try {

            WatchService watchService = FileSystems.getDefault().newWatchService();

            WatchKey key = this.path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                WatchKey k = watchService.take();
                for (WatchEvent<?> e : k.pollEvents()) {

                    Object c = e.context();

                    logger.info(String.format("Putting New %s %d %s\n", e.kind(), e.count(), c));

                    String fileName = c.toString();
                    if (checkValidFile(fileName))
                        imagesList.add(formatImageFilePath(fileName));

                }
                k.reset();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String setDeliminator() {
        if (System.getProperty("os.name").startsWith("Windows"))
            return Constants.WINDOWS_DELI;
        else
            return Constants.LINUX_DELI;
    }


    public String formatImageFilePath(String filepath) {
        if (filepath.contains(config.get("TARGET_IMAGE_DIR")))
            return filepath;
        else {

            return (config.get("TARGET_IMAGE_DIR") + deliminator + filepath);
        }
    }

    public static boolean checkValidFile(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".png"))
            return true;
        return false;
    }
}

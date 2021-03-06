package objectsdetection;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;

import objectsdetection.helpers.Helper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Producer implements Runnable {

  private final HashMap<String, String> config;
  private Path path;
  private List<String> imagesList;

  private String deliminator;

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  /**
   * A Producer is for watching the image directory
   * and put the new generated images' name into the sync list.
   *
   * @param imagesList the shared sync list
   */
  public Producer(List<String> imagesList) {
    this.config = Helper.getConfigProperties();
    this.imagesList = imagesList;
    this.deliminator = Helper.getDeliminator();
    this.path = Paths.get(config.get("TARGET_IMAGE_DIR"));
  }

  @Override
  public void run() {
    removeExistingImageFiles();
    watchAndLoadImageDirectory();
  }

  /**
   * Put existing images into the Synchronized list.
   */
  private void removeExistingImageFiles() {

    List<File> files = (List<File>) FileUtils.listFiles(new File(config.get("TARGET_IMAGE_DIR")), null, true);
    for (File f : files)
      f.delete();

  }

  /**
   * Watch the target directory;
   * If a new entry (new image file) generated, put the file name into the Synchronized list.
   */
  private void watchAndLoadImageDirectory() {

    try {

      WatchService watchService = FileSystems.getDefault().newWatchService();

      WatchKey key = this.path.register(watchService,
              StandardWatchEventKinds.ENTRY_CREATE);

      while (true) {
        WatchKey k = watchService.take();
        for (WatchEvent<?> e : k.pollEvents()) {

          Object c = e.context();

          String fileName = c.toString();
          if (checkValidFile(fileName)) {
            logger.debug(String.format("Putting New %s %d %s %n", e.kind(), e.count(), c));
            imagesList.add(formatImageFilePath(fileName));
          }


        }
        k.reset();
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }


  private String formatImageFilePath(String filepath) {
    if (filepath.contains(config.get("TARGET_IMAGE_DIR"))) {
      return filepath;
    } else {
      return (config.get("TARGET_IMAGE_DIR") + deliminator + filepath);
    }
  }

  private static boolean checkValidFile(String fileName) {
    if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
      return true;
    }
    return false;
  }
}

package objectsdetection.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import objectsdetection.notifications.I_Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Helper {

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  /**
   * Parse the config.properties to a HashMap
   *
   * @return a HashMap of config.properties
   */
  public static HashMap<String, String> getConfigProperties() {

    Properties configProp = new Properties();

    try (FileInputStream configFile = new FileInputStream(Constants.CONFIG_FILE_PATH)) {

      configProp.load(configFile);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return new HashMap<String, String>((Map) configProp);

  }


  /**
   * Check the OS is Windows or not.
   *
   * @return true for Windows; false for non-Windows
   */
  public static boolean isWindows() {
    if (System.getProperty("os.name").startsWith("Windows")) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Get deliminator based on the OS.
   *
   * @return double backslash for windows; forward slash for non-windows.
   */
  public static String getDeliminator() {
    if (isWindows()) {
      return Constants.WINDOWS_DELI;
    } else {
      return Constants.LINUX_DELI;
    }
  }

  /**
   * Check the config.properties and init the notification systems that user selected.
   *
   * @return an ArrayList of initiated user selected notification systems
   */
  public static ArrayList<I_Notification> getNotificationServices() {

    String input = Helper.getConfigProperties().get("NOTIFICATION_SYSTEMS").toUpperCase();

    ArrayList<I_Notification> notification = new ArrayList<I_Notification>();

    try {
      if (input.contains("TWILIO")) {
        notification.add((I_Notification) Class.forName(Constants.TWILIO).newInstance());
      }

      if (input.contains("AWS_SNS")) {
        notification.add((I_Notification) Class.forName(Constants.AWS_SNS).newInstance());
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return notification;

  }

  /**
   * Delete image.
   *
   * @param imageFilePath file path to the image that will be deleting
   */
  public static void deleteImage(String imageFilePath) {
    logger.info("Helper Deleting " + imageFilePath);
    Path p = Paths.get(imageFilePath);
    try {
      Files.delete(p);
    } catch (IOException e) {
      logger.error("Helper Deleting " + e);
    }
    /*
    logger.info("Helper Deleting " + imageFilePath);
    File f = new File(imageFilePath);
    f.delete();
    */
  }


}

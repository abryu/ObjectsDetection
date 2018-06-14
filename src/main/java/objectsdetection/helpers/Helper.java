package objectsdetection.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.jcraft.jsch.*;
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
    logger.debug("Helper Deleting " + imageFilePath);
    Path p = Paths.get(imageFilePath);
    try {
      Files.delete(p);
    } catch (IOException e) {
      logger.error("Helper Deleting " + e);
    }
    /*
    logger.debug("Helper Deleting " + imageFilePath);
    File f = new File(imageFilePath);
    f.delete();
    */
  }

  public static boolean runSshCommandOnPi(String command) throws JSchException, IOException {


    HashMap<String, String> configMap = getConfigProperties();

    String host = configMap.get("PI_IP_ADDRESS");
    String user = configMap.get("PI_USERNAME");
    String password = configMap.get("PI_PASSWORD");

    logger.debug("Running " + command + " on host " + host);

    java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    JSch jsch = new JSch();
    Session session = jsch.getSession(user, host, 22);
    session.setPassword(password);
    session.setConfig(config);
    session.connect();
    logger.debug("Connected to " + host);

    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(command);
    channel.connect();

    boolean success = false;
    // get stdout
    InputStream in = channel.getInputStream();
    byte[] tmp = new byte[1024];
    while (true) {
      while (in.available() > 0) {
        int i = in.read(tmp, 0, 1024);
        if (i < 0)
          break;
        logger.debug(new String(tmp, 0, i));
      }
      success = channel.getExitStatus() == 0;
      logger.debug("SSH Command Status " + success);
      if (channel.isClosed()) {
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (Exception ee) {
      }
    }
    channel.disconnect();
    session.disconnect();

    return success;

  }


}

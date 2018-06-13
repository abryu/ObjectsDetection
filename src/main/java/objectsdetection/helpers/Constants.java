package objectsdetection.helpers;

import java.util.ArrayList;

import objectsdetection.notifications.I_Notification;


public class Constants {

  // Application Related Parameters
  public static final String CONFIG_FILE_PATH = "config/config.properties";

  // OS Related
  public static final String LINUX_DELI = "/";
  public static final String WINDOWS_DELI = "\\";


  // Watson VR
  public static final String WATSON_VR_VERSION_DATE = "2018-03-19";


  // Notifications
  public static final String PACKAGE_PREFIX = "objectsdetection.notifications.";
  public static final String TWILIO = PACKAGE_PREFIX + "TwilioSms";
  public static final String AWS_SNS = PACKAGE_PREFIX + "AwsSnsNotification";

  public static final ArrayList<I_Notification> NOTIFICATIONS = Helper.getNotificationServices();


  // Video Streaming and Splitting
  public static final String STREAMING_PORT = "8554";
  public static final String IMAGE_EXTENSION = "jpg";
  public static final String IMAGE_PREFIX = "t";
  public static final String SPLITTING_RATION = "1";


}

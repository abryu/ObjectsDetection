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

  // Pi's Command
  public static final String VIDEO_STREAMING_CHECKING = "pgrep raspivid && pgrep vlc";
  public static final String VIDEO_STREAMING_STARTING = ". .profile; nohup raspivid -o - -t 0 -hf -w 640 -h 360 -fps 25 -op 10 | cvlc -vvv stream:///dev/stdin --sout '#rtp{sdp=rtsp://:8554}' :demux=h264 > /dev/null 2>&1 &";
  public static final String KILL_RASPIVID = "ps -ef | grep raspivid | grep -v grep | awk '{print $2}' | xargs kill";
  public static final String KILL_VLC = "ps -ef | grep vlc | grep -v grep | awk '{print $2}' | xargs kill";

}

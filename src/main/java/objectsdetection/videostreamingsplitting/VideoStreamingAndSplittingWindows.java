package objectsdetection.videostreamingsplitting;

import java.io.IOException;
import java.util.HashMap;

import objectsdetection.helpers.Constants;
import objectsdetection.helpers.Helper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class VideoStreamingAndSplittingWindows implements Runnable, I_VideoStreamingAndSplitting {

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  @Override
  public void run() {
    streamingVideo();
  }

  /**
   * Geneate shell command for starting video streaming and splitting.
   *
   * @return the command for starting video streaming and splitting
   */
  public String generateCommand() {
    HashMap<String, String> config = Helper.getConfigProperties();
    StringBuilder sb = new StringBuilder();

    String vlcHomePath = config.get("LOCAL_VLC_HOME_DIR");
    String ipAddress = config.get("PI_IP_ADDRESS");
    String destinationPath = config.get("TARGET_IMAGE_DIR");

    sb.append(vlcHomePath)
            .append("\\vlc.exe rtsp://")
            .append(ipAddress)
            .append(":" + Constants.STREAMING_PORT + "/ --video-filter scene --scene-format ")
            .append(Constants.IMAGE_EXTENSION)
            .append(" --scene-prefix ")
            .append(Constants.IMAGE_PREFIX)
            .append(" --scene-path ")
            .append(destinationPath)
            .append(" --no-scene-replace --scene-ratio ")
            .append(Constants.SPLITTING_RATION)
    ;

    logger.debug(sb);
    return sb.toString();
  }

  @Override
  public void streamingVideo() {
    try {
      Runtime.getRuntime().exec(generateCommand());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void killSteamingVideo() {
    try {
      Runtime.getRuntime().exec(Constants.KILL_VLC_WIN);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}

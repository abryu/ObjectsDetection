package objectsdetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jcraft.jsch.JSchException;
import objectsdetection.helpers.Constants;
import objectsdetection.helpers.Helper;
import objectsdetection.videostreamingsplitting.I_VideoStreamingAndSplitting;
import objectsdetection.videostreamingsplitting.VideoStreamingAndSplittingLinux;
import objectsdetection.videostreamingsplitting.VideoStreamingAndSplittingWindows;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller {

  private List<String> imagesList;
  private final HashMap<String, String> configProp;

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  /**
   * 1. Read and Store the configuration file to a HashMap.
   * 2. Create a Synchronized List that shared by Producer and Consumer.
   */
  public Controller() {
    this.configProp = Helper.getConfigProperties();
    this.imagesList = Collections.synchronizedList(new ArrayList<String>());
  }

  /**
   * Run threads of Video Streaming and Splitting, Producer, and Consumer.
   */
  public void run() {
    if (runVideoStreamingOnPi()) {
      createAndRunVideoStreaming();
      createAndRunProducer();
      createAndRunConsumer();
    } else {
      logger.error("Pi Video Streaming Cannot be started; Quit");
      System.exit(1);
    }

  }

  /**
   * Create and Run Video Streaming based on the Operating System Type.
   */
  private void createAndRunVideoStreaming() {
    logger.info("Starting Video Streaming ...");

    ExecutorService steamer = Executors.newSingleThreadExecutor();
    I_VideoStreamingAndSplitting runner = null;

    if (Helper.isWindows()) {
      runner = new VideoStreamingAndSplittingWindows();
    } else {
      runner = new VideoStreamingAndSplittingLinux();
    }

    steamer.execute((Runnable) runner);
    steamer.shutdown();

  }

  /**
   * Create and Run a Producer thread.
   * Use the shared Synchronized list.
   */
  private void createAndRunProducer() {
    logger.info("Starting Producer ...");
    ExecutorService producer = Executors.newSingleThreadExecutor();
    producer.execute(new Producer(imagesList));
    producer.shutdown();
  }

  /**
   * Create and Run a Consumer thread.
   * Use the shared Synchronized list.
   */
  private void createAndRunConsumer() {
    logger.info("Starting Consumer ...");
    ExecutorService consumer = Executors.newSingleThreadExecutor();
    consumer.execute(new Consumer(imagesList));
    consumer.shutdown();
  }

  /**
   * Check Pi to see if Video is Streaming.
   * If true, do nothing.
   * If false, start streaming
   */
  private boolean runVideoStreamingOnPi() {

    logger.info("Check Pi to see if Video is Streaming");

    try {
      if (Helper.runSshCommandOnPi(Constants.VIDEO_STREAMING_CHECKING))
        return true;
      else {
        logger.info("Pi is not video streaming; Trying to start.");
        Helper.runSshCommandOnPi(Constants.KILL_VLC);
        Helper.runSshCommandOnPi(Constants.KILL_RASPIVID);
        return Helper.runSshCommandOnPi(Constants.VIDEO_STREAMING_STARTING);
      }
    } catch (JSchException | IOException e) {
      logger.error(e);
    }

    return false;
  }


}


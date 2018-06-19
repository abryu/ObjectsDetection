package objectsdetection.processors;

import com.ibm.watson.developer_cloud.service.exception.NotFoundException;
import com.ibm.watson.developer_cloud.service.exception.ServiceResponseException;
import com.ibm.watson.developer_cloud.service.exception.TooManyRequestsException;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;
import objectsdetection.helpers.Constants;

import objectsdetection.helpers.Helper;
import objectsdetection.notifications.I_Notification;
import objectsdetection.videostreamingsplitting.I_VideoStreamingAndSplitting;
import objectsdetection.videostreamingsplitting.VideoStreamingAndSplittingLinux;
import objectsdetection.videostreamingsplitting.VideoStreamingAndSplittingWindows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WatsonProcessor implements Runnable, I_Processor {

  String fileName;
  VisualRecognition service;
  HashMap<String, String> configProp;
  List<I_Notification> notification;

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  /**
   * Use IBM Cloud Watson Service to detect objects.
   *
   * @param fileName file path for the image that will be classifying
   * @param service  Watson Server that has been setApiKey
   */
  public WatsonProcessor(String fileName, VisualRecognition service) {
    this.fileName = fileName;
    this.service = service;
    this.configProp = Helper.getConfigProperties();
    this.notification = Constants.NOTIFICATIONS;
  }

  @Override
  public void run() {
    try {
      classifyAnImage(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void classifyAnImage(Object inputDataType) {

    InputStream input = (InputStream) inputDataType;

    /*
    logger.info(String.format(
            "Faking : Classifying %s on Thread %s \n", fileName.toString(), Thread.currentThread().getName()));

    try {
      Thread.sleep(4000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
*/

    logger.info(String.format("Classifying %s on Thread %s \n", fileName.toString(), Thread.currentThread().getName()));

    ClassifyOptions classifyOptions;
    ClassifiedImages result = null;

    try {
      // Invoke a Visual Recognition method
      classifyOptions = new ClassifyOptions.Builder()
              .imagesFile(input).imagesFilename(fileName.toString())
              .threshold(Float.parseFloat(configProp.get("THRESHOLD")))
              .owners(Arrays.asList(configProp.get("OWNER")))
              .build();
      result = service.classify(classifyOptions).execute();
    } catch (NotFoundException e) {
      // Handle Not Found (404) exception
      logger.error(e);
      System.out.println("Handle Not Found (404) exception ; Service returned status code " + e.getStatusCode() + ": " + e.getMessage());
    } catch (TooManyRequestsException e) {
      // Handle Request Too Large (413) exception
      logger.error("TooManyRequestsException : " + e);
      System.out.println("Too Many Request exception ; Service returned status code " + e.getStatusCode() + ": " + e.getMessage() + " QUITTING");

      I_VideoStreamingAndSplitting killer;
      if (Helper.isWindows()) {
        killer = new VideoStreamingAndSplittingWindows();
      } else {
        killer = new VideoStreamingAndSplittingLinux();
      }

      logger.debug("Killing VLC process in local");
      killer.killSteamingVideo();

      System.exit(1);

    } catch (ServiceResponseException e) {
      // Base class for all exceptions caused by error responses from the service
      logger.error(e);
      System.out.println("Base class for all exceptions ; Service returned status code " + e.getStatusCode() + ": " + e.getMessage());
    }

    if (result == null)
      parseResult(result);

    try {
      input.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Helper.deleteImage(fileName);

  }

  @Override
  public void parseResult(Object resultDataType) {

    ClassifiedImages resultToParse = (ClassifiedImages) resultDataType;


    logger.info("----------- Classification Result -----------");

    resultToParse.getImages().forEach(classifiedImage -> {

              classifiedImage.getClassifiers().forEach(classifier -> {

                logger.info(classifier.getClasses());

                for (I_Notification i : notification) {

                  i.notifyUsers(classifier.getClasses().toString());

                }
              });
            }
    );

    logger.info("----------------------------------------------");

  }


}

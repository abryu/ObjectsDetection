package objectsdetection.processors;

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
    ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
            .imagesFile(input).imagesFilename(fileName.toString())
            .threshold(Float.parseFloat(configProp.get("THRESHOLD")))
            .owners(Arrays.asList(configProp.get("OWNER")))
            .build();
    ClassifiedImages result = service.classify(classifyOptions).execute();


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

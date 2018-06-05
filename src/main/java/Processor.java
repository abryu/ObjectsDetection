import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class Processor implements Runnable {

    String fileName;
    VisualRecognition service;
    HashMap<String, String> configProp;

    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public Processor(String fileName, VisualRecognition service, HashMap<String, String> configProp) {
        this.fileName = fileName;
        this.service = service;
        this.configProp = configProp;
    }

    @Override
    public void run() {
        try {
            classifyAnImage(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void classifyAnImage(InputStream input) {

        logger.info(String.format("Classifying %s on Thread %s \n", fileName.toString(), Thread.currentThread().getName()));

        ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                .imagesFile(input).imagesFilename(fileName.toString())
                .threshold(Float.parseFloat(configProp.get("THRESHOLD")))
                .owners(Arrays.asList(configProp.get("OWNER")))
                .build();
        ClassifiedImages result = service.classify(classifyOptions).execute();

        parseResult(result);

        deleteImage(fileName);

    }


    public void parseResult(ClassifiedImages resultToParse) {

        logger.info("----------- Classification Result -----------");

        resultToParse.getImages().forEach(classifiedImage -> {
                    classifiedImage.getClassifiers().forEach(classifier -> {

                        logger.info(classifier.getClasses());

                        // if className = People, do some notification things

                    });
                }
        );

        logger.info("----------------------------------------------");

    }

    private void deleteImage(String imageFileName) {
        logger.debug("Deleting " + imageFileName);
        File f = new File(imageFileName);
        f.delete();
    }
}

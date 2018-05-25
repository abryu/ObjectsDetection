import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

public class ImageClassification {

    private HashMap<String, String> configProp;
    private VisualRecognition service;

    public ImageClassification(HashMap<String, String> configProp) {
        this.configProp = configProp;
        this.service = new VisualRecognition(Constants.WATSON_VR_VERSION_DATE);
        this.service.setApiKey(this.configProp.get("WATSON_VR_API_KEY"));
    }

    public void classifyAnImage(InputStream input) {

        ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                .imagesFile(input).imagesFilename("Image.jpg")
                .threshold(Float.parseFloat(configProp.get("THRESHOLD")))
                .owners(Arrays.asList(configProp.get("OWNER")))
                .build();
        ClassifiedImages result = service.classify(classifyOptions).execute();

        parseResult(result);

    }

    public void parseResult(ClassifiedImages resultToParse) {

        resultToParse.getImages().forEach(classifiedImage -> {
                    classifiedImage.getClassifiers().forEach(classifier -> {

                        System.out.println(classifier.getClasses());

                        // if className = People, do some notification things

                    });
                }
        );

    }
}

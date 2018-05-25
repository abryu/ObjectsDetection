import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class ImageProcessor {

    private HashMap<String, String> configProp;
    private ImageClassification service;

    public ImageProcessor(HashMap<String, String> configProp) {
        this.configProp = configProp;
        this.service = new ImageClassification(this.configProp);
    }

    public void processAnImage(String imagePath) {
        try {
            Thread.sleep(1000); // Avoiding file is being writing but the classify service starts reading this file
            service.classifyAnImage(new FileInputStream(imagePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        deleteProcessedImage(imagePath);
    }

    public void deleteProcessedImage(String imagePath) {
        System.out.println("Deleting " + imagePath);
        File f = new File(imagePath);
        f.delete();
    }


}

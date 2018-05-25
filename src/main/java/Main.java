import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Main {

    private static HashMap<String, String> configProp;

    public static void main(String[] args) {
        configProp = getConfigProperties();
        ImageFileNameQueue imageFileNameQueue = new ImageFileNameQueue(configProp);
        imageFileNameQueue.run();
    }

    private static HashMap<String, String> getConfigProperties() {

        Properties configProp = new Properties();

        try (FileInputStream configFile = new FileInputStream(Constants.CONFIG_FILE_PATH)) {

            configProp.load(configFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<String, String>((Map) configProp);

    }

}

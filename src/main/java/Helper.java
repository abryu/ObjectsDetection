import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Helper {

    public static HashMap<String, String> getConfigProperties() {

        Properties configProp = new Properties();

        try (FileInputStream configFile = new FileInputStream(Constants.CONFIG_FILE_PATH)) {

            configProp.load(configFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<String, String>((Map) configProp);

    }

}

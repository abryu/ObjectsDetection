import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Controller {

    private List<String> imagesList;
    private final HashMap<String, String> configProp;

    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public Controller() {
        this.configProp = Helper.getConfigProperties();
        this.imagesList = Collections.synchronizedList(new ArrayList<String>());
    }

    public void run() {
        createAndRunProducer();
        createAndRunConsumer();
    }

    private void createAndRunProducer() {
        logger.info("Starting Producer ...");
        ExecutorService producer = Executors.newSingleThreadExecutor();
        producer.execute(new Producer(imagesList));
        producer.shutdown();
    }

    private void createAndRunConsumer() {
        logger.info("Starting Consumer ...");
        ExecutorService consumer = Executors.newSingleThreadExecutor();
        consumer.execute(new Consumer(imagesList));
        consumer.shutdown();
    }


}


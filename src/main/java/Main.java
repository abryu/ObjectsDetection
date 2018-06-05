import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.*;
import java.util.concurrent.*;


public class Main {

    private static HashMap<String, String> configProp;

    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        logger.info("Starting Main ...");

        Controller controller = new Controller();
        controller.run();

    }



}

package objectsdetection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  /**
   * Java Main
   * Create and Start a Controller.
   * @param args cmd arguments
   */
  public static void main(String[] args) {

    logger.info("Starting Main ...");

    Controller controller = new Controller();
    controller.run();

  }


}

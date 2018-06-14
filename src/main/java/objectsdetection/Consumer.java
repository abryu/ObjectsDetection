package objectsdetection;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import objectsdetection.helpers.Constants;
import objectsdetection.helpers.Helper;
import objectsdetection.processors.WatsonProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Consumer implements Runnable {

  HashMap<String, String> config;
  List<String> imagesList;
  VisualRecognition service;

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  /**
   * A consumer works for:
   * 1. Tracking the sync list.
   * 2. Create and check the status of threads.
   * 3. Once all threads are available, split the sync list into Number_Of_Thread part.
   * 4. Generate a random number for each part.
   * 5. Submit a thread task (run a processor thread) for each part with the random number index.
   * 6. Clear the sync list.
   * 7. Remove the processed images.
   *
   * @param imagesList shared sync list
   */
  public Consumer(List<String> imagesList) {
    this.config = Helper.getConfigProperties();
    this.imagesList = imagesList;
  }

  @Override
  public void run() {

    prepareVisualRecognitionService();

    int numOfThreads = Integer.parseInt(config.get("THREAD"));

    Executor cleanerPool = Executors.newSingleThreadExecutor();

    ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(numOfThreads, numOfThreads,
                    0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    List<Future<Integer>> threadStatus = new ArrayList<Future<Integer>>();

    while (true) {

      boolean threadsAvailable = checkThreadsCompletion(threadStatus);

      if (threadsAvailable && imagesList.size() >= numOfThreads) {

        int numOfImagesToProcess;
        int sizeOfDividedPart;
        int initSize;

        synchronized (imagesList) {

          numOfImagesToProcess = imagesList.size();
          sizeOfDividedPart = numOfImagesToProcess / numOfThreads;
          initSize = 0;

          for (int i = 0; i < numOfThreads; i++) {

            int startIndex = initSize;
            int endIndex = initSize + sizeOfDividedPart;
            int randomIndex;

            String filePathToProcess;

            try {
              randomIndex = ThreadLocalRandom.current().nextInt(startIndex, endIndex);
              filePathToProcess = imagesList.get(randomIndex);
            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
              logger.error(e);
              logger.debug("Exception: Use the end index of this divided part");
              randomIndex = endIndex;
              filePathToProcess = imagesList.get(randomIndex);
            }

            imagesList.set(randomIndex, null);

            logger.debug(String.format("StartIndex %d, EndIndex %d, RandomIndex %d (File Name %s), ListSize %d, eachPart %d %n",
                    startIndex, endIndex, randomIndex, filePathToProcess, numOfImagesToProcess, sizeOfDividedPart));

            Future f = threadPool.submit(new WatsonProcessor(filePathToProcess, service));
            threadStatus.add(f);

            initSize = endIndex;

          }
        }

        logger.debug(String.format("Cleaning List from %d to %d %n", 0, initSize));

        List<String> imagesToClean = new ArrayList<String>(imagesList.subList(0, numOfImagesToProcess));

        imagesList.subList(0, numOfImagesToProcess).clear();

        cleanerPool.execute(new Runnable() {

          @Override
          public void run() {
            for (String image : imagesToClean) {
              if (image != null) {
                deleteImage(image);
              }
            }
          }

          private void deleteImage(String imageFileName) {

            //logger.info("Deleting " + imageFileName + " " + new File(imageFileName).delete());

            logger.debug("Consumer Deleting " + imageFileName);
            Path p = Paths.get(imageFileName);
            try {
              Files.delete(p);
            } catch (IOException e) {
              logger.error("Consumer Deleting" + e);
            }
          }

        });

      }

    }

  }

  private void prepareVisualRecognitionService() {
    this.service = new VisualRecognition(Constants.WATSON_VR_VERSION_DATE);
    this.service.setApiKey(this.config.get("WATSON_VR_API_KEY"));
  }

  private boolean checkThreadsCompletion(List<Future<Integer>> futureList) {
    for (Future<Integer> element : futureList) {
      if (element != null && !element.isDone()) {
        return false;
      }
    }
    futureList.clear();
    return true;
  }


}

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.sun.org.apache.xerces.internal.xs.StringList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class Consumer implements Runnable {

    private final HashMap<String, String> config;
    private List<String> imagesList;
    private VisualRecognition service;

    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public Consumer(List<String> imagesList) {
        this.config = Helper.getConfigProperties();
        this.imagesList = imagesList;
    }

    @Override
    public void run() {

        prepareVisualRecognitionService();

        int numOfThreads = Integer.parseInt(config.get("THREAD"));

        Executor cleanerPool = Executors.newFixedThreadPool(1);

        ThreadPoolExecutor threadPool =
                new ThreadPoolExecutor(numOfThreads, numOfThreads,
                        0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        List<Future<Integer>> threadStatus = new ArrayList<Future<Integer>>();


        while (true) {

            boolean threadsAvailable = checkThreadsCompletion(threadStatus);

            //logger.info(String.format("All Threads are AVAILABLE %b \n", threadsAvailable));

            if (threadsAvailable && imagesList.size() > 0) {

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
                            logger.error(e.toString());
                            logger.error("IndexOutOfBoundsException, Use the end index of this divided part");
                            randomIndex = endIndex;
                            filePathToProcess = imagesList.get(randomIndex);
                        }

                        logger.info(String.format("StartIndex %d, EndIndex %d, RandomIndex %d (File Name %s), ListSize %d, eachPart %d \n",
                                startIndex, endIndex, randomIndex, filePathToProcess, numOfImagesToProcess, sizeOfDividedPart));

                        Future f = threadPool.submit(new Processor(filePathToProcess, service, config));
                        threadStatus.add(f);

                        initSize = endIndex;

                    }
                }

                logger.debug(String.format("Cleaning List from %d to %d \n", 0, initSize));

                List<String> imagesToClean = new ArrayList<String>(imagesList.subList(0, numOfImagesToProcess));

                imagesList.subList(0, numOfImagesToProcess).clear();

                cleanerPool.execute(new Runnable() {

                    @Override
                    public void run() {
                        imagesToClean.forEach(this::deleteImage);
                    }

                    private void deleteImage(String imageFileName) {
                        logger.debug("Deleting " + imageFileName);
                        File f = new File(imageFileName);
                        f.delete();
                    }

                });

            }


        }

    }

    private void prepareVisualRecognitionService() {
        this.service = new VisualRecognition(Constants.WATSON_VR_VERSION_DATE);
        this.service.setApiKey(this.config.get("WATSON_VR_API_KEY"));
    }

    private boolean checkThreadsCompletion(List<Future<Integer>> tList) {
        for (Future<Integer> element : tList) {
            if (element != null && !element.isDone())
                return false;
        }
        tList.clear();
        return true;
    }


}

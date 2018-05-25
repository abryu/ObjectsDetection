import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {

    protected BlockingQueue imageNameQueue = null;
    private ImageProcessor imageProcessor;

    public Consumer(BlockingQueue queue, HashMap<String, String> config) {
        this.imageNameQueue = queue;
        this.imageProcessor = new ImageProcessor(config);
    }

    @Override
    public void run() {

        try {

            while (true) {

                String fileName = imageNameQueue.take().toString();

                System.out.println("Processing " + fileName);

                imageProcessor.processAnImage(fileName);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

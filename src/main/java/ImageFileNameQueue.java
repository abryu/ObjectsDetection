import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ImageFileNameQueue {

    private BlockingQueue queue;
    private HashMap<String, String> configProp;

    public ImageFileNameQueue(HashMap<String, String> configProp) {
        this.configProp = configProp;
        this.queue = new ArrayBlockingQueue(Integer.parseInt(configProp.get("QUEUE_CAP")));
    }

    public void run() {

        Producer producer = new Producer(queue, configProp);
        Consumer consumer = new Consumer(queue, configProp);

        new Thread(producer).start();

        for (int i = 0; i < Integer.parseInt(configProp.get("THREAD")); i++)
            new Thread(consumer).start();

    }


}

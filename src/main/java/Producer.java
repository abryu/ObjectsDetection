import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {

    protected BlockingQueue queue;
    HashMap<String, String> config;
    Path path;

    public Producer(BlockingQueue queue, HashMap<String, String> config) {
        this.queue = queue;
        this.config = config;
        this.path = Paths.get(config.get("TARGET_IMAGE_DIR"));
    }

    @Override
    public void run() {

        loadExistingImages();
        watchImageDirectory();

    }

    public void loadExistingImages() {

        try {
            Files.newDirectoryStream(this.path)
                    .forEach(image -> {
                        try {
                            String fileName = image.toString();
                            if (checkValidFile(fileName))
                                queue.put(fileName);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkValidFile(String fileName) {

        if (fileName.endsWith(".jpg") || fileName.endsWith(".png"))
            return true;
        return false;

    }

    public void watchImageDirectory() {

        try {

            WatchService watchService = FileSystems.getDefault().newWatchService();

            WatchKey key = this.path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                WatchKey k = watchService.take();
                for (WatchEvent<?> e : k.pollEvents()) {

                    Object c = e.context();

                    System.out.printf("%s %d %s\n", e.kind(), e.count(), c);

                    String fileName = c.toString();
                    if (checkValidFile(fileName))
                        queue.put(config.get("TARGET_IMAGE_DIR") + "\\" + c);

                }
                k.reset();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

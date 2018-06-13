import net.jodah.concurrentunit.Waiter;
import objectsdetection.Controller;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ControllerTest {

    private final static int SECOND_TO_RUN = 60;

    @BeforeClass
    public static void removeExistingFiles() {
        TestHelper.removeExistingImages();
        TestHelper.removeExistingLogs();
    }

    @Test
    public void testController() throws Throwable {

        final Waiter waiter = new Waiter();

        /*
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(800);
                    TestHelper.copy25Images();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        */

        new Thread(() -> {
            new Controller().run();
        }).start();

        waiter.await(SECOND_TO_RUN * 1000);

    }

    @AfterClass
    public static void terminateVLC() {

        TestHelper.terminateVLC();

    }


}

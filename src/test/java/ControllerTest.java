import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

public class ControllerTest {

    private final static int SECOND_TO_RUN = 15;

    @Test
    public void testController() throws Throwable {

        final Waiter waiter = new Waiter();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    TestHelper.copy25Images();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        new Thread(() -> {
            new Controller().run();
        }).start();

        waiter.await(SECOND_TO_RUN * 1000);

    }
}

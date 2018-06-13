import net.jodah.concurrentunit.Waiter;
import objectsdetection.videostreamingsplitting.VideoStreamingAndSplittingWindows;
import org.junit.Test;

public class WindowsRunnerTest {

    @Test
    public void testProcessor() throws Throwable {

        final Waiter waiter = new Waiter();

        Thread t = new Thread(new VideoStreamingAndSplittingWindows());
        t.start();

        waiter.await(5000);

        t.interrupt();


    }

}

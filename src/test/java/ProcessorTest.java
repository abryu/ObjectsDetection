import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

import java.util.HashMap;

public class ProcessorTest {

    private final static int SECOND_TO_RUN = 10;

    @Test
    public void testProcessor() throws Throwable {

        HashMap<String, String> conf = Helper.getConfigProperties();
        VisualRecognition service = new VisualRecognition(Constants.WATSON_VR_VERSION_DATE);
        service.setApiKey(conf.get("WATSON_VR_API_KEY"));

        final Waiter waiter = new Waiter();

        new Thread(() -> {
            new Processor("Sample\\test00059.jpg", service, conf).run();
        }).start();

        waiter.await(SECOND_TO_RUN * 1000);

    }

}

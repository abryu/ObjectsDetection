import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import net.jodah.concurrentunit.Waiter;
import objectsdetection.helpers.Helper;
import objectsdetection.processors.WatsonProcessor;
import org.junit.Test;

import java.util.HashMap;

public class ProcessorTest {

    private final static int SECOND_TO_RUN = 10;

    @Test
    public void testProcessor() throws Throwable {

        HashMap<String, String> conf = Helper.getConfigProperties();
        VisualRecognition service = new VisualRecognition("2018-03-19");
        service.setApiKey(conf.get("WATSON_VR_API_KEY"));

        final Waiter waiter = new Waiter();

        new Thread(() -> {
            new WatsonProcessor("Sample\\t00028.jpg", service).run();
        }).start();

        waiter.await(SECOND_TO_RUN * 1000);

    }

}

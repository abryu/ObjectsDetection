import com.jcraft.jsch.JSchException;
import objectsdetection.helpers.Constants;
import objectsdetection.helpers.Helper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

public class SshCommandTest {

  @Test
  public void testSshCommand() {
    boolean finalResult = false;
    try {
      finalResult = Helper.runSshCommandOnPi(Constants.VIDEO_STREAMING_CHECKING);
      if (finalResult) {
        assertEquals(true, Helper.runSshCommandOnPi(Constants.KILL_RASPIVID));
        assertEquals(true, Helper.runSshCommandOnPi(Constants.KILL_VLC));
        assertEquals(false, Helper.runSshCommandOnPi(Constants.VIDEO_STREAMING_CHECKING));
      } else{
        assertEquals(true, Helper.runSshCommandOnPi(Constants.VIDEO_STREAMING_STARTING));
        assertEquals(true, Helper.runSshCommandOnPi(Constants.VIDEO_STREAMING_CHECKING));
      }
    } catch (JSchException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}

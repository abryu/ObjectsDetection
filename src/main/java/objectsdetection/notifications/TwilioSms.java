package objectsdetection.notifications;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import objectsdetection.helpers.Helper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TwilioSms implements I_Notification {

  private final String accountSid = Helper.getConfigProperties().get("TWILIO_ACCOUNT_SID");
  private final String authToken = Helper.getConfigProperties().get("TWILIO_AUTH_TOKEN");
  private final String fromPhoneNumber = Helper.getConfigProperties().get("TWILIO_PHONE_NUM_FROM");
  private final String toPhoneNumbers = Helper.getConfigProperties().get("TWILIO_PHONE_NUM_TO");
  private String[] users;

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  /**
   * Use Twillio SMS service for sending the result to users.
   */
  public TwilioSms() {

    Twilio.init(accountSid, authToken);
    initUserList();

  }

  @Override
  public void notifyUsers(String messageInput) {

    PhoneNumber from = new PhoneNumber(fromPhoneNumber);

    for (String user : users) {

      Message message = Message.creator(new PhoneNumber(user),from,messageInput).create();

      logger.info(message.getSid() + " " + message.getStatus().toString());

      //logger.info("Fake Message : Sent to " + user + " ; Message Body Fake Message");

    }


  }

  @Override
  public void initUserList() {
    this.users = toPhoneNumbers.split(",");
  }


}

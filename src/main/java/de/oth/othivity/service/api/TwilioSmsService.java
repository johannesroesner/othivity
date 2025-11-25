package de.oth.othivity.service.api;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import de.oth.othivity.service.SmsService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class TwilioSmsService implements SmsService {

    private PhoneNumber twilioPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(System.getProperty("TWILIO_ACCOUNT_SID"), System.getProperty("TWILIO_AUTH_TOKEN"));
        this.twilioPhoneNumber = new PhoneNumber(System.getProperty("TWILIO_TRIAL_NUMBER"));
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        try {
            Message twilioMessage = Message.creator(new PhoneNumber(phoneNumber), this.twilioPhoneNumber, message).create();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}

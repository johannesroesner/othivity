package de.oth.othivity.service.api;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import de.oth.othivity.service.SmsService;
import jakarta.annotation.PostConstruct;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService implements SmsService {

    private PhoneNumber twilioPhoneNumber;
    private String verifyServiceSid;

    @PostConstruct
    public void init() {

        String accountSid = System.getProperty("TWILIO_ACCOUNT_SID");
        String authToken = System.getProperty("TWILIO_AUTH_TOKEN");
        String trialNumber = System.getProperty("TWILIO_TRIAL_NUMBER");
        String verifyServiceSid = System.getProperty("TWILIO_VERIFY_SERVICE_SID");
        if(accountSid == null || authToken == null || trialNumber == null || verifyServiceSid == null) {
            System.err.println("Twilio environment variables are not set properly");
            return;
        }

        Twilio.init(accountSid, authToken);
        this.twilioPhoneNumber = new PhoneNumber(trialNumber);
        this.verifyServiceSid = verifyServiceSid;
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        try {
            Message twilioMessage = Message.creator(new PhoneNumber(phoneNumber), this.twilioPhoneNumber, message).create();
        } catch (Exception error) {
            System.err.println(error.getMessage());
        }
    }

    @Override
    public void startVerification(String phoneNumber) {
        try {
            Verification verification = Verification.creator(this.verifyServiceSid, phoneNumber, "sms").create();
        } catch (Exception error) {
            System.err.println(error.getMessage());
        }
    }

   @Override
    public boolean checkVerification(String phoneNumber, String code) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid).setTo(phoneNumber).setCode(code).create();
            return verificationCheck.getStatus().equals("approved");
        } catch (Exception error) {
            System.err.println(error.getMessage());
            return false;
        }
    }
}

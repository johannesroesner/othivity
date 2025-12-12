package de.oth.othivity.service.api;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ISmsService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService implements ISmsService {

    private PhoneNumber twilioPhoneNumber;
    private String verifyServiceSid;

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String trialNumber;

    @Value("${twilio.verify-service-sid}")
    private String twilioVerifyServiceSid;

    public TwilioSmsService() {
        this.twilioPhoneNumber = null;
        this.verifyServiceSid = null;
    }

    @PostConstruct
    public void init() {
        if(accountSid == null || authToken == null || trialNumber == null || twilioVerifyServiceSid == null) {
            System.err.println("Twilio configuration is missing!");
            return;
        }

        Twilio.init(accountSid, authToken);
        this.twilioPhoneNumber = new PhoneNumber(trialNumber);
        this.verifyServiceSid = twilioVerifyServiceSid;
    }

    @Override
    public void sendSms(Profile profile, String message) {
        try {
            if(profile == null || profile.getPhone().getNumber() == null || profile.getPhone().getVerified() == false ) return;
            Message.creator(new PhoneNumber(profile.getPhone().getNumber()), this.twilioPhoneNumber, message).create();
        } catch (Exception error) {
            System.err.println(error.getMessage());
        }
    }

    @Override
    public void startVerification(String phoneNumber) {
        try {
            Verification.creator(this.verifyServiceSid, phoneNumber, "sms").create();
        } catch (Exception error) {
            System.err.println(error.getMessage());
        }
    }

    @Override
    public boolean checkVerification(String phoneNumber, String code) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(this.verifyServiceSid)
                    .setTo(phoneNumber)
                    .setCode(code)
                    .create();
            return "approved".equals(verificationCheck.getStatus());
        } catch (Exception error) {
            System.err.println(error.getMessage());
            return false;
        }
    }
}

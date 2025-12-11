package de.oth.othivity.service;

import de.oth.othivity.model.main.Profile;

public interface ISmsService {
    void sendSms(Profile profile, String message);

    void startVerification(String phoneNumber);

    boolean checkVerification(String phoneNumber, String code);
}

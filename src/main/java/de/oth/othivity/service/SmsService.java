package de.oth.othivity.service;

public interface SmsService {
    void sendSms(String phoneNumber, String message);

    void startVerification(String phoneNumber);

    boolean checkVerification(String phoneNumber, String code);
}

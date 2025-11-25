package de.oth.othivity.service;

public interface SmsService {
    void sendSms(String phoneNumber, String message);
}

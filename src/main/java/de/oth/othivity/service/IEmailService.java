package de.oth.othivity.service;

import org.springframework.stereotype.Service;

import de.oth.othivity.model.main.Profile;

@Service
public interface IEmailService {
    
    void sendEmail(Profile recipient, String subject, String message);

}

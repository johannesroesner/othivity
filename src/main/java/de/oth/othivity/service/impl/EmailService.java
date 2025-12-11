package de.oth.othivity.service.impl;

import lombok.AllArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import de.oth.othivity.service.IEmailService;
import jakarta.mail.internet.MimeMessage;
import de.oth.othivity.model.main.Profile;

@AllArgsConstructor
@Service
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(Profile recipient, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom("othivity@gmail.com");
            helper.setTo(recipient.getEmail().getAddress());
            helper.setSubject(subject);
            helper.setText(message, true); 

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            // Fehlerbehandlung
            e.printStackTrace();
        }
    }
}

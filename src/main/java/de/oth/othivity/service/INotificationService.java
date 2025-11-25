package de.oth.othivity.service;

import org.springframework.stereotype.Service;

import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.model.main.Profile;

@Service
public interface INotificationService {
    
    <T> void sendNotification(NotificationType type, T entity, Profile recipient, String messageField);

    <T> void sendNotification(NotificationType type, T entity, Profile recipient, String messageField, Profile issuer);

    void createNotification(Profile profile, String message);

}

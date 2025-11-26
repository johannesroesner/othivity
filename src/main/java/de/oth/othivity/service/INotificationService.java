package de.oth.othivity.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.model.helper.Notification;
import de.oth.othivity.model.main.Profile;

@Service
public interface INotificationService {
    
    <T> void sendNotification(NotificationType type, T entity, Profile recipient, String messageField);

    <T> void sendNotification(NotificationType type, T entity, Profile recipient, String messageField, Profile issuer);

    void createNotification(Profile profile, String subject, String message);

    List<Notification> getNotificaitonsForProfile(Profile profile);

    Notification getNotificationById(UUID id);

    void setReadStatus(Notification notification, boolean isRead);

}

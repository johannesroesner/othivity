package de.oth.othivity.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.model.helper.Notification;
import de.oth.othivity.model.main.Profile;

@Service
public interface INotificationService {
    
    <T> void sendNotification(T entity, Profile recipient, String messageField, NotificationType... types);

    <T> void sendNotification(T entity, Profile recipient, String messageField, Profile issuer, NotificationType... types);

    void createNotification(Profile profile, String subject, String message);

    List<Notification> getNotificaitonsForProfile(Profile profile);

    Notification getNotificationById(UUID id);

    void setReadStatus(Notification notification, boolean isRead);

    void deleteNotification(Notification notification);

    int getCountOfUnreadNotifications(Profile profile);

    String sendVerificationEmail(Profile recipient);

    String resendVerificationEmail(Profile recipient);

}

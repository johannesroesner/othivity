package de.oth.othivity.service.impl;

import lombok.AllArgsConstructor;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import de.oth.othivity.service.INotificationService;
import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.helper.Notification;
import de.oth.othivity.repository.helper.NotificationRepository;

import org.springframework.context.MessageSource;

import java.util.UUID;

@AllArgsConstructor
@Service
public class NotificationServiceImpl implements INotificationService {

    private final MessageSource messageSource;
    private final NotificationRepository notificationRepository;
    private final EmailServiceImpl emailService;

    @Override
    public <T> void sendNotification(NotificationType type, T entity, Profile recipient, String messageField) {
        sendNotification(type, entity, recipient, messageField, null);
    }

    @Override
    public <T> void sendNotification(NotificationType type, T entity, Profile recipient, String messageField, Profile issuer) {
        // get profile language
        String content = messageSource.getMessage(messageField, null, Locale.GERMAN);

        String formattedMessage = "";
        String formattedMessageWithLink = "";

        String subject = getSubject(content);
        String message = getMessage(content);

        if (issuer == null) {
            formattedMessage = MessageFormat.format(message, recipient.getFirstName(), getName(entity));
            formattedMessageWithLink = MessageFormat.format(message, recipient.getFirstName(), setLink(entity));
        } else {
            formattedMessage = MessageFormat.format(message, recipient.getFirstName(), issuer.getFirstName() + " " + issuer.getLastName(), getName(entity));
            formattedMessageWithLink = MessageFormat.format(message, recipient.getFirstName(), setLink(issuer), setLink(entity));
        }

        createNotification(recipient, subject, formattedMessage);

        if(type == NotificationType.SMS) {
            // send email - forrmattedMessage
            return;
        }

        if(type == NotificationType.PUSH_NOTIFICATION) {
            // send push notification - formattedMessage
            return;
        }

        if(type == NotificationType.EMAIL) {
            // send push notification - formattedMessageWithLink
            emailService.sendEmail(recipient, subject, formattedMessageWithLink);
            return;
        }

    }

    @Override
    public void createNotification(Profile profile, String subject, String message) {
        Notification notification = new Notification();
        notification.setProfile(profile);
        notification.setSubject(subject);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificaitonsForProfile(Profile profile) {
        if (profile == null) return List.of();

        return notificationRepository.findByProfile(profile);
    }

    @Override
    public Notification getNotificationById(UUID id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public void setReadStatus(Notification notification, boolean isRead) {
        if (notification != null) {
            notification.setIsRead(isRead);
            notificationRepository.save(notification);
        }
    }

    public String getSubject(String message) {
        String[] parts = message.split("\\|", 2);
        return parts.length > 0 ? parts[0] : "";
    }

    public String getMessage(String message) {
        String[] parts = message.split("\\|", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    private <T> String getName(T entity) {
        String name = "";
        if (entity instanceof Activity) {
            name = ((Activity) entity).getTitle();
        }
        if (entity instanceof Club) {
            name = ((Club) entity).getName();
        }
        if (entity instanceof Profile) {
            name = ((Profile) entity).getUsername();
        }
        return name;
    }

    private <T> String setLink(T entity) {
        String link = "";
        String baseUrl = "http://localhost:8080"; // TODO set from config

        if (entity instanceof Activity) {
            link = "<a href=\"" + baseUrl + "/activities/" + ((Activity) entity).getId() + "\">" + ((Activity) entity).getTitle() + "</a>";
        }
        if (entity instanceof Club) {
            link = "<a href=\"" + baseUrl + "/clubs/" + ((Club) entity).getId() + "\">" + ((Club) entity).getName() + "</a>";
        }
        if (entity instanceof Profile) {
            link = "<a href=\"" + baseUrl + "/profile/" + ((Profile) entity).getUsername() + "\">" + ((Profile) entity).getFirstName() + " " + ((Profile) entity).getLastName() + "</a>";
        }
        return link;
    }

}

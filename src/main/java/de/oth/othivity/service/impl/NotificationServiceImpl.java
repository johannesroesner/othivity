package de.oth.othivity.service.impl;

import lombok.AllArgsConstructor;

import java.text.MessageFormat;
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
        String message = messageSource.getMessage(messageField, null, Locale.GERMAN);

        String subject = "";
        String formattedMessage = "";
        String formattedMessageWithLink = "";

        String[] parts = message.split("\\|", 2);
        subject = parts.length > 0 ? parts[0] : "";
        message = parts.length > 1 ? parts[1] : "";

        if (issuer == null) {
            formattedMessage = MessageFormat.format(message, recipient.getFirstName(), getName(entity));
            formattedMessageWithLink = MessageFormat.format(message, recipient.getFirstName(), setLink(entity));
        } else {
            formattedMessage = MessageFormat.format(message, recipient.getFirstName(), issuer.getFirstName() + " " + issuer.getLastName(), getName(entity));
            formattedMessageWithLink = MessageFormat.format(message, setLink(recipient), setLink(issuer), setLink(entity));
        }

        createNotification(recipient, formattedMessageWithLink);

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
    public void createNotification(Profile profile, String message) {
        Notification notification = new Notification();
        notification.setProfile(profile);
        notification.setMessage(message);
        notificationRepository.save(notification);
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

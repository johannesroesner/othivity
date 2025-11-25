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

    public <T> void sendNotification(NotificationType type, T entity, Profile recipient, String messageField) {
        sendNotification(type, entity, recipient, messageField, null);
    }

    public <T> void sendNotification(NotificationType type, T entity, Profile recipient, String messageField, Profile issuer) {
        // get profile language
        String message = messageSource.getMessage(messageField, null, Locale.GERMAN);

        String formattedMessage = "";
        String formattedMessageWithLink = "";
        if (issuer == null) {
            formattedMessage = MessageFormat.format(message, recipient.getFirstName(), getName(entity));
            formattedMessageWithLink = MessageFormat.format(message, recipient.getFirstName(), getName(entity));
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
            return;
        }

    }

    public void createNotification(Profile profile, String message) {
        Notification notification = new Notification();
        notification.setProfile(profile);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public <T> String getName(T entity) {
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

    public <T> String setLink(T entity) {
        String link = "";
        if (entity instanceof Activity) {
            link = "<a href=\"/activities/" + ((Activity) entity).getId() + "\">" + ((Activity) entity).getTitle() + "</a>";
        }
        if (entity instanceof Club) {
            link = "<a href=\"/clubs/" + ((Club) entity).getId() + "\">" + ((Club) entity).getName() + "</a>";
        }
        if (entity instanceof Profile) {
            link = "<a href=\"/profile/" + ((Profile) entity).getUsername() + "\">" + ((Profile) entity).getFirstName() + " " + ((Profile) entity).getLastName() + "</a>";
        }
        return link;
    }

}

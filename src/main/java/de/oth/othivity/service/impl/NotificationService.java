package de.oth.othivity.service.impl;

import de.oth.othivity.service.ISmsService;
import lombok.AllArgsConstructor;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import de.oth.othivity.model.helper.VerificationToken;
import de.oth.othivity.repository.helper.VerificationTokenRepository;
import de.oth.othivity.service.INotificationService;
import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.helper.Notification;
import de.oth.othivity.repository.helper.NotificationRepository;
import de.oth.othivity.model.enumeration.Language;
import org.springframework.context.MessageSource;
import de.oth.othivity.service.IPushNotificationService;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Set;

@AllArgsConstructor
@Service
public class NotificationService implements INotificationService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final MessageSource messageSource;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final ISmsService smsService;
    private final IPushNotificationService pushNotificationService;

    @Override
    public <T> void sendNotification(T entity, Profile recipient, String messageField, NotificationType... types) {
        sendNotification(entity, recipient, messageField, null, types);
    }

    @Override
    public <T> void sendNotification(T entity, Profile recipient, String messageField, Profile issuer, NotificationType... types) {
        Locale locale;
        if (recipient != null && recipient.getLanguage() != null) {
            switch (recipient.getLanguage()) {
                case Language.GERMAN:
                    locale = Locale.GERMAN;
                    break;
                case Language.FRENCH:
                    locale = Locale.FRENCH;
                    break;
                case Language.SPANISH:
                    locale = Locale.forLanguageTag("es");
                    break;
                default:
                    locale = Locale.ENGLISH;
            }
        } else {
            locale = Locale.ENGLISH;
        }
        String content = messageSource.getMessage(messageField, null, locale);

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

        if(!messageField.equals("profile.deleteNotification")) {
            createNotification(recipient, subject, formattedMessage);
        }

        if (types != null && types.length > 0) {
            Set<NotificationType> uniqueTypes = Arrays.stream(types).collect(Collectors.toSet());

            for (NotificationType type : uniqueTypes) {
                switch (type) {
                    case SMS -> smsService.sendSms(recipient,subject);
                    case PUSH_NOTIFICATION -> {
                        if (recipient.getEmail() != null && recipient.getEmail().isVerified()) {
                            pushNotificationService.sendPushToProfile(recipient, subject, formattedMessage);
                        }
                    }
                    case EMAIL -> emailService.sendEmail(recipient, subject, formattedMessageWithLink);
                }
            }
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

        return notificationRepository.findByProfile(profile)
            .stream()
            .sorted(
                Comparator.comparing(Notification::getIsRead)
                .thenComparing(Notification::getCreatedAt, Comparator.reverseOrder())
            )
            .toList();
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

    @Override
    public void deleteNotification(Notification notification) {
        if (notification != null) {
            notificationRepository.delete(notification);
        }
    }

    @Override
    public int getCountOfUnreadNotifications(Profile profile) {
        if (profile != null) {
            return notificationRepository.countByProfileAndIsReadFalse(profile);
        }
        return 0;
    }

    @Override
    public String sendVerificationEmail(Profile recipient) {
        
        VerificationToken existingToken = verificationTokenRepository.findByProfile(recipient);
        if (existingToken != null) {
            //emailService.sendEmail(recipient, "Verification Email", "Please verify your email using this token: " + existingToken.getToken());
            return existingToken.getToken();
        }
        
        String token = UUID.randomUUID().toString().substring(0, 6);

        VerificationToken verificationToken = new VerificationToken(token, recipient);
        verificationTokenRepository.save(verificationToken);

        emailService.sendEmail(recipient, "Verification Email", "Please verify your email using this token: " + token);

        return token;
    }

    @Override
    public String resendVerificationEmail(Profile recipient) {
        VerificationToken existingToken = verificationTokenRepository.findByProfile(recipient);
        String token;
        if (existingToken != null) {
            token = existingToken.getToken();
        } else {
            token = UUID.randomUUID().toString().substring(0, 6);
            VerificationToken verificationToken = new VerificationToken(token, recipient);
            verificationTokenRepository.save(verificationToken);
        }

        emailService.sendEmail(recipient, "Verification Email", "Please verify your email using this token: " + token);

        return token;
    }

    private String getSubject(String message) {
        String[] parts = message.split("\\|", 2);
        return parts.length > 0 ? parts[0] : "";
    }

    private String getMessage(String message) {
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

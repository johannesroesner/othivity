package de.oth.othivity.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.pushnotification.PushSubscription;
import de.oth.othivity.repository.pushnotification.PushSubscriptionRepository;
import de.oth.othivity.service.IPushNotificationService;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;
import java.util.Map;

@Service
public class PushNotificationServiceImpl implements IPushNotificationService {

    private final PushSubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper;
    private PushService pushService;
    @Value("${vapid.public.key}") private String publicKey;
    @Value("${vapid.private.key}") private String privateKey;
    @Value("${vapid.subject}") private String subject;

    public PushNotificationServiceImpl(PushSubscriptionRepository subscriptionRepository, ObjectMapper objectMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws Exception {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        this.pushService = new PushService(publicKey, privateKey, subject);
    }

    @Override
    public void sendPushToProfile(Profile profile, String title, String message) {
        List<PushSubscription> subscriptions = subscriptionRepository.findAllByProfile(profile);

        if (subscriptions.isEmpty()) return;
        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(Map.of(
                "title", title,
                "body", message
            ));
        } catch (Exception e) {
            return;
        }

        for (PushSubscription sub : subscriptions) {
            sendToDevice(sub, jsonPayload);
        }
    }

    private void sendToDevice(PushSubscription sub, String jsonPayload) {
        try {
            Subscription.Keys keys = new Subscription.Keys(sub.getP256dh(), sub.getAuth());
            Subscription librarySub = new Subscription(sub.getEndpoint(), keys);
            
            Notification notification = new Notification(librarySub, jsonPayload);
            pushService.send(notification);
            
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("410") || errorMsg.contains("404"))) {
                subscriptionRepository.delete(sub);
            } else {
                e.printStackTrace();
            }
        }
    }
}


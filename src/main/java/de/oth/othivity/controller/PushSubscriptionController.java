package de.oth.othivity.controller;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.pushnotification.PushSubscription;
import de.oth.othivity.repository.pushnotification.PushSubscriptionRepository;
import de.oth.othivity.service.ISessionService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import de.oth.othivity.dto.PushSubscriptionDto;

@Hidden
@RestController
@AllArgsConstructor
public class PushSubscriptionController {

    private final PushSubscriptionRepository subscriptionRepository;
    private final ISessionService ISessionService;

    @PostMapping("/push/subscribe")
    public void subscribe(@RequestBody PushSubscriptionDto dto, HttpSession session) {
        Profile profile = ISessionService.getProfileFromSession(session);
        if (profile == null) return;

        if (subscriptionRepository.findByEndpoint(dto.endpoint()) == null) {
            PushSubscription sub = new PushSubscription(
                dto.endpoint(),
                dto.keys().p256dh(),
                dto.keys().auth(),
                profile
            );
            subscriptionRepository.save(sub);
        }
    }
}
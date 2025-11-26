package de.oth.othivity.controller;

import de.oth.othivity.model.helper.Notification;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.INotificationService;
import de.oth.othivity.service.SessionService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@AllArgsConstructor
@Controller
public class NotificationController {

    private final INotificationService notificationService;
    private final SessionService sessionService;

    @GetMapping("/notifications")
    public String clubs(HttpSession session, Model model) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("notifications", notificationService.getNotificaitonsForProfile(profile));        

        return "notifications";
    }

    // NotificationController.java

    @PostMapping("/notification/{id}/read")
    @ResponseBody // Wichtig, da wir kein HTML, sondern nur Status 200 zurückgeben wollen
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        // Hier deinen Service aufrufen
        Notification notification = notificationService.getNotificationById(id);
        notificationService.setReadStatus(notification, true);
        
        return ResponseEntity.ok().build();
    }
    
}

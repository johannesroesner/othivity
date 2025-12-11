package de.oth.othivity.controller;

import de.oth.othivity.model.helper.Notification;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.INotificationService;
import de.oth.othivity.service.ISessionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@AllArgsConstructor
@Controller
public class NotificationController {

    private final INotificationService notificationService;
    private final ISessionService ISessionService;

    @GetMapping("/notifications")
    public String clubs(HttpSession session, Model model, @RequestParam(required = false) UUID selectId) {
        Profile profile = ISessionService.getProfileFromSession(session);
        if (profile == null) {
            return "redirect:/dashboard";
        }
        if(selectId != null) {
            model.addAttribute("selectedNotification", notificationService.getNotificationById(selectId));
        }
        model.addAttribute("notifications", notificationService.getNotificaitonsForProfile(profile));        

        return "notifications";
    }

    @PostMapping("/notification/read/{id}")
    public String markAsRead(@PathVariable UUID id) {
        Notification notification = notificationService.getNotificationById(id);
        notificationService.setReadStatus(notification, true);
        
        return "redirect:/notifications?selectId=" + id;
    }

    @PostMapping("/notification/unread/{id}")
    public String markAsUnread(@PathVariable UUID id) {
        Notification notification = notificationService.getNotificationById(id);
        notificationService.setReadStatus(notification, false);
        
        return "redirect:/notifications?selectId=" + id;
    }

    @PostMapping("/notification/delete/{id}")
    public String deleteNotification(@PathVariable UUID id) {
        Notification notification = notificationService.getNotificationById(id);
        notificationService.deleteNotification(notification);
        
        return "redirect:/notifications";
    }
    
}

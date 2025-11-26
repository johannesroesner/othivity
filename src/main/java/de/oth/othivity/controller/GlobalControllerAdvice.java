package de.oth.othivity.controller;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/// This class adds the current user's username to the model for all controllers except public ones
@AllArgsConstructor
@ControllerAdvice(assignableTypes = {
    ActivityController.class,
    ClubController.class,
    DashboardController.class,
    ProfileController.class,
    ExplorerController.class,
    NotificationController.class
})
public class GlobalControllerAdvice {

    private final SessionService sessionService;

    @ModelAttribute
    public void addCurrentUsername(HttpSession session, Model model) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile != null) model.addAttribute("currentUsername", profile.getUsername());
    }

    @ModelAttribute
    public void addCurrentProfileImage(HttpSession session, Model model) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile != null) {
            model.addAttribute("currentProfileImage", profile.getImage() != null ? profile.getImage().getUrl() : null);
            model.addAttribute("currentProfileInitials", profile.getInitials());
        }
    }

    @ModelAttribute
    public void addReturnUrl(HttpSession session, HttpServletRequest request, Model model) {
        String returnUrl = sessionService.getReturnUrlFromSession(session, request);
        if (returnUrl != null) model.addAttribute("returnUrl", returnUrl);
        else model.addAttribute("returnUrl", "/dashboard");
    }
}

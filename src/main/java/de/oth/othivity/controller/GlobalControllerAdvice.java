package de.oth.othivity.controller;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.IChatService;
import de.oth.othivity.service.ISessionService;
import de.oth.othivity.service.INotificationService;
import de.oth.othivity.service.IReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import de.oth.othivity.model.enumeration.Role;

/// This class adds the current user's username to the model for all controllers except public ones
@RequiredArgsConstructor
@ControllerAdvice(assignableTypes = {
    ActivityController.class,
    ClubController.class,
    DashboardController.class,
    ProfileController.class,
    ChatController.class,
    ExplorerController.class,
    NotificationController.class,
    ReportController.class,
    VerificationController.class,
    ClubJoinRequestController.class
})
public class GlobalControllerAdvice {

    private final ISessionService sessionService;
    private final INotificationService notificationService;
    private final IChatService chatService;
    private final IReportService reportService;
    @ModelAttribute
    public void addCurrentUsername(HttpSession session, Model model) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile != null) model.addAttribute("currentUsername", profile.getUsername());
    }

    @ModelAttribute
    public void addCurrentProfileId(HttpSession session, Model model) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile != null) model.addAttribute("currentProfileId", profile.getId().toString());
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
    public void addCurrentProfileTheme(HttpSession session, Model model) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile != null) {
            model.addAttribute("currentThemeName", profile.getTheme().getDaisyUiName());
        }
    }

    @ModelAttribute
    public void addReturnUrl(HttpSession session, HttpServletRequest request, Model model) {
        String returnUrl = sessionService.getReturnUrlFromSession(session, request);
        if (returnUrl != null) model.addAttribute("returnUrl", returnUrl);
        else model.addAttribute("returnUrl", "/dashboard");
    }

    @ModelAttribute
    public void addUnreadNotificationCount(HttpSession session, HttpServletRequest request, Model model) {
        Profile profile = sessionService.getProfileFromSession(session);
        int count = 0;
        if (profile != null) {
            count = notificationService.getCountOfUnreadNotifications(profile);
        }
        model.addAttribute("unreadNotificationCount", count);
    }

    @ModelAttribute
    public void addUnreadChatCount(HttpSession session, HttpServletRequest request, Model model) {
        model.addAttribute("unreadMessageCount", chatService.getUnreadMessageCountForProfile(session));
    }

    @ModelAttribute
    public void addReportCount(HttpServletRequest request, Model model) {
       int count = reportService.countReports();
        model.addAttribute("unresolvedReportCount", count);
    }

    @ModelAttribute
    public void isModerator(HttpSession session, Model model) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile != null) {
            model.addAttribute("isModerator", profile.getRole().equals(Role.MODERATOR));
        } else {
            model.addAttribute("isModerator", false);
        }
    }

    @Value("${vapid.public.key}")
    private String vapidPublicKey;

    @ModelAttribute
    public void addVapidPublicKey(Model model) {
        // Damit steht "vapidPublicKey" in jedem HTML-Footer zur Verfügung
        model.addAttribute("vapidPublicKey", vapidPublicKey);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        ErrorViewModel errorViewModel = new ErrorViewModel(
            "Error", 
            "An unexpected error has occurred."
        );
        
        model.addAttribute("error", errorViewModel);
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        model.addAttribute("isCustomError", true);
        
        return "error";
    }

    public record ErrorViewModel(String headline, String subtitle) {}
    
}

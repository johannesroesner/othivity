package de.oth.othivity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.ChatService;
import de.oth.othivity.service.IExplorerService;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@AllArgsConstructor
@Controller
public class DashboardController {

    private final ChatService chatService;
    private final IExplorerService explorerService;
    private final SessionService sessionService;
    private final ActivityService activityService;

    private final ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String index(Model model, HttpSession session) {
        model.addAttribute("allActivities", activityService.getAllActivitiesWithGeoCoordinates());
        Activity soonestActivity = activityService.getSoonestActivityForProfile(sessionService.getProfileFromSession(session));
        model.addAttribute("soonestActivityForProfile", soonestActivity);
        model.addAttribute("soonestActivityTimeUntil", activityService.getActivityTimeUntil(soonestActivity));
        model.addAttribute("daysToMark", activityService.getActivityDatesForProfile(session));
        model.addAttribute("activeTab", "dashboard");
        model.addAttribute("soonestActivities", explorerService.getSoonestActivities(PageRequest.of(0, 5), null, null).getContent());
        model.addAttribute("allChats", chatService.getAllChatsForProfile(session));
        model.addAttribute("clubs", sessionService.getProfileFromSession(session).getClubs());
        return "dashboard";
    }
}

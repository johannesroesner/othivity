package de.oth.othivity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.service.IActivityService;
import de.oth.othivity.service.IChatService;
import de.oth.othivity.service.IExplorerService;
import de.oth.othivity.service.ISessionService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@AllArgsConstructor
@Controller
public class DashboardController {

    private final IChatService IChatService;
    private final IExplorerService explorerService;
    private final ISessionService ISessionService;
    private final IActivityService IActivityService;

    private final ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String index(Model model, HttpSession session) {
        model.addAttribute("allActivities", IActivityService.getAllActivitiesWithGeoCoordinates());
        Activity soonestActivity = IActivityService.getSoonestActivityForProfile(ISessionService.getProfileFromSession(session));
        model.addAttribute("soonestActivityForProfile", soonestActivity);
        model.addAttribute("soonestActivityTimeUntil", IActivityService.getActivityTimeUntil(soonestActivity));
        model.addAttribute("daysToMark", IActivityService.getActivityDatesForProfile(session));
        model.addAttribute("activeTab", "dashboard");
        model.addAttribute("soonestActivities", explorerService.getSoonestActivities(PageRequest.of(0, 3), null, null).getContent());
        model.addAttribute("allChats", IChatService.getAllChatsForProfile(session));
        model.addAttribute("clubs", ISessionService.getProfileFromSession(session).getClubs());
        return "dashboard";
    }
}

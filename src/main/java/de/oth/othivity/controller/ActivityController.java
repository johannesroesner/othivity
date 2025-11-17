package de.oth.othivity.controller;

import de.oth.othivity.service.ActivityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/activities")
    public String activities(HttpSession session, Model model) {
        model.addAttribute("daysToMark", activityService.getActivityDatesForProfile(session));
        model.addAttribute("profileActivities", activityService.getActivitiesCreatedOrJoinedByProfile(session));
        model.addAttribute("createdActivities", activityService.getActivitiesCreatedByProfile(session));
        model.addAttribute("allActivities", activityService.getActivitiesNotCreatedOrNotJoinedByProfile(session));
        return "activity-overview";
    }
}

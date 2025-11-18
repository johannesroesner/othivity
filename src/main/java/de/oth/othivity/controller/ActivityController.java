package de.oth.othivity.controller;

import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.LanguageService;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.service.TagService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final ProfileService profileService;
    private final TagService tagService;
    private final LanguageService languageService;

    @GetMapping("/activities")
    public String activities(HttpSession session, Model model) {
        model.addAttribute("daysToMark", activityService.getActivityDatesForProfile(session));
        model.addAttribute("profileActivities", activityService.getActivitiesCreatedOrJoinedByProfile(session));
        model.addAttribute("createdActivities", activityService.getActivitiesCreatedByProfile(session));
        model.addAttribute("allActivities", activityService.getActivitiesNotCreatedOrNotJoinedByProfile(session));
        return "activity-overview";
    }

    @GetMapping("/activities/create")
    public String showCreateForm(HttpSession session, Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("languages", languageService.getFlags());
        model.addAttribute("tags",  tagService.getAllTags());
        model.addAttribute("tagableClubs", profileService.allJoinedClubsByProfile(session));
        return "activity-create";
    }

}

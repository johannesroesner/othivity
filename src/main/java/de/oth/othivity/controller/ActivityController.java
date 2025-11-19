package de.oth.othivity.controller;

import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final ProfileService profileService;

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
        model.addAttribute("languages", Language.getFlags());
        model.addAttribute("allTags", Tag.values());
        model.addAttribute("tagableClubs", profileService.allJoinedClubsByProfile(session));
        return "activity-create";
    }

    @PostMapping("/activities/create")
    public String createClub(@ModelAttribute("activity") Activity activity, @RequestParam MultipartFile [] uploadedImages, HttpSession session) {

        //logic to-do
        System.out.println("test");
        System.out.println(activity);
        System.out.println("----------------");

        return "redirect:/activities";
    }

}

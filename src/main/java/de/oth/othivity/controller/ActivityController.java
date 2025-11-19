package de.oth.othivity.controller;

import de.oth.othivity.dto.ActivityCreateRequest;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.validator.ActivityRequestValidator;
import de.oth.othivity.validator.ImageUploadValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final ProfileService profileService;

    private final ActivityRequestValidator activityRequestValidator;
    private final ImageUploadValidator imageUploadValidator;

    @InitBinder("activityCreateRequest")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(activityRequestValidator);
    }

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
        model.addAttribute("activityCreateRequest", new ActivityCreateRequest());
        model.addAttribute("languages", Language.getFlags());
        model.addAttribute("allTags", Tag.values());
        model.addAttribute("tagableClubs", profileService.allJoinedClubsByProfile(session));
        return "activity-create";
    }

    @PostMapping("/activities/create")
    public String createActivity(@Valid @ModelAttribute("activityCreateRequest") ActivityCreateRequest activityCreateRequest, BindingResult bindingResult, @RequestParam MultipartFile [] uploadedImages, HttpSession session, Model model) {
        if (bindingResult.hasErrors() || imageUploadValidator.validate(uploadedImages) != null ) {
            model.addAttribute("imageFilesError", imageUploadValidator.validate(uploadedImages));
            model.addAttribute("languages", Language.getFlags());
            model.addAttribute("allTags", Tag.values());
            model.addAttribute("tagableClubs", profileService.allJoinedClubsByProfile(session));
            return "activity-create";
        }

        return "redirect:/activities";
    }

}

package de.oth.othivity.controller;

import de.oth.othivity.dto.ActivityDto;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.validator.ActivityDtoValidator;
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

import java.util.UUID;

@AllArgsConstructor
@Controller
public class ActivityController {

    private final ActivityService activityService;
    private final ProfileService profileService;
    private final SessionService sessionService;

    private final ActivityDtoValidator activityDtoValidator;
    private final ImageUploadValidator imageUploadValidator;

    @InitBinder("activityDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(activityDtoValidator);
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
        model.addAttribute("activityDto", new ActivityDto());
        model.addAttribute("languages", Language.getFlags());
        model.addAttribute("allTags", Tag.values());
        model.addAttribute("tagableClubs", profileService.allJoinedClubsByProfile(session));
        return "activity-create";
    }

    @PostMapping("/activities/create")
    public String createActivity(@Valid @ModelAttribute("activityDto") ActivityDto activityDto, BindingResult bindingResult, @RequestParam MultipartFile [] uploadedImages, HttpSession session, Model model) {
        if (bindingResult.hasErrors() || imageUploadValidator.validate(uploadedImages) != null ) {
            model.addAttribute("imageFilesError", imageUploadValidator.validate(uploadedImages));
            model.addAttribute("languages", Language.getFlags());
            model.addAttribute("allTags", Tag.values());
            model.addAttribute("tagableClubs", profileService.allJoinedClubsByProfile(session));
            return "activity-create";
        }

        activityService.createActivity(activityDto, uploadedImages,session);

        return "redirect:/activities";
    }

    @GetMapping("/activities/{id}")
    public String getActivityDetail(@PathVariable("id") String activityId, Model model, HttpSession session) {
        Activity activity = activityService.getActivityById(UUID.fromString(activityId));
        if (activity == null) return "redirect:/activities";
        model.addAttribute("activity", activity);
        model.addAttribute("images", activity.getImages());
        model.addAttribute("joinAble", sessionService.canJoin(session, activity));
        model.addAttribute("leaveAble", sessionService.canLeave(session, activity));
        model.addAttribute("updateAble", sessionService.canUpdate(session, activity));
        model.addAttribute("deleteAble", sessionService.canDelete(session, activity));
        return "activity-detail";
    }

    @PostMapping("/activities/join/{id}")
    public String joinActivity(@PathVariable("id") String activityId, HttpSession session) {
        Activity activity = activityService.getActivityById(UUID.fromString(activityId));
        if (activity != null && sessionService.canJoin(session, activity)) {
            activityService.joinActivity(activity, session);
        }
        return "redirect:/activities/" + activityId;
    }

    @PostMapping("/activities/leave/{id}")
    public String leaveActivity(@PathVariable("id") String activityId, HttpSession session) {
        Activity activity = activityService.getActivityById(UUID.fromString(activityId));
        if (activity != null && sessionService.canLeave(session, activity)) {
            activityService.leaveActivity(activity, session);
        }
        return "redirect:/activities/" + activityId;
    }

    @PostMapping("/activities/delete/{id}")
    public String deleteActivity(@PathVariable("id") String activityId, HttpSession session) {
        Activity activity = activityService.getActivityById(UUID.fromString(activityId));
        if (activity != null && sessionService.canDelete(session, activity)) {
            activityService.deleteActivity(activity);
        }
        return "redirect:/activities";
    }

    @PostMapping("/activities/kick/{activityId}/{personId}")
    public String kickParticipant(@PathVariable("activityId") String activityId, @PathVariable("personId") String personId, HttpSession session) {
        Activity activity = activityService.getActivityById(UUID.fromString(activityId));
        Profile profile = profileService.getProfileById(UUID.fromString(personId));
        if (activity != null && profile != null && sessionService.canDelete(session, activity)) {
            activityService.kickParticipant(activity, profile);
        }
        return "redirect:/activities/" + activityId;
    }
}

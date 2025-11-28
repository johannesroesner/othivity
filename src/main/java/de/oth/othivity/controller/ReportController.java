package de.oth.othivity.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.IReportService;
import de.oth.othivity.model.main.Profile;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import de.oth.othivity.dto.ReportDto;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.model.enumeration.Role;

@AllArgsConstructor
@Controller

public class ReportController {
    private final IReportService reportService;
    private final ClubService clubService;
    private final ProfileService profileService;
    private final ActivityService activityService;
    private final SessionService sessionService;

    @GetMapping("/reports")
    public String getMethodName(Model model, HttpSession session) {

        Profile profile = sessionService.getProfileFromSession(session);
        if(profile == null || !profile.getRole().equals(Role.MODERATOR)) {
            return "redirect:/";
        }
        model.addAttribute("clubReports", reportService.getAllClubReports());
        model.addAttribute("activityReports", reportService.getAllActivityReports());
        model.addAttribute("profileReports", reportService.getAllProfileReports());

        return "reports-overview.html";
    }
    @GetMapping("/reports/create/club/{clubId}")
    public String getCreateClubReport(@PathVariable("clubId") String clubId, Model model, HttpSession session) {
        UUID clubUuid = UUID.fromString(clubId);
        model.addAttribute("club", clubService.getClubById(clubUuid));
        model.addAttribute("reportDto", new ReportDto());
        return "create-report-view.html";
    }
    @PostMapping("/reports/create/club/{clubId}")
    public String createClubReport(@Validated @ModelAttribute ReportDto reportDto, @PathVariable("clubId") String clubId, BindingResult bindingResult, Model model, HttpSession session) {
        UUID clubUuid = UUID.fromString(clubId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("club", clubService.getClubById(clubUuid));
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("reportDto", reportDto);
            return "create-report-view.html";
        }
        Profile issuer = sessionService.getProfileFromSession(session);
        reportService.createClubReport(reportDto, issuer, clubService.getClubById(clubUuid));
        return "redirect:/clubs/" + clubUuid;
        
    }
    @GetMapping("/reports/create/profile/{profileId}")
    public String getCreateProfileReport(@PathVariable("profileId") String profileId, Model model, HttpSession session) {
        UUID profileUuid = UUID.fromString(profileId);
        model.addAttribute("profile", profileService.getProfileById(profileUuid));
        model.addAttribute("reportDto", new ReportDto());
        return "create-report-view.html";
    }
    @PostMapping("/reports/create/profile/{profileId}")
    public String createProfileReport(@Validated @ModelAttribute ReportDto reportDto, @PathVariable("profileId") String profileId, BindingResult bindingResult, Model model, HttpSession session) {
        UUID profileUuid = UUID.fromString(profileId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", profileService.getProfileById(profileUuid));
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("reportDto", reportDto);
            return "create-report-view.html";
        }
        Profile issuer = sessionService.getProfileFromSession(session);
        reportService.createProfileReport(reportDto, issuer, profileService.getProfileById(profileUuid));
        return "redirect:/profiles/" + profileUuid;
    }
    @GetMapping("/reports/create/activity/{activityId}")
    public String getCreateActivityReport(@PathVariable("activityId") String activityId, Model model, HttpSession session) {
        UUID activityUuid = UUID.fromString(activityId);
        model.addAttribute("activity", activityService.getActivityById(activityUuid));
        model.addAttribute("reportDto", new ReportDto());
        return "create-report-view.html";
    }
    @PostMapping("/reports/create/activity/{activityId}")
    public String createActivityReport(@Validated @ModelAttribute ReportDto reportDto, @PathVariable("activityId") String activityId, BindingResult bindingResult, Model model, HttpSession session) {
        UUID activityUuid = UUID.fromString(activityId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activity", activityService.getActivityById(activityUuid));
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("reportDto", reportDto);
            return "create-report-view.html";
        }
        Profile issuer = sessionService.getProfileFromSession(session);
        reportService.createActivityReport(reportDto, issuer, activityService.getActivityById(activityUuid));
        return "redirect:/activities/" + activityUuid;
    }
    @PostMapping("/reports/accept/club/{reportId}")
    public String acceptClubReport(@PathVariable("reportId") String reportId) {
        UUID reportUuid = UUID.fromString(reportId);
        reportService.acceptClubReport(reportUuid);
        return "redirect:/reports?tab=club";
    }
    @PostMapping("/reports/accept/activity/{reportId}")
    public String acceptActivityReport(@PathVariable("reportId") String reportId) {
        UUID reportUuid = UUID.fromString(reportId);
        reportService.acceptActivityReport(reportUuid);
        return "redirect:/reports?tab=activity";
    }
    @PostMapping("/reports/accept/profile/{reportId}")
    public String acceptProfileReport(@PathVariable("reportId") String reportId) {
        UUID reportUuid = UUID.fromString(reportId);
        reportService.acceptProfileReport(reportUuid);
        return "redirect:/reports?tab=profile";
    }
    @PostMapping("/reports/reject/club/{reportId}")
    public String rejectClubReport(@PathVariable("reportId") String reportId) {
        UUID reportUuid = UUID.fromString(reportId);
        reportService.rejectClubReport(reportUuid);
        return "redirect:/reports?tab=club";
    }
    @PostMapping("/reports/reject/activity/{reportId}")
    public String rejectActivityReport(@PathVariable("reportId") String reportId) {
        UUID reportUuid = UUID.fromString(reportId);
        reportService.rejectActivityReport(reportUuid);
        return "redirect:/reports?tab=activity";
    }
    @PostMapping("/reports/reject/profile/{reportId}")
    public String rejectProfileReport(@PathVariable("reportId") String reportId) {
        UUID reportUuid = UUID.fromString(reportId);
        reportService.rejectProfileReport(reportUuid);
        return "redirect:/reports?tab=profile";
    }
}
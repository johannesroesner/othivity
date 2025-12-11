package de.oth.othivity.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import de.oth.othivity.service.IClubService;
import de.oth.othivity.service.IProfileService;
import de.oth.othivity.service.IActivityService;
import de.oth.othivity.service.IReportService;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.main.Activity;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import de.oth.othivity.dto.ReportDto;
import de.oth.othivity.service.ISessionService;
import de.oth.othivity.model.enumeration.Role;

@AllArgsConstructor
@Controller

public class ReportController {
    private final IReportService reportService;
    private final IClubService clubService;
    private final IProfileService profileService;
    private final IActivityService activityService;
    private final ISessionService sessionService;

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
        Profile issuer = sessionService.getProfileFromSession(session);
        Club club = clubService.getClubById(clubUuid);
        if (!reportService.isReportableClub(issuer, club)) {
            return "redirect:/clubs/" + clubUuid;
        }
        model.addAttribute("club", club);
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
        Club club = clubService.getClubById(clubUuid);
        if (reportService.isReportableClub(issuer, club))
            reportService.createClubReport(reportDto, issuer, club);
        return "redirect:/clubs/" + clubUuid;
        
    }
    @GetMapping("/reports/create/profile/{profileId}")
    public String getCreateProfileReport(@PathVariable("profileId") String profileId, Model model, HttpSession session) {
        UUID profileUuid = UUID.fromString(profileId);
        Profile issuer = sessionService.getProfileFromSession(session);
        Profile profile = profileService.getProfileById(profileUuid);
        if (!reportService.isReportableProfile(issuer, profile)) {
            return "redirect:/profile/" + profile.getUsername();
        }
        model.addAttribute("profile", profile);
        model.addAttribute("reportDto", new ReportDto());
        return "create-report-view.html";
    }
    @PostMapping("/reports/create/profile/{profileId}")
    public String createProfileReport(@Validated @ModelAttribute ReportDto reportDto, @PathVariable("profileId") String profileId, BindingResult bindingResult, Model model, HttpSession session) {
        UUID profileUuid = UUID.fromString(profileId);
        Profile profile = profileService.getProfileById(profileUuid);
        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", profile);
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("reportDto", reportDto);
            return "create-report-view.html";
        }
        Profile issuer = sessionService.getProfileFromSession(session);
        if (reportService.isReportableProfile(issuer, profile))
            reportService.createProfileReport(reportDto, issuer, profile);
        return "redirect:/profile/" + profile.getUsername();
    }
    @GetMapping("/reports/create/activity/{activityId}")
    public String getCreateActivityReport(@PathVariable("activityId") String activityId, Model model, HttpSession session) {
        UUID activityUuid = UUID.fromString(activityId);
        Profile issuer = sessionService.getProfileFromSession(session);
        Activity activity = activityService.getActivityById(activityUuid);
        if (!reportService.isReportableActivity(issuer, activity)) {
            return "redirect:/activities/" + activityUuid;
        }
        model.addAttribute("activity", activity);
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
        Activity activity = activityService.getActivityById(activityUuid);
        if (reportService.isReportableActivity(issuer, activity))
            reportService.createActivityReport(reportDto, issuer, activity);
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
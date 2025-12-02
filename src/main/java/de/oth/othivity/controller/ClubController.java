package de.oth.othivity.controller;

import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.IReportService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.validator.ImageUploadValidator;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.model.main.Profile;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import de.oth.othivity.dto.ClubDto;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import de.oth.othivity.validator.ClubDtoValidator;
import java.util.UUID;



@AllArgsConstructor
@Controller
public class ClubController {

    private final ImageUploadValidator imageUploadValidator;
    private final ClubService clubService;
    private final SessionService sessionService;
    private final ClubDtoValidator clubDtoValidator;
    private final ProfileService profileService;
    private final IReportService reportService;

    @InitBinder("clubDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(clubDtoValidator);
    }

    @GetMapping("/clubs")
    public String clubs(HttpSession session, Model model) {
        model.addAttribute("allClubs", clubService.getClubsNotJoinedByProfile(session));
        model.addAttribute("joinedClubs", clubService.getClubsJoinedByProfile(session));
        model.addAttribute("managedClubs", clubService.getClubsManagedByProfile(session));
        return "club-overview";
    }

    @GetMapping("/clubs/create")
    public String getCreateClub(Model model, HttpSession session) {
        model.addAttribute("clubDto", new ClubDto());
        model.addAttribute("accessLevels", AccessLevel.values());
        model.addAttribute("returnUrl", "/clubs");
        model.addAttribute("pageTitle", "clubCreate.pageTitle");
        return "club-edit";
    }

    @PostMapping("/clubs/create")
    public String createClub(@Valid @ModelAttribute("clubDto") ClubDto clubDto, BindingResult bindingResult, @RequestParam MultipartFile uploadedImage, HttpSession session, Model model) {
        if(bindingResult.hasErrors()|| imageUploadValidator.validateRequired(uploadedImage) != null) {
            model.addAttribute("imageFileError", imageUploadValidator.validateRequired(uploadedImage));
            model.addAttribute("accessLevels", AccessLevel.values());
            model.addAttribute("returnUrl", "/clubs");
            model.addAttribute("pageTitle", "clubCreate.pageTitle");
            return "club-edit";
        }
        clubService.createClubForUser(clubDto, session, uploadedImage);
        return "redirect:/clubs";
    }

    @GetMapping("/clubs/{id}")
    public String getClubDetails(@PathVariable("id") String clubId, HttpSession session, Model model) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        if(club == null) {
            return "redirect:/clubs";
        }
        model.addAttribute("club", club);
        model.addAttribute("joinAble", sessionService.canJoin(session, club));
        model.addAttribute("editMode", sessionService.canUpdate(session, club));
        model.addAttribute("leaveAble", sessionService.canLeave(session, club));
        model.addAttribute("joinAbleOnInvite", sessionService.canJoinOnInvite(session, club));
        model.addAttribute("inviteOnly", club.getAccessLevel() == AccessLevel.ON_INVITE);
    
        model.addAttribute("clubMembers", clubService.getMembersOfClubWithoutAdmins(club));
        model.addAttribute("clubAdmins", club.getAdmins());
        model.addAttribute("memberCount", club.getMembers() != null ? club.getMembers().size() : 0); 
        model.addAttribute("clubActivities", clubService.getActivitiesByClub(club));
        model.addAttribute("activitiesCount", clubService.getActivitiesByClub(club).size());
        model.addAttribute("isReportable", reportService.isReportableClub(sessionService.getProfileFromSession(session), club));

        return "club-detail";
    }

    @GetMapping("/clubs/edit/{id}")
    public String getEditClub(@PathVariable("id") String clubId, Model model, HttpSession session) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        if (club == null || !sessionService.canUpdate(session, club)) {
            return "redirect:/clubs/" + clubId;
        }
        model.addAttribute("clubDto", clubService.clubToDto(club));
        model.addAttribute("accessLevels", AccessLevel.values());
        model.addAttribute("returnUrl", "/clubs/" + clubId);
        model.addAttribute("pageTitle", "clubEdit.pageTitle");
        return "club-edit";
    }

    @PostMapping("/clubs/edit/{id}")
    public String editClub(@Valid @ModelAttribute("clubDto") ClubDto clubDto, BindingResult bindingResult, @PathVariable("id") String clubId, @RequestParam MultipartFile uploadedImage, HttpSession session, Model model) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        if (club == null || !sessionService.canUpdate(session, club)) {
            return "redirect:/clubs/" + clubId;
        }
        if(bindingResult.hasErrors()|| imageUploadValidator.validateNotRequired(uploadedImage) != null) {
            model.addAttribute("imageFileError", imageUploadValidator.validateNotRequired(uploadedImage));
            model.addAttribute("accessLevels", AccessLevel.values());
            model.addAttribute("returnUrl", "/clubs/" + clubId);
            model.addAttribute("pageTitle", "clubEdit.pageTitle");
            return "club-edit";
        }
        clubService.updateClub(club, clubDto, uploadedImage, session);
        return "redirect:/clubs/" + clubId;
    }
    
    @PostMapping("/clubs/join/{id}")
    public String joinClub(@PathVariable("id") String clubId, HttpSession session) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        if (club == null) return "redirect:/clubs";

        clubService.joinClubForProfile(session, club);

        return "redirect:/clubs/" + clubId;
    }
    @PostMapping("/clubs/leave/{id}")
    public String leaveClub(@PathVariable("id") String clubId, HttpSession session) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        if (club == null) return "redirect:/clubs";
        if (clubService.wouldLeaveRequireAdminSelection(session, club)) {
            return "redirect:/clubs/" + clubId + "/select-admin?leaving=true";
        }
        
        clubService.leaveClubForProfile(session, club);

        return "redirect:/clubs/" + clubId;
    }

    @GetMapping("/clubs/{id}/select-admin")
    public String getSelectAdmin(@PathVariable("id") String clubId, @RequestParam(value = "leaving", defaultValue = "false") boolean isLeaving, HttpSession session, Model model) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        if (club == null) {
            return "redirect:/clubs";
        }
        
        Profile currentProfile = sessionService.getProfileFromSession(session);
        if (currentProfile == null) {
            return "redirect:/clubs/" + clubId;
        }
        if (isLeaving) {
            if (!club.getAdmins().contains(currentProfile) || club.getAdmins().size() > 1) {
                return "redirect:/clubs/" + clubId;
            }
        } else {
            if (!club.getAdmins().isEmpty()) {
                return "redirect:/clubs/" + clubId;
            }
        }
        if (club.getMembers().isEmpty()) {
            return "redirect:/clubs";
        }
        
        model.addAttribute("club", club);
        model.addAttribute("clubMembers", isLeaving ? 
        club.getMembers().stream().filter(m -> !m.equals(currentProfile)).toList() : 
        club.getMembers());
        model.addAttribute("isLeaving", isLeaving);
        return "club-select-admin";
    }
    
    @PostMapping("/clubs/delete/{id}")
    public String deleteClub(@PathVariable("id") String clubId, HttpSession session) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        if (club == null) return "redirect:/clubs";

        clubService.deleteClub(club, sessionService.getProfileFromSession(session));

        return "redirect:/clubs";
    }
    @PostMapping("/clubs/makeAdmin/{clubId}/{profileId}")
    public String makeAdmin(@PathVariable("clubId") String clubId, @PathVariable("profileId") String profileId, 
                           @RequestParam(value = "leaving", defaultValue = "false") boolean isLeaving, HttpSession session) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        Profile profile = profileService.getProfileById(UUID.fromString(profileId));
        if (club == null || profile == null) {
            return "redirect:/clubs/" + clubId;
        }

        clubService.makeProfileAdminOfClub(profile, club, session);
        if (isLeaving) {
            clubService.leaveClubForProfile(session, club);
            return "redirect:/clubs";
        }

        return "redirect:/clubs/" + clubId;
    }
    @PostMapping("/clubs/removeMember/{clubId}/{profileId}")
    public String removeMember(@PathVariable("clubId") String clubId, @PathVariable("profileId") String profileId, HttpSession session) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        Profile profile = profileService.getProfileById(UUID.fromString(profileId));
        if (club == null || profile == null) {
            return "redirect:/clubs/" + clubId;
        }

        clubService.removeProfileFromClub(profile, club, session);

        return "redirect:/clubs/" + clubId;
    }
}

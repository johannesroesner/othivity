package de.oth.othivity.controller;

import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.validator.ImageUploadValidator;
import de.oth.othivity.model.main.Profile;
import java.util.List;
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
    public String getCreateClub(Model model) {
        model.addAttribute("clubDto", new ClubDto());
        model.addAttribute("accessLevels", AccessLevel.values());
        return "club-create";
    }

    @PostMapping("/clubs/create")
    public String createClub(@Valid @ModelAttribute("clubDto") ClubDto clubDto, BindingResult bindingResult, @RequestParam MultipartFile[] uploadedImages, HttpSession session, Model model) {
        if(bindingResult.hasErrors()|| imageUploadValidator.validate(uploadedImages) != null) {
            model.addAttribute("accessLevels", AccessLevel.values());
            return "club-create";
        }
        clubService.createClubForUser(clubDto, session, uploadedImages);
        return "redirect:/clubs";
    }

    @GetMapping("/clubs/{id}")
    public String getClubDetails(@PathVariable("id") String clubId, HttpSession session, Model model) {
        Club club = clubService.getClubById(UUID.fromString(clubId));
        if(club == null) {
            return "redirect:/clubs";
        }
        model.addAttribute("club", club);
        model.addAttribute("joinAble", sessionService.canJoinClub(session, club));
        model.addAttribute("editMode", sessionService.canEditClub(session, club));
        

        model.addAttribute("clubMembers", clubService.getMembersOfClubWithoutAdmins(club));
        model.addAttribute("clubAdmins", club.getAdmins());
        model.addAttribute("clubImages", club.getImages()); 
        model.addAttribute("memberCount", club.getMembers() != null ? club.getMembers().size() : 0); 
        model.addAttribute("clubActivities", clubService.getActivitiesByClub(club));
        model.addAttribute("activitiesCount", clubService.getActivitiesByClub(club).size());

        return "club-detail";
    }
}

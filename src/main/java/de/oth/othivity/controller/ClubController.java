package de.oth.othivity.controller;

import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.SessionService;
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
public class ClubController {
    private final ClubService clubService;
    private final SessionService sessionService;

    @GetMapping("/clubs")
    public String clubs(HttpSession session, Model model) {
        model.addAttribute("allClubs", clubService.getClubsNotJoinedByProfileNotPrivate(session));
        model.addAttribute("joinedClubs", clubService.getClubsJoinedByProfile(session));
        model.addAttribute("managedClubs", clubService.getClubsManagedByProfile(session));
        return "club-overview";
    }

    @GetMapping("/clubs/create")
    public String getCreateClub(Model model) {
        model.addAttribute("club", new Club());
        model.addAttribute("accessLevels", AccessLevel.values());
        return "club-create";
    }

    @PostMapping("/clubs/create")
    public String createClub(@ModelAttribute("club") Club club, @RequestParam("imageFiles") List<MultipartFile> imageFiles,
    HttpSession session) {
        //imageService 
        clubService.createClubForUser(club, session);
        return "redirect:/clubs";
    }
}

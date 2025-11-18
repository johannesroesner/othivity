package de.oth.othivity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.ProfileService;
import lombok.AllArgsConstructor;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@AllArgsConstructor
@Controller
public class ClubController {
    private final ClubService clubService;
    private final ProfileService profileService;

    @GetMapping("/clubs")
    public String clubs(HttpSession session, Model model) {
        model.addAttribute("allClubs", clubService.getClubsNotJoinedByProfileNotPrivate(session));
        model.addAttribute("joinedClubs", clubService.getClubsJoinedByProfile(session));
        model.addAttribute("managedClubs", clubService.getClubsManagedByProfile(session));
        return "club-overview";
    }   
}

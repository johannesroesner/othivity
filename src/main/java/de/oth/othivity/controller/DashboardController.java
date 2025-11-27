package de.oth.othivity.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import de.oth.othivity.dto.UsernameDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.ui.Model;

@AllArgsConstructor
@Controller
public class DashboardController {

    private final SessionService sessionService;

    @GetMapping("/dashboard")
    public String index(Model model, HttpSession session) {
        model.addAttribute("activeTab", "");

        Profile profile = sessionService.getProfileFromSession(session);
        if (profile.getSetupComplete() == null || !profile.getSetupComplete()) {
            return "redirect:/setup";
        }
        if (profile.getEmail() == null || !profile.getEmail().getVerified()) {
            return "redirect:/verify-email";
        }

        return "dashboard";
    }
}

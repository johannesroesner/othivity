package de.oth.othivity.controller;


import de.oth.othivity.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@AllArgsConstructor
@Controller
public class LandingController {

    private final ProfileService profileService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activeTab", "");
        model.addAttribute("profileCounter", profileService.getProfileCounter());
        return "landing";
    }
}

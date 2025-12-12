package de.oth.othivity.controller;


import de.oth.othivity.service.IProfileService;
import lombok.AllArgsConstructor;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@AllArgsConstructor
@Controller
public class LandingController {

    private final IProfileService profileService;

    @GetMapping("/")
    public String index(Locale local, Model model) {

        model.addAttribute("activeTab", "");
        model.addAttribute("profileCounter", profileService.getProfileCounter());
        model.addAttribute("browserLang", local.getLanguage());
        return "landing";
    }
}

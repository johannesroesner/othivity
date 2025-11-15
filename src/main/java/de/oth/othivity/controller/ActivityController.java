package de.oth.othivity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class ActivityController {

    @GetMapping("/activities")
    public String activities(Model model) {
        List<String> daysToMark = Arrays.asList("2025-11-16", "2025-11-18");
        model.addAttribute("daysToMark", daysToMark);
        model.addAttribute("activeTab", "activities");
        return "activity-overview";
    }
}

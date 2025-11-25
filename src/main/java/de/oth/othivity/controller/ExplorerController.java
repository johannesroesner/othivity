package de.oth.othivity.controller;

import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.IExplorerService;
import de.oth.othivity.model.main.Activity;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@AllArgsConstructor
@Controller
public class ExplorerController {
    private final ClubService clubService;
    private final IExplorerService explorerService;

    @GetMapping("/explorer")
    public String clubs(HttpSession session, Model model,
                        @RequestParam(required = false) Double lat,
                        @RequestParam(required = false) Double lon) {
        //TODO: remove clubs if not needed rename method
        // model.addAttribute("allClubs", clubService.getClubsNotJoinedByProfile(session));
        // model.addAttribute("joinedClubs", clubService.getClubsJoinedByProfile(session));
        // model.addAttribute("managedClubs", clubService.getClubsManagedByProfile(session));

        // Default location: OTH Regensburg
        if (lat == null || lon == null) {
            lat = 49.0179;
            lon = 12.0968;
        }

        List<Activity> closestActivities = explorerService.getClosestActivities(lat, lon, 50);
        model.addAttribute("closestActivities", closestActivities);

        List<Activity> soonestActivities = explorerService.getSoonestActivities(50);
        model.addAttribute("soonestActivities", soonestActivities);

        List<Activity> bestMixActivities = explorerService.getBestMixActivities(lat, lon, 50);
        model.addAttribute("bestMixActivities", bestMixActivities);

        model.addAttribute("allActivities", explorerService.getAllFutureActivities());

        return "explorer";
    }
}

package de.oth.othivity.controller;

import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.service.IExplorerService;
import de.oth.othivity.service.PagingService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.model.main.Profile;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Controller
public class ExplorerController {
    private final IExplorerService explorerService;
    private final PagingService pagingService;
    private final SessionService sessionService;

    @GetMapping("/explorer")
    public String explorer(HttpSession session, Model model, 
                           @RequestParam(required = false) Double lat, 
                           @RequestParam(required = false) Double lon,
                           @RequestParam(defaultValue = "0") int bestMixPage,
                           @RequestParam(defaultValue = "0") int soonestPage,
                           @RequestParam(defaultValue = "0") int closestPage,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "bestMix") String activeTab,
                           @RequestParam(defaultValue = "date") String sortBy,
                           @RequestParam(defaultValue = "asc") String direction,
                           @RequestParam(required = false) String tag,
                           @RequestParam(required = false) String search) {
        
        if (lat == null || lon == null) {
            lat = 49.0179;
            lon = 12.0968;
        }

        Tag selectedTag = null;
        if(tag != null && !tag.isBlank() && !tag.equalsIgnoreCase("all")) selectedTag = Tag.valueOf(tag.toUpperCase());

        Pageable bestMixPageable = pagingService.createPageable(bestMixPage, size, sortBy, direction);
        Pageable soonestPageable = pagingService.createPageable(soonestPage, size, sortBy, direction);
        Pageable closestPageable = pagingService.createPageable(closestPage, size, sortBy, direction);

        Page<Activity> bestMixActivities = explorerService.getBestMixActivities(lat, lon, bestMixPageable, search, selectedTag);
        model.addAttribute("bestMixActivities", bestMixActivities);

        Page<Activity> soonestActivities = explorerService.getSoonestActivities(soonestPageable, search, selectedTag);
        model.addAttribute("soonestActivities", soonestActivities);

        Page<Activity> closestActivities = explorerService.getClosestActivities(lat, lon, closestPageable, search, selectedTag);
        model.addAttribute("closestActivities", closestActivities);

        Pageable cardPageable = PageRequest.of(0, 3);

        Page<Activity> bestMixCards = explorerService.getBestMixActivities(lat, lon, cardPageable, null, null);
        model.addAttribute("bestMixCards", bestMixCards);

        Page<Activity> soonestCards = explorerService.getSoonestActivities(cardPageable, null, null);
        model.addAttribute("soonestCards", soonestCards);

        Page<Activity> closestCards = explorerService.getClosestActivities(lat, lon, cardPageable, null, null);
        model.addAttribute("closestCards", closestCards);
        
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("search", search);
        model.addAttribute("tag", selectedTag);
        model.addAttribute("allTags", Tag.values());
        
        return "explorer";
    }
}
package de.oth.othivity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import de.oth.othivity.service.ClubJoinRequestService;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.dto.ClubJoinRequestDto;
import de.oth.othivity.validator.ClubJoinRequestDtoValidator;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;



@AllArgsConstructor
@Controller
public class ClubJoinRequestController {
    private final ClubJoinRequestService clubJoinRequestService;
    private final ClubService clubService;
    private final SessionService sessionService;
    private final ClubJoinRequestDtoValidator clubJoinRequestDtoValidator;

    @InitBinder("clubJoinRequestDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(clubJoinRequestDtoValidator);
    }

    @GetMapping("/clubs/join-requests/{clubId}")
    public String getJoinRequestsForClub(@PathVariable("clubId") String clubId, Model model, HttpSession session) {
        UUID clubUuid = UUID.fromString(clubId);
        model.addAttribute("clubId", clubId);
        model.addAttribute("requests", clubJoinRequestService.getJoinRequestsForClub(clubUuid));
        return "club-join-requests";
    }
    
    @GetMapping("/clubs/join-requests/create/{clubId}")
    public String getCreateJoinRequestForClub(@PathVariable("clubId") String clubId, Model model, HttpSession session) {
        ClubJoinRequestDto clubJoinRequestDto = new ClubJoinRequestDto();
        clubJoinRequestDto.setClubId(UUID.fromString(clubId));
        model.addAttribute("clubJoinRequestDto", clubJoinRequestDto);
        model.addAttribute("clubId", clubId);
        return "club-join-request-create";
    }
    
    @PostMapping("/clubs/join-requests/create/{clubId}")
    public String createJoinRequest(@PathVariable("clubId") String clubId,
                                  @Valid @ModelAttribute("clubJoinRequestDto") ClubJoinRequestDto clubJoinRequestDto,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        
        // Set clubId in case it's not set correctly
        clubJoinRequestDto.setClubId(UUID.fromString(clubId));
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("clubId", clubId);
            return "club-join-request-create";
        }
        
        try {
            Profile currentProfile = sessionService.getProfileFromSession(session);
            if (currentProfile == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to send a join request.");
                return "redirect:/login";
            }
            
            clubJoinRequestService.createJoinRequest(clubJoinRequestDto, currentProfile);
            
            redirectAttributes.addFlashAttribute("successMessage", "Join request sent successfully!");
            return "redirect:/clubs/" + clubId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send join request: " + e.getMessage());
            return "redirect:/clubs/join-requests/create/" + clubId;
        }
    }
    @PostMapping("/clubs/join-requests/{clubId}/accept/{requestId}")
    public String acceptJoinRequest(@PathVariable("clubId") String clubId, 
                                  @PathVariable("requestId") String requestId,
                                  RedirectAttributes redirectAttributes) {
        try {
            UUID requestUuid = UUID.fromString(requestId);
            clubJoinRequestService.acceptJoinRequest(requestUuid);
            redirectAttributes.addFlashAttribute("successMessage", "Join request accepted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to accept join request: " + e.getMessage());
        }
        return "redirect:/clubs/join-requests/" + clubId;
    }
    
    @PostMapping("/clubs/join-requests/{clubId}/decline/{requestId}")
    public String declineJoinRequest(@PathVariable("clubId") String clubId,
                                   @PathVariable("requestId") String requestId,
                                   RedirectAttributes redirectAttributes) {
        try {
            UUID requestUuid = UUID.fromString(requestId);
            clubJoinRequestService.rejectJoinRequest(requestUuid);
            redirectAttributes.addFlashAttribute("successMessage", "Join request declined successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to decline join request: " + e.getMessage());
        }
        return "redirect:/clubs/join-requests/" + clubId;
    }
    
}

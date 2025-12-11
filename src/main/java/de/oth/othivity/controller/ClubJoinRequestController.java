package de.oth.othivity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import de.oth.othivity.service.IClubJoinRequestService;
import de.oth.othivity.service.ISessionService;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.dto.ClubJoinRequestDto;
import de.oth.othivity.validator.ClubJoinRequestDtoValidator;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;



@AllArgsConstructor
@Controller
public class ClubJoinRequestController {
    private final IClubJoinRequestService IClubJoinRequestService;
    private final ISessionService ISessionService;
    private final ClubJoinRequestDtoValidator clubJoinRequestDtoValidator;

    @InitBinder("clubJoinRequestDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(clubJoinRequestDtoValidator);
    }

    @GetMapping("/clubs/join-requests/{clubId}")
    public String getJoinRequestsForClub(@PathVariable("clubId") String clubId, Model model, HttpSession session) {
        UUID clubUuid = UUID.fromString(clubId);
        model.addAttribute("clubId", clubId);
        model.addAttribute("requests", IClubJoinRequestService.getJoinRequestsForClub(clubUuid));
        model.addAttribute("returnUrl", "/clubs/" + clubId);
        return "club-join-requests";
    }
    
    @GetMapping("/clubs/join-requests/create/{clubId}")
    public String getCreateJoinRequestForClub(@PathVariable("clubId") String clubId, Model model, HttpSession session) {
        ClubJoinRequestDto clubJoinRequestDto = new ClubJoinRequestDto();
        clubJoinRequestDto.setClubId(UUID.fromString(clubId));
        model.addAttribute("clubJoinRequestDto", clubJoinRequestDto);
        model.addAttribute("clubId", clubId);
        model.addAttribute("returnUrl", "/clubs/" + clubId);
        return "club-join-request-create";
    }
    
    @PostMapping("/clubs/join-requests/create/{clubId}")
    public String createJoinRequest(@PathVariable("clubId") String clubId,
                                  @Valid @ModelAttribute("clubJoinRequestDto") ClubJoinRequestDto clubJoinRequestDto,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model) {
        
        clubJoinRequestDto.setClubId(UUID.fromString(clubId));
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("clubId", clubId);
            return "club-join-request-create";
        }
        Profile currentProfile = ISessionService.getProfileFromSession(session);
        IClubJoinRequestService.createJoinRequest(clubJoinRequestDto, currentProfile);
        return "redirect:/clubs/" + clubId;
    }
    @PostMapping("/clubs/join-requests/{clubId}/accept/{requestId}")
    public String acceptJoinRequest(@PathVariable("clubId") String clubId, 
                                  @PathVariable("requestId") String requestId) {
        
        UUID requestUuid = UUID.fromString(requestId);
        IClubJoinRequestService.acceptJoinRequest(requestUuid);
        return "redirect:/clubs/join-requests/" + clubId;
    }
    
    @PostMapping("/clubs/join-requests/{clubId}/decline/{requestId}")
    public String declineJoinRequest(@PathVariable("clubId") String clubId,
                                   @PathVariable("requestId") String requestId) {
        UUID requestUuid = UUID.fromString(requestId);
        IClubJoinRequestService.rejectJoinRequest(requestUuid);
        return "redirect:/clubs/join-requests/" + clubId;
    }
    
}

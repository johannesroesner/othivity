package de.oth.othivity.controller;

import de.oth.othivity.service.IChatService;
import de.oth.othivity.validator.ProfileDtoValidator;
import de.oth.othivity.validator.EmailVerificationDtoValidator;
import de.oth.othivity.validator.UsernameDtoValidator; 

import java.util.Calendar;


import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.util.UUID;

import de.oth.othivity.service.IPagingService;
import de.oth.othivity.validator.ImageUploadValidator;
import de.oth.othivity.service.IProfileService;
import de.oth.othivity.service.ISessionService;
import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.dto.UsernameDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.helper.VerificationToken;
import de.oth.othivity.repository.helper.VerificationTokenRepository;
import de.oth.othivity.dto.EmailVerificationDto;
import de.oth.othivity.service.INotificationService;
import de.oth.othivity.service.IApiTokenService;
import de.oth.othivity.service.IReportService;
import de.oth.othivity.model.enumeration.Theme;

@AllArgsConstructor
@Controller
public class ProfileController {

    private final IProfileService profileService;
    private final ISessionService sessionService;
    private final INotificationService notificationService;
    private final VerificationTokenRepository tokenRepository;
    private final IApiTokenService apiTokenService; 
    private final IReportService reportService;
    private final IPagingService pagingService;

    private final ImageUploadValidator imageUploadValidator;
    private final ProfileDtoValidator profileDtoValidator;
    private final IChatService chatService;
    private final UsernameDtoValidator usernameDtoValidator; 

    @InitBinder("profileDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(profileDtoValidator);
    }
    
    @InitBinder("usernameDto")
    protected void initBinderUsername(WebDataBinder binder) {
        binder.addValidators(usernameDtoValidator);
    }

    @GetMapping("/profiles")
    public String searchProfiles(HttpSession session, Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "username") String sortBy,
                                 @RequestParam(defaultValue = "asc") String direction,
                                 @RequestParam(required = false) String search) {

        Pageable pageable = pagingService.createPageable(page, size, sortBy, direction);
        
        Page<Profile> profiles;
        if (search == null || search.isBlank()) {
            profiles = Page.empty(pageable);
        } else {
            profiles = profileService.searchProfiles(search, pageable);
        }

        model.addAttribute("profiles", profiles);
        model.addAttribute("search", search);
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("activeTab", "profiles"); 

        Profile profile = sessionService.getProfileFromSession(session);
        model.addAttribute("currentThemeName", (profile != null && profile.getTheme().isDark()) ? "dark" : "light");

        return "profile-overview";
    }

    @GetMapping("/profile/{username}")
    public String getProfileDetail(@PathVariable("username") String username, Model model, HttpSession session, HttpServletRequest request) {
        Profile profile = profileService.getProfileByUsername(username);
        Profile currentProfile = sessionService.getProfileFromSession(session);
        
        if (profile == null) {
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/dashboard");
        }

        model.addAttribute("profile", profile);
        model.addAttribute("isOwnProfile", currentProfile.getId().equals(profile.getId()));

        model.addAttribute("chatId", chatService.buildChatId(profile, currentProfile));

        model.addAttribute("canMessage", sessionService.canMessage(session,profile));
        model.addAttribute("canDelete", sessionService.canDelete(session, profile));
        model.addAttribute("canUpdate", sessionService.canUpdate(session, profile));
        model.addAttribute("isReportable", reportService.isReportableProfile(currentProfile, profile));

        return "profile";
    }

    @GetMapping("/settings")
    public String getProfileSettings(Model model, HttpSession session, HttpServletRequest request) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) {
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/dashboard");
        }

        model.addAttribute("apiTokens", apiTokenService.getProfileTokens(profile));
        model.addAttribute("profile", profile);
        model.addAttribute("languages", Language.values());
        model.addAttribute("themes", Theme.values());
        return "settings";
    }

    @GetMapping("/profile/edit/{username}")
    public String editProfile(@PathVariable("username") String username, Model model, HttpSession session) {
        Profile profileToEdit = profileService.getProfileByUsername(username);
        if (profileToEdit == null) return "redirect:/dashboard";
        if (!sessionService.canUpdate(session, profileToEdit)) return "redirect:/profile/" + username;
        model.addAttribute("profile", profileToEdit);
        model.addAttribute("profileDto", profileService.profileToDto(profileToEdit));
        return "profile-edit";
    }

    @PostMapping("/profile/edit/{username}")
    public String updateProfile(@PathVariable("username") String username, @Valid @ModelAttribute("profileDto") ProfileDto profileDto, BindingResult bindingResult, @RequestParam(value = "uploadedImage", required = false) MultipartFile uploadedImage, HttpSession session, Model model) {
        Profile profileToEdit = profileService.getProfileByUsername(username);

        if (profileToEdit == null) return "redirect:/dashboard";
        if (!sessionService.canUpdate(session, profileToEdit)) return "redirect:/profile/" + username;

        if (bindingResult.hasErrors() || (uploadedImage != null && imageUploadValidator.validateNotRequired(uploadedImage) != null)) {
            model.addAttribute("imageFileError", uploadedImage != null ? imageUploadValidator.validateNotRequired(uploadedImage) : null);
            model.addAttribute("profile", profileToEdit);
            return "profile-edit";
        }

        profileService.updateProfile(profileToEdit, profileDto, uploadedImage);
        return "redirect:/profile/" + profileToEdit.getUsername();
    }

    @PostMapping("/profile/deleteImage/{username}")
    public String deleteProfileImage(@PathVariable String username, HttpSession session) {
        Profile profileToEdit = profileService.getProfileByUsername(username);

        if (profileToEdit == null) return "redirect:/dashboard";
        if (!sessionService.canUpdate(session, profileToEdit)) return "redirect:/profile/" + username;

        profileService.deleteProfileImage(profileToEdit);

        return "redirect:/profile/edit/" + username;
    }

    @PostMapping("/profile/delete/{username}")
    public String deleteProfile(@PathVariable("username") String username, HttpSession session, HttpServletRequest request) {
        Profile profileToDelete = profileService.getProfileByUsername(username);
        Profile currentProfile = sessionService.getProfileFromSession(session);

        if (!sessionService.canDelete(session, profileToDelete)) {
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/dashboard");
        }

        if (profileToDelete != null) {
            if (!profileToDelete.getId().equals(currentProfile.getId())) {
                String returnUrl = sessionService.getReturnUrlFromSession(session, request);
                profileService.deleteProfile(profileToDelete);
                return "redirect:" + (returnUrl != null ? returnUrl : "/dashboard");
            }
            profileService.deleteProfile(profileToDelete);
            session.invalidate();
        }
        return "redirect:/login";
    }

    @PostMapping("/change-language")
    public String changeLanguage(@RequestParam("language") Language language, HttpSession session, HttpServletRequest request, HttpServletResponse response) { 
        Profile profile = sessionService.getProfileFromSession(session);
        
        if (profile != null) {
            profileService.updateProfileLanguage(profile, language);
            profile.setLanguage(language); 
            sessionService.updateLocaleResolverWithProfileLanguage(request, response, profile);
        }
        
        return "redirect:/settings";
    }

    @PostMapping("/change-theme")
    public String changeTheme(@RequestParam("theme") Theme theme, HttpSession session, HttpServletRequest request) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile != null) {
            profileService.updateProfileTheme(profile, theme);
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/settings");
    }

    @GetMapping("/setup")
    public String setup(Model model, HttpSession session) {

        model.addAttribute("usernameDto", new UsernameDto());
        return "setup";
    }
    
    @PostMapping("/profile/username/update")
    public String updateUsername(@Valid @ModelAttribute UsernameDto usernameDto, BindingResult bindingResult, Model model, HttpSession session , HttpServletRequest request, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "setup";
        }

        Profile profile = sessionService.getProfileFromSession(session);

        if (profile == null) {
            return "redirect:/login";
        }
        profileService.updateProfileLanguage(profile, request.getLocale());
        profileService.setUsername(profile, usernameDto.getUsername());
        sessionService.updateLocaleResolverWithProfileLanguage(request, response, profile);

        return "redirect:/dashboard";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(Model model, HttpSession session) {

        model.addAttribute("emailVerificationDto", new EmailVerificationDto());

        notificationService.sendVerificationEmail(sessionService.getProfileFromSession(session));

        return "verify-email";
    }
    

    @PostMapping("/profile/email/verify")
    public String verifyUser(@ModelAttribute EmailVerificationDto emailVerificationDto, Model model, HttpSession session) {

        VerificationToken verificationToken = tokenRepository.findByToken(emailVerificationDto.getToken());
        Profile profile = sessionService.getProfileFromSession(session);

        if (verificationToken == null) {
            model.addAttribute("message", "Ungültiger Token.");
            System.out.println("Invalid token");
            return "verify-email";
        }

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", "Token ist abgelaufen.");
            System.out.println("Token expired");
            return "verify-email";
        }

        if (verificationToken.getProfile() == null || !verificationToken.getProfile().getId().equals(profile.getId())) {
            model.addAttribute("message", "Token gehört nicht zum aktuellen Benutzer.");
            System.out.println("Token does not belong to current user");
            return "verify-email";
        }

        profileService.setVerificationForEmail(profile);
        
        profile.getEmail().setVerified(true);
        session.setAttribute("profile", profile);

        tokenRepository.delete(verificationToken);

        return "redirect:/dashboard";
    }
    

    @PostMapping("/tokens")
    public String createToken(@RequestParam("name") String name, @RequestParam("duration") int duration, Principal principal, RedirectAttributes redirectAttributes, HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);

        if (profile == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kein Profil gefunden.");
            return "redirect:/settings";
        }

        String rawToken = apiTokenService.createToken(profile, name, duration);

        redirectAttributes.addFlashAttribute("createdToken", rawToken);
        redirectAttributes.addFlashAttribute("successMessage", "Token erstellt!");

        return "redirect:/settings";
    }

    @PostMapping("/tokens/delete")
    public String deleteToken(@RequestParam("id") UUID id, Principal principal, HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        
        if (profile != null) {
            apiTokenService.revokeToken(id, profile);
        }
        
        return "redirect:/settings";
    }
}
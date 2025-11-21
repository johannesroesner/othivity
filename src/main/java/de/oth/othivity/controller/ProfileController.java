package de.oth.othivity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.oth.othivity.validator.ImageUploadValidator;
import de.oth.othivity.service.ImageService;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.model.main.Profile;

@AllArgsConstructor
@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final ImageService imageService;
    private final ImageUploadValidator imageUploadValidator;
    private final SessionService sessionService;

    @GetMapping("/profile/{username}")
    public String getProfileDetail(@PathVariable("username") String username, Model model, HttpSession session, HttpServletRequest request) {
        Profile profile = profileService.getProfileByUsername(username);
        Profile currentProfile = sessionService.getProfileFromSession(session);
        
        if (profile == null) {
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/dashboard");
        }
        model.addAttribute("profile", profile);
        model.addAttribute("images", profile.getImages());

        model.addAttribute("isOwnProfile", currentProfile.getId().equals(profile.getId()));

        String returnUrl = sessionService.getReturnUrlFromSession(session, request);
        if (returnUrl == null) returnUrl = "/dashboard";
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("canDelete", sessionService.canDelete(session, profile));
        model.addAttribute("canUpdate", sessionService.canUpdate(session, profile));

        return "profile";
    }

    @GetMapping("/settings")
    public String getProfileSettings(Model model, HttpSession session, HttpServletRequest request) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) {
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/dashboard");
        }
        model.addAttribute("profile", profile);
        model.addAttribute("images", profile.getImages());

        String returnUrl = sessionService.getReturnUrlFromSession(session, request);
        model.addAttribute("returnUrl", returnUrl);

        return "settings";
    }

    @GetMapping("/profile/edit/{username}")
    public String editProfile(@PathVariable("username") String username, Model model, HttpSession session) {
        Profile profileToEdit = profileService.getProfileByUsername(username);
        if (profileToEdit == null) {
            return "redirect:/dashboard";
        }

        if (!sessionService.canUpdate(session, profileToEdit)) {
            return "redirect:/profile/" + username;
        }

        ProfileDto profileDto = new ProfileDto();
        profileDto.setPhone(profileToEdit.getPhone());
        profileDto.setAboutMe(profileToEdit.getAboutMe());
        
        model.addAttribute("profile", profileToEdit);
        model.addAttribute("profileDto", profileDto);
        model.addAttribute("images", profileToEdit.getImages());
        return "profile-edit";
    }

    @PostMapping("/profile/edit/{username}")
    public String updateProfile(@PathVariable("username") String username, @ModelAttribute("profileDto") ProfileDto profileDto, @RequestParam(value = "uploadedImages", required = false) MultipartFile[] uploadedImages, HttpSession session, Model model) {
        Profile profileToEdit = profileService.getProfileByUsername(username);
        if (profileToEdit == null) {
            return "redirect:/dashboard";
        }

        if (!sessionService.canUpdate(session, profileToEdit)) {
            return "redirect:/profile/" + username;
        }

        if (uploadedImages != null && uploadedImages.length > 0 && !uploadedImages[0].isEmpty()) {
             String imageError = imageUploadValidator.validate(uploadedImages);
             if (imageError != null) {
                 model.addAttribute("imageFilesError", imageError);
                 model.addAttribute("profile", profileToEdit);
                 model.addAttribute("images", profileToEdit.getImages());
                 return "profile-edit";
             }
             imageService.saveImagesForProfile(profileToEdit, uploadedImages);
        }

        profileService.updateProfile(profileToEdit, profileDto);
        return "redirect:/profile/" + profileToEdit.getUsername();
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
}

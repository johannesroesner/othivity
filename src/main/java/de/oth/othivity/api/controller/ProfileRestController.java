package de.oth.othivity.api.controller;

import de.oth.othivity.api.dto.ProfileApiDto;
import de.oth.othivity.api.service.EntityConverter;
import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.CustomUserDetails;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Theme;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.service.IUserService; 
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    private final ProfileService profileService;
    private final EntityConverter entityConverter;
    private final IUserService userService; 

    @GetMapping ("/all")
    public List<ProfileApiDto> getAllProfiles() {
        return profileService.getAllProfiles()
                .stream()
                .map(entityConverter::ProfileToApiDto)
                .toList();
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getProfileByPrincipal(@AuthenticationPrincipal UserDetails userDetail) {
        Profile profile = profileService.getProfileByEmail(userDetail.getUsername());
        if (profile == null) {
            return ResponseEntity
                    .status(404)
                    .body("error: profile not found" + userDetail.getUsername());
        }
        ProfileApiDto response = entityConverter.ProfileToApiDto(profile);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Object> getProfileByUsername(@AuthenticationPrincipal CustomUserDetails userDetail, @PathVariable String username) {
        Profile requesterProfile = profileService.getProfileByEmail(userDetail.getUsername());
        
        if(requesterProfile == null || !requesterProfile.getRole().equals(Role.MODERATOR)){
            return ResponseEntity.status(403).body("error: forbidden");
        }
        
        Profile profile = profileService.getProfileByUsername(username);

        if (profile == null) {
            return ResponseEntity.status(404).body("error: profile not found");
        }

        ProfileApiDto response = entityConverter.ProfileToApiDto(profile);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity<Object> createProfile(@AuthenticationPrincipal CustomUserDetails userDetail, @RequestBody ProfileApiDto apiDto) {
        Profile requesterProfile = profileService.getProfileByEmail(userDetail.getUsername());
        if (requesterProfile == null || !requesterProfile.getRole().equals(Role.MODERATOR)) {
            return ResponseEntity
                    .status(403)
                    .body("error: unauthorized");
        }

        RegisterDto registerDto;
        ProfileDto profileDetailsDto;
        try {
            registerDto = entityConverter.ApiDtoToRegisterDto(apiDto);
            profileDetailsDto = entityConverter.ApiDtoToProfileDto(apiDto);
            
            if (profileService.isEmailTaken(registerDto.getEmail())) {
                 return ResponseEntity.status(409).body("error: email already taken");
            }

            if (profileService.isUsernameTaken(registerDto.getUsername())) {
                 return ResponseEntity.status(409).body("error: username already taken");
            }
        } catch (Exception error) {
            return ResponseEntity
                    .status(400)
                    .body("error: " + error.getMessage());
        }

        try {
            userService.registerNewUserAccount(registerDto, Locale.ENGLISH, false, true); 
        } catch (Exception e) {
             return ResponseEntity.status(400).body("error creating user: " + e.getMessage());
        }

        Profile createdProfile = profileService.getProfileByEmail(registerDto.getEmail());
        
        if (createdProfile == null) {
            return ResponseEntity.internalServerError().body("error: profile creation failed internally");
        }

        createdProfile = profileService.updateProfile(createdProfile, profileDetailsDto, null);

        if (apiDto.getLanguage() != null) {
             try {
                 Language language = Language.valueOf(apiDto.getLanguage());
                 profileService.updateProfileLanguage(createdProfile, language);
             } catch (IllegalArgumentException e) {
                 // ignore
             }
        }

        if (apiDto.getTheme() != null) {
             try {
                 Theme theme = Theme.valueOf(apiDto.getTheme());
                 profileService.updateProfileTheme(createdProfile, theme);
             } catch (IllegalArgumentException e) {
                 // ignore
             }
        }

        return ResponseEntity.status(201).body(entityConverter.ProfileToApiDto(createdProfile));
    }

    @PutMapping("/{username}")
    public ResponseEntity<Object> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetail, @PathVariable String username, @RequestBody ProfileApiDto apiDto) {
        Profile profileToUpdate = profileService.getProfileByUsername(username);
        Profile requesterProfile = profileService.getProfileByEmail(userDetail.getUsername());

        if (profileToUpdate == null || requesterProfile == null || 
            (!profileToUpdate.getId().equals(requesterProfile.getId()) && !requesterProfile.getRole().equals(Role.MODERATOR))) {
            return ResponseEntity.status(403).body("error: forbidden");
        }

        ProfileDto profileDto;
        try {
            profileDto = entityConverter.ApiDtoToProfileDto(apiDto);
            if (profileToUpdate.getFirstName() != null && profileDto.getFirstName() != null && !profileToUpdate.getFirstName().equals(profileDto.getFirstName())) 
                throw new IllegalArgumentException("first name cannot be changed");
                
            if (profileToUpdate.getLastName() != null && profileDto.getLastName() != null && !profileToUpdate.getLastName().equals(profileDto.getLastName())) 
                throw new IllegalArgumentException("last name cannot be changed");
                
            if (profileToUpdate.getEmail() != null && profileDto.getEmail() != null && profileDto.getEmail().getAddress() != null && 
                !profileToUpdate.getEmail().getAddress().equals(profileDto.getEmail().getAddress())) 
                throw new IllegalArgumentException("email cannot be changed");
                
        } catch (Exception error) {
            return ResponseEntity
                    .status(400)
                    .body("error: " + error.getMessage());
        }
        
        Profile updatedProfile = profileService.updateProfile(profileToUpdate, profileDto, null);

        if (apiDto.getLanguage() != null) {
             try {
                 Language language = Language.valueOf(apiDto.getLanguage());
                 profileService.updateProfileLanguage(updatedProfile, language);
             } catch (IllegalArgumentException e) {
                 // ignore
             }
        }

        if (apiDto.getTheme() != null) {
             try {
                 Theme theme = Theme.valueOf(apiDto.getTheme());
                 profileService.updateProfileTheme(updatedProfile, theme);
             } catch (IllegalArgumentException e) {
                 // ignore
             }
        }

        return ResponseEntity.status(200).body(entityConverter.ProfileToApiDto(updatedProfile));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Object> deleteProfile(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String username) {
        Profile profileToDelete = profileService.getProfileByUsername(username);
        Profile requesterProfile = profileService.getProfileByEmail(userDetails.getUsername());

        if (profileToDelete == null || requesterProfile == null || 
            (!profileToDelete.getId().equals(requesterProfile.getId()) && !requesterProfile.getRole().equals(Role.MODERATOR))) {
            return ResponseEntity.status(403).body("error: forbidden");
        }

        profileService.deleteProfile(profileToDelete);
        return ResponseEntity.status(204).build();
    }
}
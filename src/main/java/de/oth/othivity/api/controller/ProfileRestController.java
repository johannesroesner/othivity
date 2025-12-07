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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/profiles")
@Tag(name = "Profiles", description = "Profile Management API")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileRestController {

    private final ProfileService profileService;
    private final EntityConverter entityConverter;
    private final IUserService userService; 

    @Operation(summary = "Get all profiles", description = "Returns a list of all profiles in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of profiles",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileApiDto.class)))
    })
    @GetMapping ("/all")
    public List<ProfileApiDto> getAllProfiles() {
        return profileService.getAllProfiles()
                .stream()
                .map(entityConverter::ProfileToApiDto)
                .toList();
    }

    @Operation(summary = "Get current user profile", description = "Returns the profile of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileApiDto.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
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

    @Operation(summary = "Get profile by username", description = "Returns a profile by username. Only moderators can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileApiDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not a moderator"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<Object> getProfileByUsername(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @Parameter(description = "Username of the profile to retrieve", required = true) @PathVariable String username) {
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

    @Operation(summary = "Create a new profile", description = "Creates a new profile with user account. Only moderators can create profiles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileApiDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict - Email or username already taken")
    })
    @PostMapping
    public ResponseEntity<Object> createProfile(@AuthenticationPrincipal CustomUserDetails userDetail, @RequestBody ProfileApiDto apiDto) {
        Profile requesterProfile = profileService.getProfileByEmail(userDetail.getUsername());
        if (requesterProfile == null || !requesterProfile.getRole().equals(Role.MODERATOR)) {
            return ResponseEntity
                    .status(403)
                    .body("error: forbidden");
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

    @Operation(summary = "Update a profile", description = "Updates an existing profile. Users can update their own profile, moderators can update any profile. First name, last name, and email cannot be changed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileApiDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or attempt to change immutable fields"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to update this profile")
    })
    @PutMapping("/{username}")
    public ResponseEntity<Object> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @Parameter(description = "Username of the profile to update", required = true) @PathVariable String username,
            @RequestBody ProfileApiDto apiDto) {
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

    @Operation(summary = "Delete a profile", description = "Deletes a profile. Users can delete their own profile, moderators can delete any profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profile successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to delete this profile")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Object> deleteProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Username of the profile to delete", required = true) @PathVariable String username) {
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
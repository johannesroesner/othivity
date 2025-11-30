package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.helper.Phone;
import de.oth.othivity.model.helper.Email;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ClubRepository;

import de.oth.othivity.service.ImageService;
import de.oth.othivity.service.INotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final SessionService sessionService;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ClubRepository clubRepository;
    private final ImageService imageService;
    private final INotificationService notificationService;

    @Override
    public List<Club> allJoinedClubsByProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();
        return profile.getClubs();
    }

    @Override
    public Profile createProfileFromUser(User user, RegisterDto registerDto, Locale clientLocale, boolean needSetup, boolean needVerificationEmail) {
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFirstName(registerDto.getFirstName());
        profile.setLastName(registerDto.getLastName());
        profile.setUsername(registerDto.getUsername());
        profile.setEmail(new Email(registerDto.getEmail()));
        if (!needSetup) {
            profile.setSetupComplete(true);
        }
        if (!needVerificationEmail) {
            profile.getEmail().setVerified(true);
        }
        profile.setRole(Role.USER);
        if (clientLocale != null) {
            profile.setLanguage(localeToLanguage(clientLocale));
        }
        return profileRepository.save(profile);
    }

    @Override
    public void updateProfile(Profile profile, ProfileDto profileDto, MultipartFile uploadedImage) {
        if(uploadedImage != null && uploadedImage.getSize() != 0) profile.setImage(imageService.saveImage(profile, uploadedImage));
        profile.setPhone(profileDto.getPhone());
        profile.setAboutMe(profileDto.getAboutMe());
        profileRepository.save(profile);
    }

    @Override
    public void updateProfileLanguage(Profile profile, Language language) {
        profile.setLanguage(language);
        profileRepository.save(profile);
    }

    @Override
    public void updateProfileLanguage(Profile profile, Locale clientLocale) {
        profile.setLanguage(localeToLanguage(clientLocale));
        profileRepository.save(profile);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return profileRepository.existsByusername(username);
    }

    @Override
    public boolean isEmailTaken(String email){
        return profileRepository.existsByEmailAddress(email);
    }

    @Override
    public Profile getProfileById(UUID profileId) {
        return profileRepository.findById(profileId).orElse(null);
    }

    @Override
    public Profile getProfileByUsername(String username) {
        return profileRepository.findByusername(username);
    }

    @Override
    public Profile getCurrentProfile(HttpSession session) {
        return sessionService.getProfileFromSession(session);
    }

    @Override
    public Profile setUsername(Profile profile, String username) {
        profile.setUsername(username);
        profile.setSetupComplete(true);
        return profileRepository.save(profile);
    }

    @Override
    public void setVerificationForEmail(Profile profile) {
        profile.getEmail().setVerified(true);
        profileRepository.save(profile);
    }

    @Override
    public void deleteProfile(Profile profile) {
        userRepository.delete(profile.getUser()); //TODO SBM delete profile
    }

    @Override
    public void deleteProfileImage(Profile profile) {
        profile.setImage(null);
        profileRepository.save(profile);
    }

    @Override
    public ProfileDto profileToDto(Profile profile) {
        ProfileDto profileDto = new ProfileDto();
        if (profile.getPhone() == null){
            profileDto.setPhone(new Phone());
        } else {
            profileDto.setPhone(profile.getPhone());
        }
        profileDto.setAboutMe(profile.getAboutMe());
        return profileDto;
    }

    @Override
    public boolean isEmailVerified(Profile profile) {
        return profile.getEmail().getVerified();
    }

    @Override
    public boolean isSetupComplete(Profile profile) {
        return profile.getSetupComplete();
    }

    private Language localeToLanguage(Locale locale) {
        if (locale == null) return Language.ENGLISH;
        String languageTag = locale.getLanguage();
        return switch (languageTag) {
            case "de" -> Language.GERMAN;
            case "en" -> Language.ENGLISH;
            case "fr" -> Language.FRENCH;
            case "es" -> Language.SPANISH;
            default -> Language.ENGLISH;
        };
    }
}
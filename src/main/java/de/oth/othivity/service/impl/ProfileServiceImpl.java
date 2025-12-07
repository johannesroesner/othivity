package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.helper.Phone;
import de.oth.othivity.model.helper.Email;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.*;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ClubRepository;


import de.oth.othivity.model.enumeration.Theme;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final SessionService sessionService;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final ClubService clubService;
    private final ActivityService activityService;
    private final INotificationService notificationService;

    @Override
    public List<Profile> getAllProfiles(){
        return profileRepository.findAll();
    }

    @Override
    public List<Profile> getAllProfiles(){
        return profileRepository.findAll();
    }

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
        profile.setEmail(new Email(registerDto.getEmail().toLowerCase()));
        if(registerDto.getImage() != null) profile.setImage(imageService.saveImage(profile,registerDto.getImage()));
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
    public Profile updateProfile(Profile profile, ProfileDto profileDto, MultipartFile uploadedImage) {
        if(uploadedImage != null && uploadedImage.getSize() != 0) profile.setImage(imageService.saveImage(profile,uploadedImage));
        else if(profileDto.getImage() != null) profile.setImage(imageService.saveImage(profile,profileDto.getImage()));
        
        if(profileDto.getPhone() != null && !profileDto.getPhone().getNumber().isEmpty()) profile.setPhone(profileDto.getPhone());
        
        if(profileDto.getFirstName() != null && !profileDto.getFirstName().isEmpty()) profile.setFirstName(profileDto.getFirstName());
        if(profileDto.getLastName() != null && !profileDto.getLastName().isEmpty()) profile.setLastName(profileDto.getLastName());
        
        profile.setAboutMe(profileDto.getAboutMe());
        profileRepository.save(profile);
        return profile;
    }

    @Override
    public void updateProfileLanguage(Profile profile, Language language) {
        profile.setLanguage(language);
        profileRepository.save(profile);
    }

    @Override
    public void updateProfileTheme(Profile profile, Theme theme) {
        profile.setTheme(theme);
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
    public Profile getProfileByEmail(String email) {
        return profileRepository.findByEmailAddress(email.toLowerCase()).orElse(null);
        return profileRepository.findByEmailAddress(email.toLowerCase()).orElse(null);
    }

    @Override
    public Profile getProfileByUsername(String username) {
        return profileRepository.findByUsername(username);
        return profileRepository.findByUsername(username);
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

        for(Club club : profile.getClubs()) {
            club.getMembers().remove(profile);
            if(club.getAdmins().contains(profile)){
                if(club.getAdmins().size() == 1) clubService.deleteClub(club,profile);
                else {
                    club.getAdmins().remove(profile);
                }
            }
        }

        activityService.removeProfileFromActivities(profile);

        notificationService.sendNotification(profile,profile,"profile.deleteNotification", NotificationType.EMAIL, NotificationType.SMS);

        userRepository.delete(profile.getUser());
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
        profileDto.setFirstName(profile.getFirstName());
        profileDto.setLastName(profile.getLastName());
        return profileDto;
    }

    @Override
    public int getProfileCounter() {
        return (int) profileRepository.count();
    }

    @Override
    public boolean isEmailVerified(Profile profile) {
        return profile.getEmail().getVerified();
    }

    @Override
    public boolean isSetupComplete(Profile profile) {
        return profile.getSetupComplete();
    }

    @Override
    public Page<Profile> searchProfiles(String search, Pageable pageable) {
        return profileRepository.findAllByFilter(search, pageable);
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
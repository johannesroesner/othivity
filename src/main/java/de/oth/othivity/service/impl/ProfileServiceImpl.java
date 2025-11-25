package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.service.ImageService;
import de.oth.othivity.service.IEmailService;
import de.oth.othivity.service.INotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    public Profile createProfileFromUser(User user, RegisterDto registerDto) {
        // Implementation here
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFirstName(registerDto.getFirstName());
        profile.setLastName(registerDto.getLastName());
        profile.setUsername(registerDto.getUsername());
        profile.setEmail(registerDto.getEmail());
        profile.setRole(Role.USER);
        
        return profileRepository.save(profile);
    }

    @Override
    public void updateProfile(Profile profile, ProfileDto profileDto, MultipartFile uploadedImage) {
        if(uploadedImage != null && uploadedImage.getSize() != 0) profile.setImage(imageService.saveImage(profile, uploadedImage));
        profile.setPhone(profileDto.getPhone());
        profile.setAboutMe(profileDto.getAboutMe());
        profileRepository.save(profile);

        // SBM entferne test
        notificationService.sendNotification(NotificationType.EMAIL, profile, profile, "notification.profile.updated");
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return profileRepository.existsByusername(username);
    }

    @Override
    public boolean isEmailTaken(String email){
        return profileRepository.existsByemail(email);
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
    public void deleteProfile(Profile profile) {
        // TODO SBM delete profile not user - OAuth
        userRepository.delete(profile.getUser());
    }

    @Override
    public void deleteProfileImage(Profile profile) {
        profile.setImage(null);
        profileRepository.save(profile);
    }
}

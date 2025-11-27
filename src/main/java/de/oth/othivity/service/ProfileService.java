package de.oth.othivity.service;

import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.enumeration.Language;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import de.oth.othivity.model.security.User;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.dto.ProfileDto;
import java.util.Locale;


@Service
public interface ProfileService {

    List<Club> allJoinedClubsByProfile(HttpSession session);

    Profile createProfileFromUser(User user, RegisterDto registerDto, Locale clientLocale, boolean needSetup, boolean setEmailVerified);
    
    void updateProfile(Profile profile, ProfileDto profileDto, MultipartFile uploadedImage);

    void updateProfileLanguage(Profile profile, Locale clientLocale);

    void updateProfileLanguage(Profile profile, Language language);

    boolean isUsernameTaken(String userName);

    boolean isEmailTaken(String email);

    Profile getProfileById(java.util.UUID profileId);

    Profile getProfileByUsername(String username);

    Profile getCurrentProfile(HttpSession session);

    void deleteProfile(Profile profile);

    void deleteProfileImage(Profile profile);

    ProfileDto profileToDto(Profile profile);

    boolean isEmailVerified(Profile profile);

    boolean isSetupComplete(Profile profile);

    Profile setUsername(Profile profile, String username);

    void setVerificationForEmail(Profile profile);

}

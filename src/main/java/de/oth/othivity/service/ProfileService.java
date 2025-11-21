package de.oth.othivity.service;

import de.oth.othivity.model.main.Club;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.List;
import de.oth.othivity.model.security.User;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.dto.ProfileDto;

@Service
public interface ProfileService {

    List<Club> allJoinedClubsByProfile(HttpSession session);

    Profile createProfileFromUser(User user, RegisterDto registerDto);
    
    void updateProfile(Profile profile, ProfileDto profileDto);

    boolean isusernameTaken(String userName);

    Profile getProfileById(java.util.UUID profileId);

    Profile getProfileByUsername(String username);

    Profile getCurrentProfile(HttpSession session);

    void deleteProfile(Profile profile);
}

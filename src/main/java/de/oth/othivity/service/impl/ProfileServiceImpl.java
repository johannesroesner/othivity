package de.oth.othivity.service.impl;

import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.repository.main.ProfileRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {
    private final SessionService sessionService;
    private final ProfileRepository profileRepository;

    @Override
    public List<Club> allJoinedClubsByProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();
        return profile.getClubs();
    }

    @Override
    public Profile createProfileFromUser(User user, RegisterDto registerRequest) {
        // Implementation here
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFirstName(registerRequest.getFirstName());
        profile.setLastName(registerRequest.getLastName());
        profile.setEmail(registerRequest.getEmail());
        
        return profileRepository.save(profile);
    }
}

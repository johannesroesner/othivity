package de.oth.othivity.service.impl;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    private static final String PROFILE_ID_KEY = "profileId";

    private final ProfileRepository profileRepository;

    public UUID profileId ;

    public SessionServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getProfileFromSession(HttpSession session) {
        //UUID profileId = (UUID) session.getAttribute(PROFILE_ID_KEY);
        //UUID  profileId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
        /*
        if (profileId == null) {
            return null;
        }

         */
        return profileRepository.findById(profileId).orElse(null);
    }

}

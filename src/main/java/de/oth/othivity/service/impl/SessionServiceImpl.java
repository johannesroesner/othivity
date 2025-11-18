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
        return profileRepository.findById(profileId).orElse(null);
    }

}

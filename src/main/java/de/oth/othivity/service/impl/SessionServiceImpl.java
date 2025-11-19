package de.oth.othivity.service.impl;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    private final ProfileRepository profileRepository;

    public SessionServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getProfileFromSession(HttpSession session) {
        return profileRepository.findById((UUID) session.getAttribute("profileId")).orElse(null);
    }

}

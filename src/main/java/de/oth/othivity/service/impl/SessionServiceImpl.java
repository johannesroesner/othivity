package de.oth.othivity.service.impl;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import de.oth.othivity.model.main.Club;

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

    @Override
    public Boolean canEditActivity(HttpSession session, Activity activity) {
        Profile profile = getProfileFromSession(session);
        if (profile == null) return false;
        return activity.getStartedBy().getId().equals(profile.getId()) || profile.getRole().toString().equals("MODERATOR");
    }

    @Override
    public Boolean canJoinActivity(HttpSession session, Activity activity) {
        Profile profile = getProfileFromSession(session);
        if (profile == null) return false;
        return !activity.getStartedBy().getId().equals(profile.getId()) && activity.getTakePart().size() < activity.getGroupSize();
    }

    @Override
    public Boolean canEditActivity(HttpSession session, Activity activity) {
        Profile profile = getProfileFromSession(session);
        if (profile == null) return false;
        return activity.getStartedBy().getId().equals(profile.getId()) || profile.getRole().toString().equals("MODERATOR");
    }

    @Override
    public Boolean canJoinActivity(HttpSession session, Activity activity) {
        Profile profile = getProfileFromSession(session);
        if (profile == null) return false;
        return !activity.getStartedBy().getId().equals(profile.getId()) && activity.getTakePart().size() < activity.getGroupSize();
    }
    @Override
    public Boolean isMemberOfClub(HttpSession session, Club club) {
        Profile profile = getProfileFromSession(session);
        if (profile == null) return false;
        return profile.getClubs().stream().anyMatch(c -> c.getId().equals(club.getId()));
    }
    @Override
    public Boolean isAdminOfClub(HttpSession session, Club club) {
        Profile profile = getProfileFromSession(session);
        if (profile == null) return false;
        return profile.getAdminClubs().stream().anyMatch(c -> c.getId().equals(club.getId()));
    }

}

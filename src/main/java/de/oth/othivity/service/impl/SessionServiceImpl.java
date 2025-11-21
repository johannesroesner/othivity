package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Override
    public Boolean canEditActivity(HttpSession session, Activity activity) {
        Profile profile = getProfileFromSession(session);
        if (profile == null) return false;
        return activity.getStartedBy().getId().equals(profile.getId()) || profile.getRole().toString().equals("MODERATOR");
    }

    @Override
    public <T> Boolean canUpdate(HttpSession session, T entity) {
        if (entity instanceof Activity) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;
            return ((Activity) entity).getStartedBy().getId().equals(profile.getId()) || profile.getRole().equals(Role.MODERATOR);
        } else if (entity instanceof Profile) {
            Profile currentProfile = getProfileFromSession(session);
            if (currentProfile == null) return false;
            return currentProfile.getId().equals(((Profile) entity).getId()) || currentProfile.getRole().equals(Role.MODERATOR);
        }
        return false;
    }

    @Override
    public <T> Boolean canDelete(HttpSession session, T entity) {
        if (entity instanceof Activity) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;
            return ((Activity) entity).getStartedBy().getId().equals(profile.getId()) || profile.getRole().equals(Role.MODERATOR);
        } else if (entity instanceof Profile) {
            Profile currentProfile = getProfileFromSession(session);
            if (currentProfile == null) return false;
            return currentProfile.getId().equals(((Profile) entity).getId()) || currentProfile.getRole().equals(Role.MODERATOR);
        }
        return false;
    }

    @Override
    public Boolean canJoinActivity(HttpSession session, Activity activity) {
        Profile profile = getProfileFromSession(session);
        if (profile == null) return false;
        return !activity.getStartedBy().getId().equals(profile.getId()) && activity.getTakePart().size() < activity.getGroupSize();
    }

    @Override
    public String getReturnUrlFromSession(HttpSession session, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        
        // Hier "/settings" zur Ausschlussliste hinzufügen
        if (referer != null 
            && !referer.contains("/profile/") 
            && !referer.contains("/settings")
            && !referer.contains("/login") 
            && !referer.contains("/register") 
            && !referer.contains("/error")) {
            
            session.setAttribute("profileReturnUrl", referer);
        }
        
        String returnUrl = (String) session.getAttribute("profileReturnUrl");
        if (returnUrl == null) returnUrl = "/dashboard";
        return returnUrl;
    }

}

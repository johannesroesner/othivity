package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.ISessionService;
import de.oth.othivity.model.enumeration.AccessLevel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver; 

import de.oth.othivity.model.main.Club;

import java.util.Locale;
import java.util.UUID;
import java.util.Deque;
import java.util.ArrayDeque;
import java.net.URI;

@AllArgsConstructor
@Service
public class SessionService implements ISessionService {

    private final ProfileRepository profileRepository;
    private final LocaleResolver localeResolver;

    @Override
    public Profile getProfileFromSession(HttpSession session) {
        UUID profileId = (UUID) session.getAttribute("profileId");

        if (profileId == null) {
            return null;
        }

        return profileRepository.findById(profileId).orElse(null);
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
        }else if (entity instanceof Club) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;
            return profile.getAdminClubs().stream().anyMatch(c -> c.getId().equals(((Club) entity).getId())|| profile.getRole().equals(Role.MODERATOR));
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
        }else if (entity instanceof Club) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;
            return profile.getAdminClubs().stream().anyMatch(c -> c.getId().equals(((Club) entity).getId())|| profile.getRole().equals(Role.MODERATOR));
        }
        return false;
    }

    @Override
    public <T> Boolean canJoin(HttpSession session, T entity) {
        if (entity instanceof Activity activity) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;
            boolean isNotCreator = !activity.getStartedBy().getId().equals(profile.getId());
            boolean hasNotJoined = activity.getTakePart().stream().noneMatch(p -> p.getId().equals(profile.getId()));
            return isNotCreator && hasNotJoined && activity.getTakePart().size() < activity.getGroupSize();
        }else if( entity instanceof Club club) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;
            return profile.getClubs().stream().noneMatch(c -> c.getId().equals(club.getId()))&& (club.getAccessLevel().equals(AccessLevel.OPEN));
        }
        return false;
    }

    @Override
    public <T> Boolean canJoinOnInvite(HttpSession session, T entity) {
        if (entity instanceof Club club) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;
            return profile.getClubs().stream().noneMatch(c -> c.getId().equals(club.getId())) && (club.getAccessLevel().equals(AccessLevel.ON_INVITE));
        }
        return false;
    }

    @Override
    public Boolean canMessage(HttpSession session, Profile profile) {
        return !getProfileFromSession(session).getId().equals(profile.getId());
    }

    @Override
    public <T> Boolean canLeave(HttpSession session, T entity) {
        if (entity instanceof Activity activity) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;

            boolean isCreator = activity.getStartedBy().getId().equals(profile.getId());
            boolean isParticipant = activity.getTakePart().stream().anyMatch(p -> p.getId().equals(profile.getId()));

            return !isCreator && isParticipant;
        }else if( entity instanceof Club club) {
            Profile profile = getProfileFromSession(session);
            if (profile == null) return false;
            return profile.getClubs().stream().anyMatch(c -> c.getId().equals(club.getId()));
        }
        return false;
    }

    @Override
    public String getReturnUrlFromSession(HttpSession session, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String currentUri = request.getRequestURI();

        @SuppressWarnings("unchecked")
        Deque<String> urlHistory = (Deque<String>) session.getAttribute("urlHistory");
        if (urlHistory == null) {
            urlHistory = new ArrayDeque<>(10);
            session.setAttribute("urlHistory", urlHistory);
        }

        if ("GET".equalsIgnoreCase(request.getMethod()) && referer != null) {
            String refererPath = extractPath(referer);
            if (isValidHistoryUrl(refererPath, currentUri)) {
                if (urlHistory.isEmpty() || !urlHistory.peekLast().equals(refererPath)) {
                    urlHistory.addLast(refererPath);
                    if (urlHistory.size() > 10) {
                        urlHistory.removeFirst();
                    }
                }
            }
        }

        String returnUrl = null;
        while (!urlHistory.isEmpty()) {
            String lastUrl = urlHistory.pollLast();

            if (lastUrl != null && !lastUrl.equals(currentUri) && !currentUri.startsWith(lastUrl)) {
                returnUrl = lastUrl;
                break;
            }
        }

        if (returnUrl == null) {
            if (currentUri.startsWith("/activities")) {
                returnUrl = "/activities";
            } else if (currentUri.startsWith("/clubs")) {
                returnUrl = "/clubs";
            } else if (currentUri.startsWith("/profile")) {
                returnUrl = "/dashboard";
            } else {
                returnUrl = "/dashboard";
            }
        }

        return returnUrl;
    }

    private String extractPath(String url) {
        if (url == null) return null;
        try {
            java.net.URI uri = new java.net.URI(url);
            String path = uri.getPath();
            String query = uri.getQuery();
            return query != null ? path + "?" + query : path;
        } catch (Exception e) {
            return url;
        }
    }

    private boolean isValidHistoryUrl(String url, String currentUri) {
        if (url == null || url.equals(currentUri)) return false;
        
        return !url.contains("/login") 
            && !url.contains("/register") 
            && !url.contains("/error") 
            && !url.contains("/logout")
            && !url.contains("/settings")
            && !url.equals("/");
    }

    @Override
    public void updateLocaleResolverWithProfileLanguage(HttpServletRequest request, HttpServletResponse response, Profile profile) {
        if (profile == null || profile.getLanguage() == null) {
            return;
        }
        String languageCode = profile.getLanguage().getLocaleCode();
        Locale targetLocale = Locale.forLanguageTag(languageCode);
        localeResolver.setLocale(request, response, targetLocale);
    }
}

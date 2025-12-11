package de.oth.othivity.service;

import de.oth.othivity.model.main.Profile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;


@Service
public interface ISessionService {

    Profile getProfileFromSession(HttpSession session);

    <T> Boolean canUpdate(HttpSession session, T entity);

    <T> Boolean canDelete(HttpSession session, T entity);

    <T> Boolean canJoin(HttpSession session, T entity);

    <T> Boolean canLeave(HttpSession session, T entity);

    <T> Boolean canJoinOnInvite(HttpSession session, T entity);

    Boolean canMessage(HttpSession session, Profile profile);

    String getReturnUrlFromSession(HttpSession session, HttpServletRequest request);

    void updateLocaleResolverWithProfileLanguage(HttpServletRequest request, HttpServletResponse response, Profile profile);

}

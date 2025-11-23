package de.oth.othivity.service;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;


@Service
public interface SessionService {

    Profile getProfileFromSession(HttpSession session);

    <T> Boolean canUpdate(HttpSession session, T entity);

    <T> Boolean canDelete(HttpSession session, T entity);

    <T> Boolean canJoin(HttpSession session, T entity);

    <T> Boolean canLeave(HttpSession session, T entity);

    String getReturnUrlFromSession(HttpSession session, HttpServletRequest request);

}

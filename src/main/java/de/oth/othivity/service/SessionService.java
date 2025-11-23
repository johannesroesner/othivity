package de.oth.othivity.service;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import de.oth.othivity.model.main.Club;


@Service
public interface SessionService {

    Profile getProfileFromSession(HttpSession session);
    
    Boolean canEditActivity(HttpSession session, Activity activity);

    Boolean canJoinActivity(HttpSession session, Activity activity);

    Boolean canEditClub(HttpSession session, Club club);

    Boolean canJoinClub(HttpSession session, Club club);

    Boolean canLeaveClub(HttpSession session, Club club);

    <T> Boolean canUpdate(HttpSession session, T entity);

    <T> Boolean canDelete(HttpSession session, T entity);

    String getReturnUrlFromSession(HttpSession session, HttpServletRequest request);

}

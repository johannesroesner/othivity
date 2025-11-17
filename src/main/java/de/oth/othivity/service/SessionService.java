package de.oth.othivity.service;

import de.oth.othivity.model.main.Profile;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SessionService {

    Profile getProfileFromSession(HttpSession session);

}

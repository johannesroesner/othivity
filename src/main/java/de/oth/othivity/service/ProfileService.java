package de.oth.othivity.service;

import de.oth.othivity.model.main.Club;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import de.oth.othivity.model.security.User;
import de.oth.othivity.dto.RegisterRequest;
import de.oth.othivity.model.main.Profile;

@Service
public interface ProfileService {

    List<Club> allJoinedClubsByProfile(HttpSession session);

    Profile createProfileFromUser(User user, RegisterRequest registerRequest);

}

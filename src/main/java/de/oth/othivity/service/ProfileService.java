package de.oth.othivity.service;

import de.oth.othivity.model.main.Club;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProfileService {
    List<Club> allJoinedClubsByProfile(HttpSession session);
}

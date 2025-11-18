package de.oth.othivity.service;

import de.oth.othivity.model.main.Club;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClubService {

    List<Club> getAllClubs();

    List<Club> getClubsJoinedByProfile(HttpSession session);
    
    List<Club> getClubsManagedByProfile(HttpSession session);

    List<Club> getClubsNotJoinedByProfileNotPrivate(HttpSession session);

}

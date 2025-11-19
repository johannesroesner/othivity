package de.oth.othivity.service;

import de.oth.othivity.model.main.Club;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import de.oth.othivity.dto.ClubDto;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.UUID;

@Service
public interface ClubService {

    List<Club> getAllClubs();

    Club getClubById(UUID clubId);

    List<Club> getClubsJoinedByProfile(HttpSession session);
    
    List<Club> getClubsManagedByProfile(HttpSession session);

    List<Club> getClubsNotJoinedByProfileNotPrivate(HttpSession session);

    Club createClubForUser(ClubDto clubDto, HttpSession session, MultipartFile[] uploadedImages);

    ClubDto clubToDto (Club club);
}

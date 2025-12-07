package de.oth.othivity.service;

import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Club;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import de.oth.othivity.dto.ClubDto;
import de.oth.othivity.model.main.Profile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.UUID;

@Service
public interface ClubService {

    List<Club> getAllClubs();

    Club getClubById(UUID clubId);

    List<Club> getClubsJoinedByProfile(HttpSession session);
    
    Page<Club> getClubsJoinedByProfile(HttpSession session, Pageable pageable, String search, AccessLevel accessLevel);
    
    List<Club> getClubsManagedByProfile(HttpSession session);

    List<Club> getClubsNotJoinedByProfile(HttpSession session);
    
    Page<Club> getClubsNotJoinedByProfile(HttpSession session, Pageable pageable, String search, AccessLevel accessLevel);

    Club createClubForUser(ClubDto clubDto, Profile profile, MultipartFile uploadedImage);

    Club updateClub(Club club, ClubDto clubDto, MultipartFile uploadedImage, HttpSession session);

    List<Activity> getActivitiesByClub(Club club);

    ClubDto clubToDto (Club club);

    List<Profile> getMembersOfClubWithoutAdmins(Club club);

    void joinClubForProfile(HttpSession session, Club club);

    void leaveClubForProfile(HttpSession session, Club club);

    boolean wouldLeaveRequireAdminSelection(HttpSession session, Club club);

    void deleteClub(Club club, Profile profile);

    void makeProfileAdminOfClub(Profile profile, Club club, HttpSession session);

    void removeProfileFromClub(Profile profile, Club club, HttpSession session);

}

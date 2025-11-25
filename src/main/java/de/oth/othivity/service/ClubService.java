package de.oth.othivity.service;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Club;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import de.oth.othivity.dto.ClubDto;
import de.oth.othivity.model.main.Profile;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.UUID;

@Service
public interface ClubService {

    List<Club> getAllClubs();

    Club getClubById(UUID clubId);

    List<Club> getClubsJoinedByProfile(HttpSession session);
    
    List<Club> getClubsManagedByProfile(HttpSession session);

    List<Club> getClubsNotJoinedByProfile(HttpSession session);

    Club createClubForUser(ClubDto clubDto, HttpSession session, MultipartFile uploadedImage);

    Club updateClub(Club club, ClubDto clubDto, MultipartFile uploadedImage, HttpSession session);

    List<Activity> getActivitiesByClub(Club club);

    ClubDto clubToDto (Club club);

    List<Profile> getMembersOfClubWithoutAdmins(Club club);

    void joinClubForProfile(HttpSession session, Club club);

    void leaveClubForProfile(HttpSession session, Club club);

    boolean wouldLeaveRequireAdminSelection(HttpSession session, Club club);

    void deleteClub(Club club, HttpSession session);

    void makeProfileAdminOfClub(Profile profile, Club club, HttpSession session);

    void removeProfileFromClub(Profile profile, Club club, HttpSession session);

}

package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.SessionService;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import lombok.AllArgsConstructor;

@AllArgsConstructor 
@Service
public class ClubServiceImpl implements ClubService {
    private final SessionService sessionService;
    private final ClubRepository clubRepository;

    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }
    @Override
    public List<Club> getClubsJoinedByProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();
        return profile.getClubs();
    }
    @Override
    public List<Club> getClubsManagedByProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();
        return profile.getAdminClubs();
    }       
    @Override
    public List<Club> getClubsNotJoinedByProfileNotPrivate(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();
        List<Club> allClubs = new ArrayList<>(clubRepository.findByAccessLevelNot(AccessLevel.CLOSED));
        allClubs.removeAll(profile.getClubs());
        return allClubs;
    }
    @Override
    public Club createClubForUser(Club club, HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) { return null; }
        club.getMembers().add(profile);
        club.getAdmins().add(profile);
        return clubRepository.save(club);
    }
}

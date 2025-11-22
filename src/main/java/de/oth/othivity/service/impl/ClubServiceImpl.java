package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.repository.main.ActivityRepository;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import de.oth.othivity.dto.ClubDto;
import org.springframework.web.multipart.MultipartFile;
import de.oth.othivity.service.ImageService;
import java.util.UUID;

@AllArgsConstructor 
@Service
public class ClubServiceImpl implements ClubService {
    private final SessionService sessionService;
    private final ClubRepository clubRepository;
    private final ImageService imageService;
    private final ActivityRepository activityRepository;

    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }
    @Override
    public Club getClubById(UUID clubId) {
        return clubRepository.findById(clubId).orElse(null);
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
    public List<Club> getClubsNotJoinedByProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();
        List<Club> allClubs = new ArrayList<>(clubRepository.findAll());
        allClubs.removeAll(profile.getClubs());
        return allClubs;
    }
    @Override
    public Club createClubForUser(ClubDto clubDto, HttpSession session, MultipartFile[] uploadedImages) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) { return null; }
        Club club = new Club();
        club.setName(clubDto.getName());
        club.setDescription(clubDto.getDescription());
        club.setAccessLevel(clubDto.getAccessLevel());
        club.setAddress(clubDto.getAddress());
    
        club.getMembers().add(profile);
        club.getAdmins().add(profile);
        Club savedClub = clubRepository.save(club);
        imageService.saveImagesForClub(savedClub, uploadedImages);
        
        return savedClub;
    }
    @Override
    public List<Activity> getActivitiesByClub(Club club) {
        return activityRepository.findAllByOrganizer(club);
    }
    @Override
    public ClubDto clubToDto(Club club) {
        if (club == null) {
            return null;
        }
        ClubDto clubDto = new ClubDto();
        clubDto.setName(club.getName());
        clubDto.setDescription(club.getDescription());
        clubDto.setAccessLevel(club.getAccessLevel());
        clubDto.setAddress(club.getAddress());
        return clubDto;
    }
    @Override
    public List<Profile> getMembersOfClubWithoutAdmins(Club club) {
        List<Profile> membersWithoutAdmins = new ArrayList<>(club.getMembers());
        membersWithoutAdmins.removeAll(club.getAdmins());
        return membersWithoutAdmins;
    }
}
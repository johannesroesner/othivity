package de.oth.othivity.service.impl;
import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.model.helper.ClubJoinRequest;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.helper.ClubJoinRequestRepository;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.service.ClubJoinRequestService;
import de.oth.othivity.service.INotificationService;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.dto.ClubJoinRequestDto;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ClubJoinRequestServiceImpl implements ClubJoinRequestService {

    private final ClubRepository clubRepository;
    private final ClubJoinRequestRepository clubJoinRequestRepository;
    private final ClubService clubService;
    private final INotificationService notificationService;

    @Override
    public List<ClubJoinRequest> getJoinRequestsForClub(UUID clubId) {
        Club club = clubService.getClubById(clubId);
        if (club == null) {
            return List.of();
        }
        return clubJoinRequestRepository.findAllByClub(club);
    }
    
    @Override
    public ClubJoinRequest createJoinRequest(ClubJoinRequestDto clubJoinRequestDto, Profile profile) {
        Club club = clubService.getClubById(clubJoinRequestDto.getClubId());
        if (club == null) {
            throw new IllegalArgumentException("Club not found");
        }
        
        ClubJoinRequest joinRequest = new ClubJoinRequest();
        joinRequest.setClub(club);
        joinRequest.setProfile(profile);
        joinRequest.setText(clubJoinRequestDto.getText());
        ClubJoinRequest savedRequest = clubJoinRequestRepository.save(joinRequest);
        for (Profile admin : club.getAdmins()) {
            notificationService.sendNotification(NotificationType.PUSH_NOTIFICATION, club, admin, "notification.club.join.request.created", profile);
        }
        return savedRequest;
    }
    @Override
    public void acceptJoinRequest(UUID requestId) {
        ClubJoinRequest joinRequest = clubJoinRequestRepository.findById(requestId).orElse(null);
        if (joinRequest != null) {
            Club club = joinRequest.getClub();
            Profile profile = joinRequest.getProfile();
            club.getMembers().add(profile);
            clubRepository.save(club);
            clubJoinRequestRepository.delete(joinRequest);
            notificationService.sendNotification(NotificationType.PUSH_NOTIFICATION, club, profile, "notification.club.join.request.accepted");
        }
    }
    
    @Override
    public void rejectJoinRequest(UUID requestId) {
        ClubJoinRequest joinRequest = clubJoinRequestRepository.findById(requestId).orElse(null);
        if (joinRequest != null) {
            clubJoinRequestRepository.delete(joinRequest);
            notificationService.sendNotification(NotificationType.PUSH_NOTIFICATION, joinRequest.getClub(), joinRequest.getProfile(), "notification.club.join.request.declined");
        }
    }
}
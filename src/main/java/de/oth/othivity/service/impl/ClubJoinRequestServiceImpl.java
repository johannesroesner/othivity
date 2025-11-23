package de.oth.othivity.service.impl;
import de.oth.othivity.model.helper.ClubJoinRequest;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.helper.ClubJoinRequestRepository;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.service.ClubJoinRequestService;
import de.oth.othivity.service.ClubService;
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

    @Override
    public List<ClubJoinRequest> getJoinRequestsForClub(UUID clubId) {
        Club club = clubService.getClubById(clubId);
        if (club == null) {
            return List.of();
        }
        return clubJoinRequestRepository.findAllByClub(club);
    }
    
    @Override
    public ClubJoinRequest createJoinRequest(UUID clubId, Profile profile, String message) {
        Club club = clubService.getClubById(clubId);
        if (club == null) {
            throw new IllegalArgumentException("Club not found");
        }
        
        ClubJoinRequest joinRequest = new ClubJoinRequest();
        joinRequest.setClub(club);
        joinRequest.setProfile(profile);
        joinRequest.setText(message);
        
        return clubJoinRequestRepository.save(joinRequest);
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
        }
    }
    
    @Override
    public void rejectJoinRequest(UUID requestId) {
        ClubJoinRequest joinRequest = clubJoinRequestRepository.findById(requestId).orElse(null);
        if (joinRequest != null) {
            clubJoinRequestRepository.delete(joinRequest);
        }
    }
}
package de.oth.othivity.service;
import de.oth.othivity.model.helper.ClubJoinRequest;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.dto.ClubJoinRequestDto;
import java.util.List;
import java.util.UUID;

public interface ClubJoinRequestService {

    List<ClubJoinRequest> getJoinRequestsForClub(UUID clubId);
    
    ClubJoinRequest createJoinRequest(ClubJoinRequestDto clubJoinRequestDto, Profile profile);

    void acceptJoinRequest(UUID requestId);

    void rejectJoinRequest(UUID requestId);
    
}

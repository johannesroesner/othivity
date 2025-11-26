package de.oth.othivity.service;
import de.oth.othivity.model.helper.ClubJoinRequest;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.dto.ClubJoinRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ClubJoinRequestService {

    List<ClubJoinRequest> getJoinRequestsForClub(UUID clubId);
    
    ClubJoinRequest createJoinRequest(ClubJoinRequestDto clubJoinRequestDto, Profile profile);

    void acceptJoinRequest(UUID requestId);

    void rejectJoinRequest(UUID requestId);
    
}

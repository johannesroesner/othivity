package de.oth.othivity.service;
import de.oth.othivity.model.helper.ClubJoinRequest;
import de.oth.othivity.model.main.Profile;
import java.util.List;
import java.util.UUID;

public interface ClubJoinRequestService {

    List<ClubJoinRequest> getJoinRequestsForClub(UUID clubId);
    
    ClubJoinRequest createJoinRequest(UUID clubId, Profile profile, String message);

    void acceptJoinRequest(UUID requestId);

    void rejectJoinRequest(UUID requestId);
    
}

package de.oth.othivity.repository.helper;

import de.oth.othivity.model.helper.ClubJoinRequest;
import de.oth.othivity.model.main.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClubJoinRequestRepository extends JpaRepository<ClubJoinRequest, UUID> {
    
    List<ClubJoinRequest> findAllByClub(Club club);

}

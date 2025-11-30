package de.oth.othivity.repository.helper;

import de.oth.othivity.model.helper.ClubJoinRequest;
import de.oth.othivity.model.main.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClubJoinRequestRepository extends JpaRepository<ClubJoinRequest, UUID> {
    
    List<ClubJoinRequest> findAllByClub(Club club);

}

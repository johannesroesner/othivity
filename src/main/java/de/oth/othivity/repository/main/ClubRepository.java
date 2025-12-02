package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import de.oth.othivity.model.enumeration.AccessLevel;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import de.oth.othivity.model.main.Profile;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {
    List<Club> findByAccessLevelNot(AccessLevel accessLevel);
    
    @Query("SELECT c FROM Club c WHERE :profile MEMBER OF c.members AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR :search IS NULL) AND (:accessLevel IS NULL OR c.accessLevel = :accessLevel)")
    Page<Club> findClubsJoinedByProfile(@Param("profile") Profile profile, @Param("search") String search, @Param("accessLevel") AccessLevel accessLevel, Pageable pageable);
    
    @Query("SELECT c FROM Club c WHERE :profile NOT MEMBER OF c.members AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR :search IS NULL) AND (:accessLevel IS NULL OR c.accessLevel = :accessLevel)")
    Page<Club> findClubsNotJoinedByProfile(@Param("profile") Profile profile, @Param("search") String search, @Param("accessLevel") AccessLevel accessLevel, Pageable pageable);
}

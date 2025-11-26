package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import de.oth.othivity.model.enumeration.AccessLevel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {
    List<Club> findByAccessLevelNot(AccessLevel accessLevel);
}

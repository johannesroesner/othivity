package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import de.oth.othivity.model.enumeration.AccessLevel;
import java.util.List;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<Club, UUID> {
    List<Club> findByAccessLevelNot(AccessLevel accessLevel);
}

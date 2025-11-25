package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findAllByOrganizer(Club organizer);
}

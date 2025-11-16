package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Activity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

}

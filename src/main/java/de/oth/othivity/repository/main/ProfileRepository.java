package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    boolean existsByusername(String username);

    boolean existsByemail(String email);

    Profile findByusername(String username);

}

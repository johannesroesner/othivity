package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    boolean existsByusername(String username);

    boolean existsByemail(String email);

    Profile findByusername(String username);

}

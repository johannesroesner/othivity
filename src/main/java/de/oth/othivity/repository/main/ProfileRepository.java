package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    boolean existsByusername(String username);

    boolean existsByEmailAddress(String address);

    Profile findByusername(String username);

    Profile findByEmailAddress(String address);
}

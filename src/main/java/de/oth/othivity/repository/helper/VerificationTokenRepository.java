package de.oth.othivity.repository.helper;

import de.oth.othivity.model.helper.VerificationToken;
import de.oth.othivity.model.main.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    VerificationToken findByToken(String token);

    VerificationToken findByProfile(Profile profile);
}

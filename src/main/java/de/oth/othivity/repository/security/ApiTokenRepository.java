package de.oth.othivity.repository.security;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiTokenRepository extends JpaRepository<ApiToken, UUID> {
    
    List<ApiToken> findAllByProfile(Profile profile);
    
    Optional<ApiToken> findByTokenIdentifier(String tokenIdentifier);
    
    void deleteByIdAndProfile(UUID id, Profile profile);
}
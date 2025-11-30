package de.oth.othivity.service.impl;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.ApiToken;
import de.oth.othivity.model.security.CustomUserDetails;
import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.security.ApiTokenRepository;
import de.oth.othivity.service.IApiTokenService;
import de.oth.othivity.service.IJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiTokenService implements IApiTokenService {

    private final ApiTokenRepository tokenRepository;
    private final IJwtService jwtService;

    @Transactional
    public String createToken(Profile profile, String name, int durationInMonths) {
        String tokenIdentifier = UUID.randomUUID().toString();
        User user = profile.getUser();

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtString = jwtService.generateApiToken(userDetails, tokenIdentifier, durationInMonths);

        ApiToken apiToken = ApiToken.builder()
                .name(name)
                .tokenIdentifier(tokenIdentifier)
                .profile(profile)
                .expiresAt(LocalDateTime.now().plusMonths(durationInMonths))
                .build();

        tokenRepository.save(apiToken);

        return jwtString;
    }

    public List<ApiToken> getProfileTokens(Profile profile) {
        return tokenRepository.findAllByProfile(profile);
    }

    @Transactional
    public void revokeToken(UUID tokenId, Profile profile) {
        tokenRepository.deleteByIdAndProfile(tokenId, profile);
    }

    public boolean isTokenActive(String tokenIdentifier) {
        return tokenRepository.findByTokenIdentifier(tokenIdentifier).isPresent();
    }
}
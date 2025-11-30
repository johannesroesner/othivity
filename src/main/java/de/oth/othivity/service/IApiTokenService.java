package de.oth.othivity.service;

import java.util.List;
import java.util.UUID;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.ApiToken;

public interface IApiTokenService {

    public String createToken(Profile profile, String name, int durationInMonths);

    public List<ApiToken> getProfileTokens(Profile profile);

    public void revokeToken(UUID tokenId, Profile profile);

    public boolean isTokenActive(String tokenIdentifier);

}

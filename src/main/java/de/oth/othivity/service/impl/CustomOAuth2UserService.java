package de.oth.othivity.service.impl;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.model.security.User;
import de.oth.othivity.service.IUserService;
import de.oth.othivity.service.IProfileService;
import de.oth.othivity.service.ICustomOAuth2UserService;
import lombok.AllArgsConstructor;

import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CustomOAuth2UserService extends OidcUserService implements ICustomOAuth2UserService {
    
    private final IUserService userService;
    private final IProfileService profileService;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        
        OidcUser oidcUser = super.loadUser(userRequest);

        String emailAddress = oidcUser.getEmail();
        String name = oidcUser.getAttribute("name");

        User user = userService.getUserByEmail(emailAddress);
        if (user == null) {

            RegisterDto registerDto = new RegisterDto();
            registerDto.setEmail(emailAddress);
            registerDto.setUsername(UUID.randomUUID().toString());
            registerDto.setFirstName(name != null && name.contains(" ") ? name.split(" ")[0] : name);
            registerDto.setLastName(name != null && name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "");
            registerDto.setPassword(UUID.randomUUID().toString());

            User newUser = userService.registerNewUserAccount(registerDto, Locale.ENGLISH, true, false);

            profileService.setVerificationForEmail(newUser.getProfile());
        }

        return oidcUser;
    }
}
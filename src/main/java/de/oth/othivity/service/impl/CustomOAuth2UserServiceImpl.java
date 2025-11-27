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
import lombok.AllArgsConstructor;

import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CustomOAuth2UserServiceImpl extends OidcUserService {
    
    private final IUserService userService;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");

        User user = userService.getUserByEmail(email);
        if (user == null) {

            RegisterDto registerDto = new RegisterDto();
            registerDto.setEmail(email);
            registerDto.setUsername(email.split("@")[0]);
            registerDto.setFirstName(name != null && name.contains(" ") ? name.split(" ")[0] : name);
            registerDto.setLastName(name != null && name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "");
            registerDto.setPassword(UUID.randomUUID().toString());


            userService.registerNewUserAccount(registerDto, Locale.ENGLISH);
        }

        return oidcUser;
    }
}
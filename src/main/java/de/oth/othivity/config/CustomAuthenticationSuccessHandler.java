package de.oth.othivity.config;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.CustomUserDetails;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.ISessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ISessionService sessionService;
    private final ProfileRepository profileRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Profile profile = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            profile = userDetails.getUser().getProfile();
        } 
        else if (principal instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) principal;
            String email = oidcUser.getEmail();

            profile = profileRepository.findByEmailAddress(email).orElse(null);

        }
        if (profile != null) {
            HttpSession session = request.getSession();
            session.setAttribute("profileId", profile.getId());
            session.setAttribute("role", profile.getRole());
            sessionService.updateLocaleResolverWithProfileLanguage(request, response, profile);
        } 
        response.sendRedirect("/dashboard");
    }
}
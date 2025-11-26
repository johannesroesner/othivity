package de.oth.othivity.config;
import de.oth.othivity.model.security.CustomUserDetails;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.model.main.Profile;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionService sessionService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Profile profile = userDetails.getUser().getProfile();

        HttpSession session = request.getSession();
        session.setAttribute("profileId", userDetails.getProfileId());

        if (profile != null) {
            session.setAttribute("role", profile.getRole());
            sessionService.updateLocaleResolverWithProfileLanguage(request, response, profile);
        }
        response.sendRedirect("/dashboard");
    }
}
package de.oth.othivity.config;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.ISessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class SetupVerificationInterceptor implements HandlerInterceptor {

    private final ISessionService ISessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return true; 
        }

        String uri = request.getRequestURI();
        
        if (uri.equals("/setup") || 
            uri.equals("/verify-email") || 
            uri.equals("/logout") ||
            uri.startsWith("/api")) {
            return true;
        }

        Profile profile = ISessionService.getProfileFromSession(session);
        if (profile == null) {
            return true; 
        }

        if (profile.getSetupComplete() == null || !profile.getSetupComplete()) {
            response.sendRedirect("/setup");
            return false;
        }

        if (profile.getEmail() == null || !profile.getEmail().getVerified()) {
            response.sendRedirect("/verify-email");
            return false;
        }

        return true;
    }
}

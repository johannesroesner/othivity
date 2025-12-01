package de.oth.othivity.config;

import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SetupVerificationInterceptor setupVerificationInterceptor;

    // needed because of optional bean in test profile
    public WebMvcConfig(Optional<SetupVerificationInterceptor> setupVerificationInterceptor) {
        this.setupVerificationInterceptor = setupVerificationInterceptor.orElse(null);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (setupVerificationInterceptor != null) {
            registry.addInterceptor(setupVerificationInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(
                        "/", 
                        "/login", 
                        "/register", 
                        "/process-register", 
                        "/process-login",
                        "/oauth2/**",
                        "/h2-console/**",
                        "/profile/username/update",
                        "/profile/email/verify",
                        "/api/**"
                    );
        }
    }
}

package de.oth.othivity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final SetupVerificationInterceptor setupVerificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
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

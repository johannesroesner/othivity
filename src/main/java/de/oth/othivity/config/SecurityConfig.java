package de.oth.othivity.config;

import de.oth.othivity.service.CustomUserDetailsService;
import de.oth.othivity.model.security.CustomUserDetails;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**")  // Nur H2-Console ohne CSRF
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/process-register", "/h2-console/**").permitAll()  // Fixed: Added /process-register and fixed /h2-console path
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/process-login")
                        .usernameParameter("email")  // Use email field as username
                        .defaultSuccessUrl("/dashboard", true) //(... , true) to always redirect to dashboard
                        .successHandler((request, response, authentication) -> {
                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                            request.getSession().setAttribute("profileId", userDetails.getProfileId());
                            response.sendRedirect("/dashboard");
                        })
                        .permitAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

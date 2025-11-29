package de.oth.othivity.config;

import de.oth.othivity.model.security.CustomUserDetails;
import de.oth.othivity.service.impl.CustomUserDetailsService;

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
    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**")  // Nur H2-Console ohne CSRF
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/process-register", "/h2-console/**", "/test.png","/bridge.png","/moritz.png","/sebastian.png","/johannes.png", "/fav.ico").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // TODO delete when H2 not needed anymore
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/process-login")
                .usernameParameter("email")
                .successHandler(successHandler) 
                
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

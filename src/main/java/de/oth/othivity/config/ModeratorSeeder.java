package de.oth.othivity.config;


import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.helper.Email;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.security.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ModeratorSeeder {

    @Value("${moderator.password}")
    private String moderatorPassword;

    @Bean
    CommandLineRunner loadTestData(ProfileRepository profileRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {

        return args -> {

            User user = new User();
            user.setEmail("moderator@example.com");
            user.setPassword(passwordEncoder.encode(moderatorPassword));
            userRepository.save(user);

            Profile profile = new Profile();
            profile.setFirstName("moderator");
            profile.setLastName("moderator");
            profile.setUsername("moderator");
            Email email = new Email("moderator@example.com");
            email.setVerified(true);
            profile.setEmail(email);
            profile.setAboutMe("moderator");
            profile.setRole(Role.MODERATOR);
            profile.setLanguage(Language.ENGLISH);
            profile.setUser(user);
            profile.setSetupComplete(true);
            profileRepository.save(profile);
        };
    }
}

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

            User moderator = new User();
            moderator.setEmail("moderator@example.com");
            moderator.setPassword(passwordEncoder.encode(moderatorPassword));
            userRepository.save(moderator);

            Profile moderatorProfile = new Profile();
            moderatorProfile.setFirstName("moderator");
            moderatorProfile.setLastName("moderator");
            moderatorProfile.setUsername("moderator");
            Email moderatorEmail = new Email("moderator@example.com");
            moderatorEmail.setVerified(true);
            moderatorProfile.setEmail(moderatorEmail);
            moderatorProfile.setAboutMe("moderator");
            moderatorProfile.setRole(Role.MODERATOR);
            moderatorProfile.setLanguage(Language.ENGLISH);
            moderatorProfile.setUser(moderator);
            moderatorProfile.setSetupComplete(true);
            profileRepository.save(moderatorProfile);

            // added a regular user for assigment requirements
            User user = new User();
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            userRepository.save(user);

            Profile userProfile = new Profile();
            userProfile.setFirstName("john");
            userProfile.setLastName("doe");
            userProfile.setUsername("johndoe");
            Email userEmail = new Email("user@example.com");
            userEmail.setVerified(true);
            userProfile.setEmail(userEmail);
            userProfile.setAboutMe("test user");
            userProfile.setRole(Role.USER);
            userProfile.setLanguage(Language.ENGLISH);
            userProfile.setUser(user);
            userProfile.setSetupComplete(true);
            profileRepository.save(userProfile);
        };
    }
}

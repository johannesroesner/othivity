package de.oth.othivity;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.helper.TagRepository;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.service.impl.SessionServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.helper.Tag;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Configuration
public class TestDataRunner {

    @Bean
    CommandLineRunner loadTestData(ProfileRepository profileRepository,
                                   UserRepository userRepository,
                                   ActivityRepository activityRepository,
                                   TagRepository tagRepository,
                                   SessionServiceImpl sessionService,
                                   PasswordEncoder passwordEncoder,
                                   ClubRepository clubRepository) {
        return args -> {

            // ---- User & Profil ----
            User user = new User();
            user.setEmail("max.mustermann@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            userRepository.save(user);

            Profile profile = new Profile();
            profile.setFirstName("Max");
            profile.setLastName("Mustermann");
            profile.setEmail("max.mustermann@example.com");
            profile.setAboutMe("Ich bin ein Testprofil.");
            profile.setPhone("0123456789");
            profile.setRole(Role.USER);
            profile.setUser(user);
            profileRepository.save(profile);


            User user2 = new User();
            user2.setEmail("sebastian@example.com");
            user2.setPassword(passwordEncoder.encode("password"));
            userRepository.save(user2);

            Profile profile2 = new Profile();
            profile2.setFirstName("Sebastian");
            profile2.setLastName("Moritz");
            profile2.setEmail("sebastian@example.com");
            profile2.setAboutMe("Ich bin ein Testprofil.");
            profile2.setPhone("0123456789");
            profile2.setRole(Role.USER);
            profile2.setUser(user2);
            profileRepository.save(profile2);

            //sessionService.profileId = profile.getId();

            Activity activity = new Activity();
            activity.setTitle("my activity");
            activity.setDescription("test description");
            activity.setDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            activity.setGroupSize(10);
            activity.setStartedBy(profile);
            activity.setLanguage(Language.GERMAN);
            activityRepository.save(activity);

            Activity otherActivity = new Activity();
            otherActivity.setTitle("other activity");
            otherActivity.setDescription("test description");
            otherActivity.setDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            otherActivity.setGroupSize(10);
            otherActivity.setStartedBy(profile2);  
            otherActivity.setLanguage(Language.GERMAN);
            activityRepository.save(otherActivity);
        };
    }
}

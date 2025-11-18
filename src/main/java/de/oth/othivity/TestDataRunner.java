package de.oth.othivity;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.helper.TagRepository;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.service.impl.SessionServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Configuration
public class TestDataRunner {

    @Bean
    CommandLineRunner loadTestData(ProfileRepository profileRepository,
                                   UserRepository userRepository,
                                   ActivityRepository activityRepository,
                                   TagRepository tagRepository,
                                   SessionServiceImpl sessionService,
                                   ClubRepository clubRepository) {
        return args -> {

            // ---- User & Profil ----
            User user = new User();
            user.setEmail("max.mustermann@example.com");
            user.setPassword("{noop}password");
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

            sessionService.profileId = profile.getId();
        
            // ---- Club ----
            Club clubMemberOpen = new Club();
            clubMemberOpen.setName("Open Test Club Member");
            clubMemberOpen.setDescription("This is a open test club with the user as a member.");
            clubMemberOpen.setAccessLevel(AccessLevel.OPEN);
            clubMemberOpen.setMembers(List.of(profile));
            clubRepository.save(clubMemberOpen);

            Club clubNoMemberOpen = new Club();
            clubNoMemberOpen.setName("Open Test Club No Member");
            clubNoMemberOpen.setDescription("This is an open test club without the user as a member.");
            clubNoMemberOpen.setAccessLevel(AccessLevel.OPEN);
            clubRepository.save(clubNoMemberOpen);

            Club clubMemberClosed = new Club();
            clubMemberClosed.setName("Closed Test Club Member");
            clubMemberClosed.setDescription("This is a closed test club with the user as a member.");
            clubMemberClosed.setAccessLevel(AccessLevel.CLOSED);
            clubMemberClosed.setMembers(List.of(profile));
            clubRepository.save(clubMemberClosed);

            Club clubNoMemberClosed = new Club();
            clubNoMemberClosed.setName("Closed Test Club No Member");
            clubNoMemberClosed.setDescription("This is a closed test club without the user as a member.");
            clubNoMemberClosed.setAccessLevel(AccessLevel.CLOSED);
            clubRepository.save(clubNoMemberClosed);
        };
    }
}

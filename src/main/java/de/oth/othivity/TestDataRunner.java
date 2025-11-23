package de.oth.othivity;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ProfileRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                                   SessionServiceImpl sessionService,
                                   PasswordEncoder passwordEncoder,
                                   ClubRepository clubRepository) {
        return args -> {

            // ---- User & Profil ----
            User user = new User();
            user.setEmail("a@a.com");
            user.setPassword(passwordEncoder.encode("password"));
            userRepository.save(user);

            Profile profile = new Profile();
            profile.setFirstName("Max");
            profile.setLastName("Mustermann");
            profile.setUsername("gaudiSepp");
            profile.setEmail("a@a.com");
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
            profile2.setUsername("moesef");
            profile2.setEmail("sebastian@example.com");
            profile2.setAboutMe("Ich bin ein Testprofil.");
            profile2.setPhone("0123456789");
            profile2.setRole(Role.USER);
            profile2.setUser(user2);
            profileRepository.save(profile2);

            User user3 = new User();
            user3.setEmail("moritz@example.com");
            user3.setPassword(passwordEncoder.encode("password"));
            userRepository.save(user3);

            Profile profile3 = new Profile();
            profile3.setFirstName("Moritz");
            profile3.setLastName("Semmelmann");
            profile3.setUsername("moritz");
            profile3.setEmail("moritz@example.com");
            profile3.setAboutMe("Ich bin ein Testprofil.");
            profile3.setPhone("0123456789");
            profile3.setRole(Role.USER);
            profile3.setUser(user3);
            profileRepository.save(profile3);


            User user4 = new User();
            user4.setEmail("Sebastian@moritz-furth.de");
            user4.setPassword(passwordEncoder.encode("jklöjklö"));
            userRepository.save(user4);

            Profile profile4 = new Profile();
            profile4.setFirstName("Sebastian");
            profile4.setLastName("Moritz");
            profile4.setUsername("moe");
            profile4.setEmail("sebastian@moritz-furth.de");
            profile4.setAboutMe("Ich bin ein Testprofil.");
            profile4.setPhone("0123456789");
            profile4.setRole(Role.MODERATOR);
            profile4.setUser(user4);
            profileRepository.save(profile4);

            Activity activity = new Activity();
            activity.setTitle("my activity");
            activity.setDescription("test description");
            activity.setDate(LocalDateTime.now());
            activity.setGroupSize(10);
            activity.setStartedBy(profile);
            // Teilnehmerliste korrekt setzen
            List<Profile> participants1 = new ArrayList<>();
            participants1.add(profile);
            activity.setTakePart(participants1);
            activity.setLanguage(Language.GERMAN);
            activityRepository.save(activity);

            Activity otherActivity = new Activity();
            otherActivity.setTitle("other activity");
            otherActivity.setDescription("test description");
            otherActivity.setDate(LocalDateTime.now());
            otherActivity.setGroupSize(10);
            otherActivity.setStartedBy(profile2);
            // Teilnehmerliste korrekt setzen
            List<Profile> participants2 = new ArrayList<>();
            participants2.add(profile2);
            participants2.add(profile);
            otherActivity.setTakePart(participants2);
            otherActivity.setLanguage(Language.GERMAN);
            activityRepository.save(otherActivity);

            Club club = new Club();
            club.setName("Test Club");
            club.setDescription("Dies ist ein Test Club.");
            club.setAccessLevel(AccessLevel.OPEN);
            Address address = new Address();
            address.setStreet("Musterstraße");
            address.setHouseNumber("1");
            address.setCity("Musterstadt");
            address.setPostalCode("12345");
            club.setAddress(address);
            club.getMembers().add(profile);
            club.getMembers().add(profile2);
            club.getMembers().add(profile3);
            club.getAdmins().add(profile3);
            clubRepository.save(club);

            Club club2 = new Club();
            club2.setName("Exclusive Club");
            club2.setDescription("Ein exklusiver Club nur für geladene Gäste.");
            club2.setAccessLevel(AccessLevel.CLOSED);
            Address address2 = new Address();
            address2.setStreet("Exclusive Street");
            address2.setHouseNumber("99");
            address2.setCity("Elite City");
            address2.setPostalCode("54321");
            club2.setAddress(address2);
            club2.getMembers().add(profile);
            club2.getMembers().add(profile2);
            club2.getAdmins().add(profile);
            clubRepository.save(club2);

            Club club3 = new Club();
            club3.setName("Open Community");
            club3.setDescription("Ein offener Club für alle Interessierten.");
            club3.setAccessLevel(AccessLevel.OPEN);
            Address address3 = new Address();
            address3.setStreet("Community Road");
            address3.setHouseNumber("42");
            address3.setCity("Openville");
            address3.setPostalCode("11111");
            club3.setAddress(address3);
            club3.getMembers().add(profile);
            club3.getMembers().add(profile2);
            club3.getAdmins().add(profile2);
            clubRepository.save(club3);

            Club club4 = new Club();
            club4.setName("Social Hub");
            club4.setDescription("Ein sozialer Club für gemeinsame Aktivitäten.");
            club4.setAccessLevel(AccessLevel.CLOSED);
            Address address4 = new Address();
            address4.setStreet("Social Avenue");
            address4.setHouseNumber("17");
            address4.setCity("Friendlytown");
            address4.setPostalCode("22222");
            club4.setAddress(address4);
            club4.getMembers().add(profile);
            club4.getMembers().add(profile2);
            club4.getMembers().add(profile3);
            club4.getAdmins().add(profile);
            club4.getAdmins().add(profile2);
            clubRepository.save(club4);
            
        };
    }
}

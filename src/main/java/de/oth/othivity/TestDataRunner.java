package de.oth.othivity;

import de.oth.othivity.model.enumeration.Role;
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

import de.oth.othivity.model.image.ActivityImage;
import de.oth.othivity.model.helper.Address;

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
            user3.setEmail("Sebastian@moritz-furth.de");
            user3.setPassword(passwordEncoder.encode("jklöjklö"));
            userRepository.save(user3);

            Profile profile3 = new Profile();
            profile3.setFirstName("Sebastian");
            profile3.setLastName("Moritz");
            profile3.setUsername("moe");
            profile3.setEmail("sebastian@moritz-furth.de");
            profile3.setAboutMe("Ich bin ein Testprofil.");
            profile3.setPhone("0123456789");
            profile3.setRole(Role.MODERATOR);
            profile3.setUser(user3);
            profileRepository.save(profile3);

            Activity activity = new Activity();
            activity.setTitle("Soonest Activity");
            activity.setDescription("This activity is happening very soon in Furth im Wald.");
            activity.setDate(LocalDateTime.now().plusDays(1));
            activity.setGroupSize(10);
            activity.setStartedBy(profile);
            // Teilnehmerliste korrekt setzen
            List<Profile> participants1 = new ArrayList<>();
            participants1.add(profile);
            activity.setTakePart(participants1);
            activity.setLanguage(Language.GERMAN);

            ActivityImage image1 = new ActivityImage();
            image1.setUrl("https://picsum.photos/id/1/200/300");
            image1.setActivity(activity);
            activity.getImages().add(image1);

            Address address1 = new Address();
            address1.setStreet("Michael-Buchberger-Straße");
            address1.setHouseNumber("7");
            address1.setCity("Furth im Wald");
            address1.setPostalCode("93437");
            address1.setCountry("Germany");
            address1.setLatitude(49.3085);
            address1.setLongitude(12.8425);
            activity.setAddress(address1);

            activityRepository.save(activity);

            Activity otherActivity = new Activity();
            otherActivity.setTitle("Closest Activity");
            otherActivity.setDescription("This activity is very close to OTH Regensburg.");
            otherActivity.setDate(LocalDateTime.now().plusDays(3));
            otherActivity.setGroupSize(10);
            otherActivity.setStartedBy(profile2);
            // Teilnehmerliste korrekt setzen
            List<Profile> participants2 = new ArrayList<>();
            participants2.add(profile2);
            participants2.add(profile);
            otherActivity.setTakePart(participants2);
            otherActivity.setLanguage(Language.GERMAN);

            ActivityImage image2 = new ActivityImage();
            image2.setUrl("https://picsum.photos/id/2/200/300");
            image2.setActivity(otherActivity);
            otherActivity.getImages().add(image2);

            Address address2 = new Address();
            address2.setStreet("Am Gries");
            address2.setHouseNumber("1");
            address2.setCity("Regensburg");
            address2.setPostalCode("93059");
            address2.setCountry("Germany");
            address2.setLatitude(49.0225);
            address2.setLongitude(12.0983);
            otherActivity.setAddress(address2);

            activityRepository.save(otherActivity);

            Activity bestActivity = new Activity();
            bestActivity.setTitle("Best Mix Activity");
            bestActivity.setDescription("Good balance of distance and time.");
            bestActivity.setDate(LocalDateTime.now().plusDays(2));
            bestActivity.setGroupSize(10);
            bestActivity.setStartedBy(profile2);
            // Teilnehmerliste korrekt setzen
            List<Profile> participants3 = new ArrayList<>();
            participants3.add(profile2);
            participants3.add(profile);
            bestActivity.setTakePart(participants3);
            bestActivity.setLanguage(Language.GERMAN);

            ActivityImage image3 = new ActivityImage();
            image3.setUrl("https://picsum.photos/id/3/200/300");
            image3.setActivity(bestActivity);
            bestActivity.getImages().add(image3);

            Address address3 = new Address();
            address3.setStreet("Brunhuberstraße");
            address3.setHouseNumber("14");
            address3.setCity("Regensburg");
            address3.setPostalCode("93053");
            address3.setCountry("Germany");
            address3.setLatitude(49.0085);
            address3.setLongitude(12.1105);
            bestActivity.setAddress(address3);

            activityRepository.save(bestActivity);
        };
    }
}

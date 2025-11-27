package de.oth.othivity;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.helper.*;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.helper.ClubJoinRequestRepository;
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

import de.oth.othivity.model.helper.Address;

@Configuration
public class TestDataRunner {

    private final ClubJoinRequestRepository clubJoinRequestRepository;

    TestDataRunner(ClubJoinRequestRepository clubJoinRequestRepository) {
        this.clubJoinRequestRepository = clubJoinRequestRepository;
    }

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
            profile.setRole(Role.USER);
            profile.setLanguage(Language.ENGLISH);
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
            profile2.setRole(Role.USER);
            profile2.setLanguage(Language.ENGLISH);
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
            profile3.setRole(Role.USER);
            profile3.setLanguage(Language.ENGLISH);
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
            profile4.setRole(Role.MODERATOR);
            profile4.setLanguage(Language.ENGLISH);
            profile4.setUser(user4);
            profileRepository.save(profile4);

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

            Image image1 = new Image();
            image1.setUrl("https://picsum.photos/id/1/200/300");
            image1.setPublicId("1");
            activity.setImage(image1);

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

            Club club6 = new Club();
            club6.setName("Invite Only Club");
            club6.setDescription("Ein weiterer exklusiver Club, der nur auf Einladung beitretbar ist.");
            club6.setAccessLevel(AccessLevel.ON_INVITE);
            Address address6 = new Address();
            address6.setStreet("Exclusive Blvd");
            address6.setHouseNumber("13");
            address6.setCity("Selectville");
            address6.setPostalCode("44444");
            club6.setAddress(address6);
            club6.getMembers().add(profile3);
            club6.getAdmins().add(profile3);
            clubRepository.save(club6);


            Activity otherActivity = new Activity();
            otherActivity.setTitle("Closest Activity");
            otherActivity.setDescription("This activity is very close to OTH Regensburg.");
            otherActivity.setDate(LocalDateTime.now().plusDays(3));
            otherActivity.setGroupSize(10);
            otherActivity.setOrganizer(club6);
            otherActivity.setStartedBy(profile2);
            // Teilnehmerliste korrekt setzen
            List<Profile> participants2 = new ArrayList<>();
            participants2.add(profile2);
            participants2.add(profile);
            otherActivity.setTakePart(participants2);
            otherActivity.setLanguage(Language.GERMAN);

            Image image2 = new Image();
            image2.setUrl("https://picsum.photos/id/2/200/300");
            image2.setPublicId("2");
            otherActivity.setImage(image2);

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
            Address address4 = new Address();
            address4.setStreet("Exclusive Street");
            address4.setHouseNumber("99");
            address4.setCity("Elite City");
            address4.setPostalCode("54321");
            club2.setAddress(address4);
            club2.getMembers().add(profile);
            club2.getMembers().add(profile2);
            club2.getAdmins().add(profile);
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

            Image image3 = new Image();
            image3.setUrl("https://picsum.photos/id/3/200/300");
            image3.setPublicId("3");
            bestActivity.setImage(image3);

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
            clubRepository.save(club2);

            Club club3 = new Club();
            club3.setName("Open Community");
            club3.setDescription("Ein offener Club für alle Interessierten.");
            club3.setAccessLevel(AccessLevel.OPEN);
            Address address8 = new Address();
            address8.setStreet("Brunhuberstraße");
            address8.setHouseNumber("14");
            address8.setCity("Regensburg");
            address8.setPostalCode("93053");
            address8.setCountry("Germany");
            address8.setLatitude(49.0085);
            address8.setLongitude(12.1105);
            club3.setAddress(address8);
            club3.getMembers().add(profile);
            club3.getMembers().add(profile2);
            club3.getAdmins().add(profile2);
            clubRepository.save(club3);

            Club club4 = new Club();
            club4.setName("Social Hub");
            club4.setDescription("Ein sozialer Club für gemeinsame Aktivitäten.");
            club4.setAccessLevel(AccessLevel.CLOSED);
            Address address7 = new Address();
            address7.setStreet("Social Avenue");
            address7.setHouseNumber("17");
            address7.setCity("Friendlytown");
            address7.setPostalCode("22222");
            club4.setAddress(address7);
            club4.getMembers().add(profile);
            club4.getMembers().add(profile2);
            club4.getMembers().add(profile3);
            club4.getAdmins().add(profile);
            club4.getAdmins().add(profile2);
            clubRepository.save(club4);

            Club club5 = new Club();
            club5.setName("Only on invite Club");
            club5.setDescription("Ein exklusiver Club, der nur auf Einladung beitretbar ist.");
            club5.setAccessLevel(AccessLevel.ON_INVITE);
            Address address5 = new Address();
            address5.setStreet("Invite Lane");
            address5.setHouseNumber("7");
            address5.setCity("Invitetown");
            address5.setPostalCode("33333");
            club5.setAddress(address5);
            club5.getMembers().add(profile);
            club5.getAdmins().add(profile);
            clubRepository.save(club5);

            ClubJoinRequest joinRequest = new ClubJoinRequest();
            joinRequest.setClub(club6);
            joinRequest.setProfile(profile);
            joinRequest.setText("Ich würde gerne diesem exklusiven Club beitreten.");
            clubJoinRequestRepository.save(joinRequest);

            // ---- Additional 100 Activities ----
            List<Activity> extraActivities = new ArrayList<>();
            Profile[] possibleStarters = {profile, profile2, profile3, profile4};
            Language[] langs = {Language.GERMAN, Language.ENGLISH};

            String[] streets = {
                    "Hauptstraße", "Nebenweg", "Parkallee", "Bergstraße", "Sonnenweg",
                    "Waldweg", "Ringstraße", "Feldgasse", "Weinbergstraße", "Industriestraße",
                    "Schillerstraße", "Goethestraße", "Ahornweg", "Lindenweg", "Kirchplatz"
            };

            for (int i = 0; i < 15; i++) {
                Activity a = new Activity();
                a.setTitle("Generated Activity " + (i + 1));
                a.setDescription("Automatically generated test activity #" + (i + 1));
                a.setDate(LocalDateTime.now().plusDays(2 + i));
                a.setGroupSize(10 + (i % 5));

                Profile starter = possibleStarters[i % possibleStarters.length];
                a.setStartedBy(starter);

                List<Profile> participants = new ArrayList<>();
                participants.add(starter);
                if (i % 2 == 0) participants.add(profile);
                if (i % 3 == 0) participants.add(profile2);
                a.setTakePart(participants);

                a.setLanguage(langs[i % langs.length]);

                Image img = new Image();
                img.setUrl("https://picsum.photos/id/" + (10 + i) + "/200/300");
                img.setPublicId("auto_" + (10 + i));
                a.setImage(img);

                Address adr = new Address();
                adr.setStreet(streets[i % streets.length]);   // FIX: Index modulo
                adr.setHouseNumber(String.valueOf(1 + (i % 20)));
                adr.setCity("Regensburg");
                adr.setPostalCode("9300" + (i % 10));
                adr.setCountry("Germany");
                adr.setLatitude(49.0 + (i * 0.001));
                adr.setLongitude(12.1 + (i * 0.001));
                a.setAddress(adr);

                extraActivities.add(a);
            }

            activityRepository.saveAll(extraActivities);

        };

    }
}

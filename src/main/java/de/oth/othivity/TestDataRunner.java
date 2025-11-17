package de.oth.othivity;

import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.helper.Tag;
import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.helper.TagRepository;
import de.oth.othivity.repository.security.UserRepository;
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
                                   SessionServiceImpl sessionService) {
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

            // ---- Tags ----
            Tag tag1 = new Tag();
            tag1.setName("Sport");
            Tag tag2 = new Tag();
            tag2.setName("Outdoor");
            Tag tag3 = new Tag();
            tag3.setName("Entspannung");
            tagRepository.saveAll(List.of(tag1, tag2, tag3));

            // Hilfsmethode, um Date aus LocalDate zu machen
            java.util.function.Function<LocalDate, Date> toDate = ld -> Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // ---- Aktivitäten erstellen ----
            List<Activity> activities = new ArrayList<>();

            // Aktivität 1: heute, Profil ist Ersteller + Teilnehmer
            Activity activity1 = new Activity();
            activity1.setTitle("Joggen im Park");
            activity1.setDescription("Wir treffen uns zum gemeinsamen Joggen.");
            activity1.setDate(toDate.apply(LocalDate.now()));
            activity1.setLanguage(Language.GERMAN);
            activity1.setGroupSize(10);
            activity1.setStartedBy(profile);
            activity1.setTakePart(List.of(profile));
            activity1.setTags(List.of(tag1, tag2));
            activities.add(activity1);

            // Aktivität 2: morgen, Profil ist Ersteller + Teilnehmer
            Activity activity2 = new Activity();
            activity2.setTitle("Yoga am Morgen");
            activity2.setDescription("Entspannt in den Tag starten.");
            activity2.setDate(toDate.apply(LocalDate.now().plusDays(1)));
            activity2.setLanguage(Language.GERMAN);
            activity2.setGroupSize(5);
            activity2.setStartedBy(profile);
            activity2.setTakePart(List.of(profile));
            activity2.setTags(List.of(tag2, tag3));
            activities.add(activity2);

            // Aktivität 3: übermorgen, Profil ist weder Ersteller noch Teilnehmer
            Activity activity3 = new Activity();
            activity3.setTitle("Kochkurs Italienisch");
            activity3.setDescription("Lerne Pasta & Pizza zuzubereiten.");
            activity3.setDate(toDate.apply(LocalDate.now().plusDays(2)));
            activity3.setLanguage(Language.GERMAN);
            activity3.setGroupSize(8);
            activity3.setStartedBy(profile); // Platzhalter
            activity3.setTakePart(new ArrayList<>());
            activity3.setTags(List.of(tag1));
            activities.add(activity3);

            // Aktivität 4: +3 Tage, Profil ist Teilnehmer, nicht Ersteller
            Activity activity4 = new Activity();
            activity4.setTitle("Mountainbike Tour");
            activity4.setDescription("Gemeinsame MTB-Tour in der Umgebung.");
            activity4.setDate(toDate.apply(LocalDate.now().plusDays(3)));
            activity4.setLanguage(Language.GERMAN);
            activity4.setGroupSize(6);
            activity4.setStartedBy(profile); // Platzhalter
            activity4.setTakePart(List.of(profile));
            activity4.setTags(List.of(tag1, tag2));
            activities.add(activity4);

            activityRepository.saveAll(activities);

            System.out.println("Testdaten erstellt! Profile ID: " + profile.getId());
            System.out.println("Aktivitäten: " + activityRepository.findAll().size());
        };
    }
}

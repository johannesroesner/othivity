package de.oth.othivity;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.service.impl.SessionServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class TestDataRunner {

    @Bean
    CommandLineRunner loadTestData(ProfileRepository profileRepository,
                                   UserRepository userRepository,
                                   ActivityRepository activityRepository,
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

            // ---- Testaktivitäten ----
            List<Activity> activities = new ArrayList<>();
            // 1. Aktivität: Wandern
            Activity hiking = new Activity();
            hiking.setTitle("Wandern in den Bergen");
            hiking.setDescription("Eine entspannte Wanderung für alle Levels.");
            // KORREKTUR: LocalDateTime.now() + Tage + Uhrzeit
            hiking.setDate(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
            hiking.setGroupSize(10);
            hiking.setLanguage(de.oth.othivity.model.enumeration.Language.GERMAN);
            hiking.setStartedBy(profile);
            hiking.getTags().add(de.oth.othivity.model.enumeration.Tag.HIKING);
            hiking.getTags().add(de.oth.othivity.model.enumeration.Tag.OUTDOOR);
            hiking.getTags().add(Tag.FOOD);
            activities.add(hiking);

            // 2. Aktivität: Brettspielabend
            Activity boardGames = new Activity();
            boardGames.setTitle("Brettspielabend");
            boardGames.setDescription("Wir spielen Klassiker wie Catan, Risiko und mehr.");
            // KORREKTUR: LocalDateTime.now() + Tage + Uhrzeit
            boardGames.setDate(LocalDateTime.now().plusDays(5).withHour(19).withMinute(30));
            boardGames.setGroupSize(8);
            boardGames.setLanguage(de.oth.othivity.model.enumeration.Language.ENGLISH);
            boardGames.setStartedBy(profile);
            boardGames.getTags().add(de.oth.othivity.model.enumeration.Tag.BOARDGAME);
            boardGames.getTags().add(de.oth.othivity.model.enumeration.Tag.INDOOR);
            activities.add(boardGames);

            // 3. Aktivität: Party
            Activity party = new Activity();
            party.setTitle("Sommerparty auf der Dachterrasse");
            party.setDescription("Musik, Drinks und gute Laune garantiert!");
            // KORREKTUR: LocalDateTime.now() + Tage + Uhrzeit
            party.setDate(LocalDateTime.now().plusDays(7).withHour(21).withMinute(0));
            party.setGroupSize(20);
            party.setLanguage(de.oth.othivity.model.enumeration.Language.GERMAN);
            party.setStartedBy(profile);
            party.getTags().add(de.oth.othivity.model.enumeration.Tag.PARTY);
            party.getTags().add(de.oth.othivity.model.enumeration.Tag.MUSIC);
            activities.add(party);

            // Speichern aller Aktivitäten
            activityRepository.saveAll(activities);

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

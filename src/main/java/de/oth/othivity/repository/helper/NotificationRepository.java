package de.oth.othivity.repository.helper;

import org.springframework.data.jpa.repository.JpaRepository;

import de.oth.othivity.model.helper.Notification;
import de.oth.othivity.model.main.Profile;

import java.util.UUID;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByProfile(Profile profile);

    int countByProfileAndIsReadFalse(Profile profile);

}

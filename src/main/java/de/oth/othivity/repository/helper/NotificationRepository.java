package de.oth.othivity.repository.helper;

import org.springframework.data.jpa.repository.JpaRepository;

import de.oth.othivity.model.helper.Notification;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

}

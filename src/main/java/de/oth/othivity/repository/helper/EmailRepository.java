package de.oth.othivity.repository.helper;

import de.oth.othivity.model.helper.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {

}

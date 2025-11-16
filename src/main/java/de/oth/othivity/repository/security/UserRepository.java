package de.oth.othivity.repository.security;

import de.oth.othivity.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

}

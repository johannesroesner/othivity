package de.oth.othivity.repository.helper;

import de.oth.othivity.model.helper.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, UUID> {
    Optional<Phone> findByNumber(String number);

}

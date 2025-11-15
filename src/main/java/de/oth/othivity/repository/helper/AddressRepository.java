package de.oth.othivity.repository.helper;

import de.oth.othivity.model.helper.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

}

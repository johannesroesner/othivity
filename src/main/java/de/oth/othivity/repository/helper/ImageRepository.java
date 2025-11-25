package de.oth.othivity.repository.helper;

import de.oth.othivity.model.helper.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {

}

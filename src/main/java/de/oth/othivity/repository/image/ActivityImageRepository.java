package de.oth.othivity.repository.image;

import de.oth.othivity.model.image.ActivityImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityImageRepository extends JpaRepository<ActivityImage, UUID> {

}

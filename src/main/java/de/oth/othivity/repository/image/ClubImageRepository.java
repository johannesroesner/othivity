package de.oth.othivity.repository.image;

import de.oth.othivity.model.image.ClubImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClubImageRepository extends JpaRepository<ClubImage, UUID> {


}

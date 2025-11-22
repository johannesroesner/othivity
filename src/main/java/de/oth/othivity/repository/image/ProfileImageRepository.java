package de.oth.othivity.repository.image;

import de.oth.othivity.model.image.ProfileImage;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProfileImageRepository extends CrudRepository<ProfileImage, UUID> {

}

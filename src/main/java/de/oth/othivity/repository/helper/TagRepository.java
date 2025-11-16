package de.oth.othivity.repository.helper;

import de.oth.othivity.model.helper.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

}

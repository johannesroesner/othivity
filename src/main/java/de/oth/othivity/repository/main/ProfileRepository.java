package de.oth.othivity.repository.main;

import de.oth.othivity.model.main.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    boolean existsByusername(String username);

    boolean existsByEmailAddress(String address);

    Profile findByUsername(String username);

    Optional<Profile> findByEmailAddress(String address);

    @Query("SELECT p FROM Profile p WHERE (:search IS NULL OR LOWER(p.username) LIKE CONCAT('%', LOWER(:search), '%') OR LOWER(p.firstName) LIKE CONCAT('%', LOWER(:search), '%') OR LOWER(p.lastName) LIKE CONCAT('%', LOWER(:search), '%'))")
    Page<Profile> findAllByFilter(@Param("search") String search, Pageable pageable);

}

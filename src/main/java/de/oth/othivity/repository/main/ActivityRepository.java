    package de.oth.othivity.repository.main;

    import de.oth.othivity.model.enumeration.Tag;
    import de.oth.othivity.model.main.Activity;
    import de.oth.othivity.model.main.Club;
    import de.oth.othivity.model.main.Profile;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.UUID;

    @Repository
    public interface ActivityRepository extends JpaRepository<Activity, UUID> {
        List<Activity> findAllByOrganizer(Club organizer);

        @Query("SELECT DISTINCT a FROM Activity a LEFT JOIN a.takePart p LEFT JOIN a.tags t WHERE (a.startedBy = :profile OR p = :profile) AND (:tag IS NULL OR t = :tag) AND (:search IS NULL OR LOWER(a.title) LIKE CONCAT('%', LOWER(:search), '%'))")
        Page<Activity> findAllCreatedOrJoinedByProfileWithFilter(@Param("profile") Profile profile, Pageable pageable, @Param("tag") Tag tag, @Param("search") String search);

        @Query("SELECT DISTINCT a FROM Activity a LEFT JOIN a.tags t WHERE a.startedBy != :profile AND :profile NOT MEMBER OF a.takePart AND (:tag IS NULL OR t = :tag) AND (:search IS NULL OR LOWER(a.title) LIKE CONCAT('%', LOWER(:search), '%'))")
        Page<Activity> findAllNotCreatedOrNotJoinedByProfileWithFilter(@Param("profile") Profile profile, Pageable pageable, @Param("tag") Tag tag, @Param("search") String search);

        @Query("SELECT DISTINCT a FROM Activity a LEFT JOIN a.tags t WHERE a.startedBy = :profile AND (:tag IS NULL OR t = :tag) AND (:search IS NULL OR LOWER(a.title) LIKE CONCAT('%', LOWER(:search), '%'))")
        Page<Activity> findAllCreatedByProfileWithFilter(@Param("profile") Profile profile, Pageable pageable, @Param("tag") Tag tag, @Param("search") String search);

    }

    package de.oth.othivity.repository.main;

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

        Page<Activity> findAllByStartedBy(Profile startedBy, Pageable pageable);

        @Query("SELECT DISTINCT a FROM Activity a LEFT JOIN a.takePart p WHERE a.startedBy = :profile OR p = :profile")
        Page<Activity> findAllCreatedOrJoinedByProfile(@Param("profile") Profile profile, Pageable pageable);

        @Query("SELECT a FROM Activity a WHERE a.startedBy != :profile AND :profile NOT MEMBER OF a.takePart")
        Page<Activity> findAllNotCreatedOrNotJoinedByProfile(@Param("profile") Profile profile, Pageable pageable);
    }

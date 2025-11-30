package de.oth.othivity.repository.pushnotification;

import de.oth.othivity.model.pushnotification.PushSubscription;
import de.oth.othivity.model.main.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {
    
    List<PushSubscription> findAllByProfile(Profile profile);

    PushSubscription findByEndpoint(String endpoint);
}
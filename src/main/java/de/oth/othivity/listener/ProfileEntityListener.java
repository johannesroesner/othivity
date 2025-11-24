package de.oth.othivity.listener;

import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.service.ActivityService;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileEntityListener {
    private static ActivityService activityService;

    @Autowired
    public void init(ActivityService activityService) {
        ProfileEntityListener.activityService = activityService;
    }

    @PreRemove
    public void preRemoveProfile(Profile profile) {
        activityService.removeProfileFromActivities(profile);
    }
}
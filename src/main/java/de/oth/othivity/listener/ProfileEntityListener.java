package de.oth.othivity.listener;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ActivityRepository;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileEntityListener {
    private static ActivityRepository activityRepository;

    @Autowired
    public void init(ActivityRepository activityRepository) {
        ProfileEntityListener.activityRepository = activityRepository;
    }

    @PreRemove
    public void preRemoveProfile(Profile profile) {
        List<Activity> participatingActivities = new ArrayList<>(profile.getParticipatingActivities());

        for (Activity activity : participatingActivities) {
            if (!activity.getStartedBy().getId().equals(profile.getId())) {
                activity.getTakePart().remove(profile);
                activityRepository.save(activity);
            }
        }
    }
}
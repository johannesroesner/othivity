package de.oth.othivity.service.impl;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final SessionService sessionService;
    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(SessionService sessionService,ActivityRepository activityRepository) {
        this.sessionService = sessionService;
        this.activityRepository = activityRepository;
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public List<Activity> getActivitiesCreatedOrJoinedByProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();

        List<Activity> activities = new ArrayList<>(profile.getParticipatingActivities());
        activities.addAll(profile.getStartedActivities());

        return activities.stream().distinct().toList();
    }

    @Override
    public List<Activity> getActivitiesNotCreatedOrNotJoinedByProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();

        List<Activity> allActivities = new ArrayList<>(profile.getParticipatingActivities());

        return allActivities.stream()
                .filter(activity -> !profile.getStartedActivities().contains(activity))
                .filter(activity -> !profile.getParticipatingActivities().contains(activity))
                .toList();
    }

    @Override
    public List<Activity> getActivitiesCreatedByProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return List.of();

        return  profile.getStartedActivities();
    }

    @Override
    public List<String> getActivityDatesForProfile(HttpSession session) {
        List<Activity> activities = getActivitiesCreatedOrJoinedByProfile(session);
        if (activities.isEmpty()) {
            return List.of();
        }
        return activities.stream()
                .map(Activity::getDate)
                .map(date -> new java.text.SimpleDateFormat("yyyy-MM-dd").format(date))
                .toList();
    }
}

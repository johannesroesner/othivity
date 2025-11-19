package de.oth.othivity.service.impl;

import de.oth.othivity.dto.ActivityCreateRequest;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.ImageService;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ActivityServiceImpl implements ActivityService {
    private final SessionService sessionService;
    private final ImageService imageService;

    private final ActivityRepository activityRepository;

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

        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return activities.stream()
                .map(Activity::getDate)
                .map(date -> date.format(dateFormatter))
                .toList();
    }

    @Override
    public Activity createActivity(ActivityCreateRequest activityCreateRequest, MultipartFile[] uploadedImages, HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return null;

        Activity activity = new Activity();
        activity.setTitle(activityCreateRequest.getTitle());
        activity.setDescription(activityCreateRequest.getDescription());
        activity.setDate(activityCreateRequest.getDate());
        activity.setLanguage(activityCreateRequest.getLanguage());
        activity.setGroupSize(activityCreateRequest.getGroupSize());
        activity.setOrganizer(activityCreateRequest.getOrganizer());
        activity.setTags(activityCreateRequest.getTags());
        activity.setAddress(activityCreateRequest.getAddress());
        activity.setStartedBy(profile);
        List<Profile> participants = new ArrayList<>();
        participants.add(profile);
        activity.setTakePart(participants);

        Activity newActivity = activityRepository.save(activity);
        imageService.saveImagesForActivity(newActivity, uploadedImages);

        return newActivity;
    }
}

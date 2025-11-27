    package de.oth.othivity.service.impl;

    import de.oth.othivity.dto.ActivityDto;
    import de.oth.othivity.model.main.Activity;
    import de.oth.othivity.model.main.Profile;
    import de.oth.othivity.repository.main.ActivityRepository;
    import de.oth.othivity.service.ActivityService;
    import de.oth.othivity.service.GeocodingService;
    import de.oth.othivity.service.ImageService;
    import de.oth.othivity.service.SessionService;
    import jakarta.servlet.http.HttpSession;
    import lombok.AllArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;

    @AllArgsConstructor
    @Service
    public class ActivityServiceImpl implements ActivityService {
        private final SessionService sessionService;
        private final ImageService imageService;
        private final GeocodingService geocodingService;

        private final ActivityRepository activityRepository;

        @Override
        public List<Activity> getAllActivities() {
            return activityRepository.findAll();
        }

        @Override
        public Page<Activity> getActivitiesCreatedOrJoinedByProfile(HttpSession session, Pageable pageable) {
            Profile profile = sessionService.getProfileFromSession(session);
            if (profile == null) return Page.empty();

            return activityRepository.findAllCreatedOrJoinedByProfile(profile, pageable);
        }

        @Override
        public Page<Activity> getActivitiesNotCreatedOrNotJoinedByProfile(HttpSession session, Pageable pageable) {
            Profile profile = sessionService.getProfileFromSession(session);
            if (profile == null) return Page.empty();

            return activityRepository.findAllNotCreatedOrNotJoinedByProfile(profile, pageable);
        }

        @Override
        public Page<Activity> getActivitiesCreatedByProfile(HttpSession session, Pageable pageable) {
            Profile profile = sessionService.getProfileFromSession(session);
            if (profile == null) return Page.empty();

            return activityRepository.findAllByStartedBy(profile, pageable);
        }

        @Override
        public List<String> getActivityDatesForProfile(HttpSession session) {
            Profile profile = sessionService.getProfileFromSession(session);
            List<Activity> activities = activityRepository.findAllCreatedOrJoinedByProfile(profile, Pageable.unpaged()).getContent();

            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            return activities.stream()
                    .map(Activity::getDate)
                    .map(date -> date.format(dateFormatter))
                    .toList();
        }

        @Override
        public Activity createActivity(ActivityDto activityDto, MultipartFile uploadedImage, HttpSession session) {
            Profile profile = sessionService.getProfileFromSession(session);
            if (profile == null) return null;

            Activity activity = new Activity();
            activity.setTitle(activityDto.getTitle());
            activity.setDescription(activityDto.getDescription());
            activity.setImage(imageService.saveImage(activity,uploadedImage));
            activity.setDate(activityDto.getDate());
            activity.setLanguage(activityDto.getLanguage());
            activity.setGroupSize(activityDto.getGroupSize());
            activity.setOrganizer(activityDto.getOrganizer());
            activity.setTags(activityDto.getTags());

            // get latitude and longitude from geocoding service
            activity.setAddress(geocodingService.geocode(activityDto.getAddress()));
            activity.setStartedBy(profile);

            List<Profile> participants = new ArrayList<>();
            participants.add(profile);
            activity.setTakePart(participants);

            Activity newActivity = activityRepository.save(activity);

            return newActivity;
        }

        @Override
        public Activity updateActivity(Activity activity, ActivityDto activityDto, MultipartFile uploadedImage, HttpSession session) {
            Profile profile = sessionService.getProfileFromSession(session);
            if (profile == null) return null;

            activity.setTitle(activityDto.getTitle());
            activity.setDescription(activityDto.getDescription());
            activity.setDate(activityDto.getDate());
            activity.setLanguage(activityDto.getLanguage());
            activity.setGroupSize(activityDto.getGroupSize());
            activity.setOrganizer(activityDto.getOrganizer());
            activity.setTags(activityDto.getTags());

            // get latitude and longitude from geocoding service
            activity.setAddress(geocodingService.geocode(activityDto.getAddress()));
            activity.setStartedBy(profile);

            if(uploadedImage != null && uploadedImage.getSize() != 0) {
                activity.setImage(imageService.saveImage(activity, uploadedImage));
                activityRepository.save(activity);
            }

            return activityRepository.save(activity);
        }

        @Override
        public Activity getActivityById(UUID activityId) {
            return activityRepository.findById(activityId).orElse(null);
        }

        @Override
        public ActivityDto activityToDto(Activity activity) {
            ActivityDto activityDto = new ActivityDto();
            activityDto.setId(activity.getId());
            activityDto.setTitle(activity.getTitle());
            activityDto.setDescription(activity.getDescription());
            activityDto.setDate(activity.getDate());
            activityDto.setLanguage(activity.getLanguage());
            activityDto.setGroupSize(activity.getGroupSize());
            activityDto.setOrganizer(activity.getOrganizer());
            activityDto.setTags(activity.getTags());
            activityDto.setAddress(activity.getAddress());
            activityDto.setImage(activity.getImage());
            return activityDto;
        }

        @Override
        public Activity joinActivity(Activity activity, HttpSession session) {
            Profile profile = sessionService.getProfileFromSession(session);
            if (profile == null) return null;

            List<Profile> participants = activity.getTakePart();
            participants.add(profile);
            activity.setTakePart(participants);

            return activityRepository.save(activity);
        }

        @Override
        public Activity leaveActivity(Activity activity, HttpSession session) {
            Profile profile = sessionService.getProfileFromSession(session);
            if (profile == null) return null;
            List<Profile> participants = activity.getTakePart();
            participants.removeIf(p -> p.getId().equals(profile.getId()));
            activity.setTakePart(participants);
            return activityRepository.save(activity);
        }

        @Override
        public Activity kickParticipant(Activity activity, Profile profile) {
            activity.getTakePart().removeIf(p -> p.getId().equals(profile.getId()));
            return activityRepository.save(activity);
        }

        @Override
        public void deleteActivity(Activity activity) {
            activityRepository.delete(activity);
        }

        @Override
        public void removeProfileFromActivities(Profile profile) {
            List<Activity> participatingActivities = new ArrayList<>(profile.getParticipatingActivities());

            for (Activity activity : participatingActivities) {
                if (!activity.getStartedBy().getId().equals(profile.getId())) {
                    activity.getTakePart().remove(profile);
                    activityRepository.save(activity);
                }
            }
        }
    }

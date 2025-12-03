package de.oth.othivity.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.oth.othivity.dto.ActivityDto;
import de.oth.othivity.model.enumeration.NotificationType;
import de.oth.othivity.dto.MarkerDto;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ActivityServiceImpl implements ActivityService {
    private final SessionService sessionService;
    private final ImageService imageService;
    private final GeocodingService geocodingService;
    private final INotificationService notificationService;

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    private final ActivityRepository activityRepository;

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Page<Activity> getActivitiesCreatedOrJoinedByProfileWithFilter(HttpSession session, Pageable pageable, Tag tag, String search) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return Page.empty();

        return activityRepository.findAllCreatedOrJoinedByProfileWithFilter(profile, pageable, tag, search);
    }

    @Override
    public Page<Activity> getActivitiesNotCreatedOrNotJoinedByProfileWithFilter(HttpSession session, Pageable pageable, Tag tag, String search) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return Page.empty();

        return activityRepository.findAllNotCreatedOrNotJoinedByProfileWithFilter(profile, pageable, tag, search);
    }

    @Override
    public Page<Activity> getActivitiesCreatedByProfileWithFilter(HttpSession session, Pageable pageable, Tag tag, String search) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return Page.empty();

        return activityRepository.findAllCreatedByProfileWithFilter(profile, pageable, tag, search);
    }


    @Override
    public List<String> getActivityDatesForProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        List<Activity> activities = activityRepository.findAllCreatedOrJoinedByProfileWithFilter(profile, Pageable.unpaged(), null, null).getContent();

        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return activities.stream()
                .map(Activity::getDate)
                .map(date -> date.format(dateFormatter))
                .toList();
    }

    @Override
    public Activity createActivity(ActivityDto activityDto, MultipartFile uploadedImage, Profile profile) {
        if (profile == null) return null;

        Activity activity = new Activity();
        activity.setTitle(activityDto.getTitle());
        activity.setDescription(activityDto.getDescription());

        //image handling
        if(uploadedImage != null && uploadedImage.getSize() != 0) activity.setImage(imageService.saveImage(activity,uploadedImage));
        else if(activityDto.getImage() != null) activity.setImage(imageService.saveImage(activity,activityDto.getImage()));
        else return null;

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
    public Activity updateActivity(Activity activity, ActivityDto activityDto, MultipartFile uploadedImage, Profile profile) {
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
        }
        if(activityDto.getImage()!= null && activityDto.getImage().getUrl() != null && !activity.getImage().getUrl().equals(activityDto.getImage().getUrl())) {
            activity.setImage(imageService.saveImage(activity, activityDto.getImage()));
        }

        for(Profile participant : activity.getTakePart() ) {
            //TODO moe
            notificationService.sendNotification(NotificationType.EMAIL,activity,participant, "notification.activity.updated");
            notificationService.sendNotification(NotificationType.PUSH_NOTIFICATION,activity,participant, "notification.activity.updated");
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
    public Activity joinActivity(Activity activity, Profile profile) {
        if (profile == null) return null;

        List<Profile> participants = activity.getTakePart();
        participants.add(profile);
        activity.setTakePart(participants);

        //TODO moe
        notificationService.sendNotification(NotificationType.EMAIL,activity,activity.getStartedBy(), "notification.activity.joined");
        notificationService.sendNotification(NotificationType.PUSH_NOTIFICATION,activity,activity.getStartedBy(), "notification.activity.joined");

        return activityRepository.save(activity);
    }

    @Override
    public Activity leaveActivity(Activity activity, HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if (profile == null) return null;
        List<Profile> participants = activity.getTakePart();
        participants.removeIf(p -> p.getId().equals(profile.getId()));
        activity.setTakePart(participants);

        //TODO moe
        notificationService.sendNotification(NotificationType.EMAIL,activity,activity.getStartedBy(), "notification.activity.left");
        notificationService.sendNotification(NotificationType.PUSH_NOTIFICATION,activity,activity.getStartedBy(), "notification.activity.left");

        return activityRepository.save(activity);
    }

    @Override
    public Activity kickParticipant(Activity activity, Profile profile) {
        activity.getTakePart().removeIf(p -> p.getId().equals(profile.getId()));

        //TODO moe
        notificationService.sendNotification(NotificationType.EMAIL,activity,profile, "notification.activity.kicked");
        notificationService.sendNotification(NotificationType.PUSH_NOTIFICATION,activity,profile, "notification.activity.kicked");
        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivity(Activity activity) {
        for (Profile profile : activity.getTakePart()) {
            //TODO moe
            notificationService.sendNotification(NotificationType.EMAIL,activity,profile, "notification.activity.deleted");
            notificationService.sendNotification(NotificationType.PUSH_NOTIFICATION,activity,profile, "notification.activity.deleted");
        }

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

    @Override
    public Activity getSoonestActivityForProfile(Profile profile) {
        if (profile == null) return null;
        List<Activity> activities = activityRepository.findAllCreatedOrJoinedByProfileWithFilter(profile, Pageable.unpaged(), null, null).getContent();
        return activities.isEmpty() ? null : activities.get(0);
    }

    @Override
    public String getActivityTimeUntil(Activity activity) {
        if (activity == null || activity.getDate() == null) return "";

        Locale locale = LocaleContextHolder.getLocale();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = activity.getDate();

        if (date.isBefore(now)) {
            return messageSource.getMessage("dashboard.activityOccurred", null, locale);
        }

        long days = Duration.between(now, date).toDays();
        if (days > 0) {
            return messageSource.getMessage("dashboard.activityDays", new Object[]{days}, locale);
        }

        long hours = Duration.between(now, date).toHours();
        if (hours > 0) {
            return messageSource.getMessage("dashboard.activityHours", new Object[]{hours}, locale);
        }

        long minutes = Duration.between(now, date).toMinutes();
        return messageSource.getMessage("dashboard.activityMinutes", new Object[]{minutes}, locale);
    }

    @Override
    public String getAllActivitiesWithGeoCoordinates(){
        List<MarkerDto> markers = activityRepository.findAll().stream()
                .filter(a -> a.getAddress() != null
                        && a.getAddress().getLatitude() != null
                        && a.getAddress().getLongitude() != null)
                .map(a -> new MarkerDto(
                        a.getAddress().getLatitude(),
                        a.getAddress().getLongitude(),
                        "/activities/" + a.getId(),
                        a.getTitle()
                ))
                .collect(Collectors.toList());
        String json = null;
        try {
            json = objectMapper.writeValueAsString(markers);
        } catch (Exception e) {}

        return json;
    }

    @Override
    public Activity resetTakePart(Activity activity) {
        activity.setTakePart(new ArrayList<>());
        return activityRepository.save(activity);
    }
}

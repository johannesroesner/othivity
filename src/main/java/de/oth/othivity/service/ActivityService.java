package de.oth.othivity.service;

import de.oth.othivity.dto.ActivityDto;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;;

@Service
public interface ActivityService {

    List<Activity> getAllActivities();

    Page<Activity> getActivitiesCreatedOrJoinedByProfileWithFilter(HttpSession session, Pageable pageable, Tag tag, String search);

    Page<Activity> getActivitiesNotCreatedOrNotJoinedByProfileWithFilter(HttpSession session, Pageable pageable, Tag tag, String search);

    Page<Activity> getActivitiesCreatedByProfileWithFilter(HttpSession session, Pageable pageable, Tag tag, String search);

    List<String> getActivityDatesForProfile(HttpSession session);

    Activity createActivity(ActivityDto activityCreateRequest, MultipartFile uploadedImage, HttpSession session);

    Activity updateActivity(Activity activity, ActivityDto activityUpdateRequest, MultipartFile uploadedImage, HttpSession session) ;

    Activity getActivityById(UUID activityId);

    ActivityDto activityToDto(Activity activity);

    Activity joinActivity(Activity activity, HttpSession session);

    Activity leaveActivity(Activity activity, HttpSession session);

    Activity kickParticipant(Activity activity, Profile profile);

    void deleteActivity(Activity activity);

    void removeProfileFromActivities(Profile profile);

    Activity getSoonestActivityForProfile(Profile profile);

    String getActivityTimeUntil(Activity activity);

    String getAllActivitiesWithGeoCoordinates();
}

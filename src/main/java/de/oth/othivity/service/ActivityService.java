package de.oth.othivity.service;

import de.oth.othivity.model.main.Activity;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;;

@Service
public interface ActivityService {

    List<Activity> getAllActivities();

    List<Activity> getActivitiesCreatedOrJoinedByProfile(HttpSession session);

    List<Activity> getActivitiesNotCreatedOrNotJoinedByProfile(HttpSession session);

    List<Activity> getActivitiesCreatedByProfile(HttpSession session);

    List<String> getActivityDatesForProfile(HttpSession session);
}

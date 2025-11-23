package de.oth.othivity.service;

import de.oth.othivity.model.main.Activity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IExplorerService {
    List<Activity> getClosestActivities(double lat, double lon, int limit);
    List<Activity> getSoonestActivities(double lat, double lon, int limit);
    List<Activity> getBestMixActivities(double lat, double lon, int limit);
    List<Activity> getAllFutureActivities();
}

package de.oth.othivity.service.impl;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.service.IExplorerService;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
public class ExplorerServiceImpl implements IExplorerService {

    private final ActivityRepository activityRepository;

    @Override
    public List<Activity> getClosestActivities(double lat, double lon, int limit) {
        LocalDateTime now = LocalDateTime.now();

        return activityRepository.findAll().stream()
                .filter(a -> a.getDate().isAfter(now))
                .filter(a -> a.getAddress() != null)
                .filter(a -> a.getAddress().getLatitude() != null && a.getAddress().getLongitude() != null)
                .sorted(Comparator.comparingDouble(a -> calculateDistance(lat, lon, a.getAddress().getLatitude(), a.getAddress().getLongitude())))
                .limit(limit)
                .toList();
    }

    @Override
    public List<Activity> getSoonestActivities(int limit) {
        LocalDateTime now = LocalDateTime.now();

        return activityRepository.findAll().stream()
                .filter(a -> a.getDate().isAfter(now))
                .filter(a -> a.getAddress() != null)
                .sorted(Comparator.comparing(Activity::getDate))
                .limit(limit)
                .toList();
    }

    @Override
    public List<Activity> getBestMixActivities(double lat, double lon, int limit) {
        LocalDateTime now = LocalDateTime.now();

        return activityRepository.findAll().stream()
                .filter(a -> a.getDate().isAfter(now))
                .filter(a -> a.getAddress() != null)
                .filter(a -> a.getAddress().getLatitude() != null && a.getAddress().getLongitude() != null)
                .sorted(Comparator.comparingDouble(a -> {
                    double distance = calculateDistance(lat, lon, a.getAddress().getLatitude(), a.getAddress().getLongitude());
                    long minutesUntilStart = ChronoUnit.MINUTES.between(now, a.getDate());
                    // Heuristic: 1 km is equivalent to 60 minutes of waiting
                    // This balances distance more heavily against time
                    return distance * 60 + minutesUntilStart;
                }))
                .limit(limit)
                .toList();
    }

    @Override
    public List<Activity> getAllFutureActivities() {
        LocalDateTime now = LocalDateTime.now();
        return activityRepository.findAll().stream()
                .filter(a -> a.getDate().isAfter(now))
                .sorted(Comparator.comparing(Activity::getDate))
                .toList();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}

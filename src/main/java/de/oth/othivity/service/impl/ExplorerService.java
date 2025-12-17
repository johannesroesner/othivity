package de.oth.othivity.service.impl;

import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.service.IExplorerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class ExplorerService implements IExplorerService {

    private final ActivityRepository activityRepository;

    @Override
    public Page<Activity> getClosestActivities(double lat, double lon, Pageable pageable, String search, Tag tag, Profile profile) {
        List<Activity> filtered = getBaseStream(search, tag, profile)
                .filter(a -> a.getAddress() != null && a.getAddress().getLatitude() != null && a.getAddress().getLongitude() != null)
                .sorted(Comparator.comparingDouble(a -> calculateDistance(lat, lon, a.getAddress().getLatitude(), a.getAddress().getLongitude())))
                .toList();

        return createPageFromList(filtered, pageable);
    }

    @Override
    public Page<Activity> getSoonestActivities(Pageable pageable, String search, Tag tag, Profile profile) {
        List<Activity> filtered = getBaseStream(search, tag, profile)
                .sorted(Comparator.comparing(Activity::getDate))
                .toList();

        return createPageFromList(filtered, pageable);
    }

    @Override
    public Page<Activity> getBestMixActivities(double lat, double lon, Pageable pageable, String search, Tag tag, Profile profile) {
        LocalDateTime now = LocalDateTime.now();
        List<Activity> filtered = getBaseStream(search, tag, profile)
                .filter(a -> a.getAddress() != null && a.getAddress().getLatitude() != null && a.getAddress().getLongitude() != null)
                .sorted(Comparator.comparingDouble(a -> {
                    double distance = calculateDistance(lat, lon, a.getAddress().getLatitude(), a.getAddress().getLongitude());
                    long minutesUntilStart = ChronoUnit.MINUTES.between(now, a.getDate());
                    // Heuristic: 1 km is equivalent to 60 minutes of waiting
                    return distance * 60 + minutesUntilStart;
                }))
                .toList();

        return createPageFromList(filtered, pageable);
    }

    @Override
    public Page<Activity> getAllFutureActivities(Pageable pageable, String search, Tag tag) {
        List<Activity> filtered = getBaseStream(search, tag, null)
                .sorted(Comparator.comparing(Activity::getDate))
                .toList();
        
        return createPageFromList(filtered, pageable);
    }

    private Stream<Activity> getBaseStream(String search, Tag tag, Profile profile) {
        LocalDateTime now = LocalDateTime.now();
        Stream<Activity> stream = activityRepository.findAll().stream()
                .filter(a -> a.getDate().isAfter(now));

        if (profile != null) {
            stream = stream.filter(a -> !a.getStartedBy().getId().equals(profile.getId()));
            stream = stream.filter(a -> a.getTakePart().stream().noneMatch(p -> p.getId().equals(profile.getId())));
        }

        if (tag != null) {
            stream = stream.filter(a -> a.getTags().contains(tag));
        }

        if (search != null && !search.isBlank()) {
            String searchLower = search.toLowerCase();
            stream = stream.filter(a -> a.getTitle().toLowerCase().contains(searchLower));
        }

        return stream;
    }

    private Page<Activity> createPageFromList(List<Activity> activities, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), activities.size());
        
        if (start > activities.size()) {
             return new PageImpl<>(List.of(), pageable, activities.size());
        }
        
        List<Activity> pageContent = activities.subList(start, end);
        return new PageImpl<>(pageContent, pageable, activities.size());
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
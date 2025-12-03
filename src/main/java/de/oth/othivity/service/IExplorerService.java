package de.oth.othivity.service;

import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IExplorerService {
    Page<Activity> getClosestActivities(double lat, double lon, Pageable pageable, String search, Tag tag);
    Page<Activity> getSoonestActivities(Pageable pageable, String search, Tag tag);
    Page<Activity> getBestMixActivities(double lat, double lon, Pageable pageable, String search, Tag tag);
    Page<Activity> getAllFutureActivities(Pageable pageable, String search, Tag tag);
}

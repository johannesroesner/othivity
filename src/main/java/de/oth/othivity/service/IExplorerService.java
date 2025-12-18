package de.oth.othivity.service;

import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IExplorerService {

    Page<Activity> getClosestActivities(double lat, double lon, Pageable pageable, String search, Tag tag, Profile profile);

    Page<Activity> getSoonestActivities(Pageable pageable, String search, Tag tag, Profile profile);

    Page<Activity> getBestMixActivities(double lat, double lon, Pageable pageable, String search, Tag tag, Profile profile);
    
    Page<Activity> getAllFutureActivities(Pageable pageable, String search, Tag tag);
}

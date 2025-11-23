package de.oth.othivity.service;

import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import de.oth.othivity.model.main.Club;

@Service
public interface ImageService {
    void saveImagesForActivity(Activity activity, MultipartFile[] file);
    String saveImageForProfile(MultipartFile file);
    void saveImagesForClub(Club club, MultipartFile[] images);
    void deleteImagesForActivity(Activity activity);
    void deleteImagesForClub(Club club);
}

package de.oth.othivity.service;

import de.oth.othivity.model.main.Activity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {
    void saveImagesForActivity(Activity activity, MultipartFile[] file);
    void deleteImagesForActivity(Activity activity);
    String saveImageForProfile(MultipartFile file);
}

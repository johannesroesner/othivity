package de.oth.othivity.service.impl;

import de.oth.othivity.model.image.ActivityImage;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.repository.image.ActivityImageRepository;
import de.oth.othivity.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final ActivityImageRepository activityImageRepository;

    @Override
    public void saveImagesForActivity(Activity activity, MultipartFile[] images) {
        for(int i = 0; i < images.length; i++) {
            ActivityImage activityImage = new ActivityImage();
            activityImage.setActivity(activity);
            // String url = postInCloud(images[i]);
            activityImage.setUrl("dummy");
            activityImage.setPriority(i+1);
            activityImageRepository.save(activityImage);
        }
    }
}

package de.oth.othivity.service.impl;

import de.oth.othivity.model.image.ActivityImage;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.repository.image.ActivityImageRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.ImageService;
import de.oth.othivity.model.image.ClubImage;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.repository.image.ClubImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final ActivityImageRepository activityImageRepository;
    private final ProfileRepository profileRepository;
    private final ClubImageRepository clubImageRepository;

    @Override
    public void saveImagesForActivity(Activity activity, MultipartFile[] images) {
        for(int i = 0; i < images.length; i++) {
            ActivityImage activityImage = new ActivityImage();
            activityImage.setActivity(activity);
            // String url = postInCloud(images[i]);

            int randomId = ThreadLocalRandom.current().nextInt(1, 101);
            activityImage.setUrl("https://picsum.photos/id/" + randomId + "/200/300");

            activityImage.setPriority(i+1);
            activityImageRepository.save(activityImage);
        }
    }

    @Override
    public void deleteImagesForActivity(Activity activity) {
        // do something in the cloud to delete images
        List<ActivityImage> existingImages = activity.getImages();
        if (existingImages != null) existingImages.clear();
    }

    @Override
    public void saveImagesForClub(Club club, MultipartFile[] images) {
        for(int i = 0; i < images.length; i++) {
            ClubImage clubImage = new ClubImage();
            clubImage.setClub(club);
            // String url = postInCloud(images[i]);

            int randomId = ThreadLocalRandom.current().nextInt(1, 101);
            clubImage.setUrl("https://picsum.photos/id/" + randomId + "/200/300");

            clubImage.setPriority(i+1);
            clubImageRepository.save(clubImage);
        }
    }

    @Override
    public void deleteImagesForClub(Club club) {
        // do something in the cloud to delete images
        List<ClubImage> existingImages = club.getImages();
        if (existingImages != null) existingImages.clear();
    }

    @Override
    public String saveImageForProfile(MultipartFile image) {
            if(image == null || image.isEmpty()) {
                return null;
            }
            // String url = postInCloud(image);
        int randomId = ThreadLocalRandom.current().nextInt(1, 101);
        return "https://picsum.photos/id/" + randomId + "/200/300";
    }
}

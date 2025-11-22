package de.oth.othivity.service.impl;

import de.oth.othivity.model.image.ActivityImage;
import de.oth.othivity.model.image.ProfileImage;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.image.ActivityImageRepository;
import de.oth.othivity.model.image.ClubImage;
import de.oth.othivity.repository.image.ProfileImageRepository;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.repository.image.ClubImageRepository;
import de.oth.othivity.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final ActivityImageRepository activityImageRepository;
    private final ClubImageRepository clubImageRepository;
    private final ProfileImageRepository profileImageRepository;

    @Override
    public void saveImagesForActivity(Activity activity, MultipartFile[] images) {
        for(int i = 0; i < images.length; i++) {
            ActivityImage activityImage = new ActivityImage();
            activityImage.setActivity(activity);
            // String url = postInCloud(images[i]);
            activityImage.setUrl("https://picsum.photos/200");
            activityImage.setPriority(i+1);
            activityImageRepository.save(activityImage);
        }
    }
    public void saveImagesForClub(Club club, MultipartFile[] images) {
        for (int i = 0; i < images.length; i++) {
            ClubImage clubImage = new ClubImage();
            clubImage.setClub(club);
            // String url = postInCloud(images[i]);
            clubImage.setUrl("https://picsum.photos/200");
            clubImage.setPriority(i + 1);
            clubImageRepository.save(clubImage);
        }
    }

    @Override
    public void saveImagesForProfile(Profile profile, MultipartFile[] images) {
        for(int i = 0; i < images.length; i++) {
            ProfileImage profileImage = new ProfileImage();
            profileImage.setProfile(profile);
            // String url = postInCloud(images[i]);
            profileImage.setUrl("https://picsum.photos/200");
            profileImage.setPriority(i+1);
            profileImageRepository.save(profileImage);
        }
    }
}

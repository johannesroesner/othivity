package de.oth.othivity.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import de.oth.othivity.model.image.ActivityImage;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.repository.image.ActivityImageRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final ActivityImageRepository activityImageRepository;
    private final ProfileRepository profileRepository;

    private final Cloudinary cloudinary;



    private Map uploadInCloud(MultipartFile file) throws IOException {
        Map options = ObjectUtils.asMap("folder", "othivity_images");
        return cloudinary.uploader().upload(file.getBytes(), options);
    }

    private void deleteFromCloud(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }



    @Override
    public void saveImagesForActivity(Activity activity, MultipartFile[] images) {
        for(int i = 0; i < images.length; i++) {
            ActivityImage activityImage = new ActivityImage();
            activityImage.setActivity(activity);

            try {
                Map uploadResult = uploadInCloud(images[i]);

                String url = uploadResult.get("secure_url").toString();
                String publicId = uploadResult.get("public_id").toString();

                activityImage.setUrl(url);
                activityImage.setPublicId(publicId);

            } catch (IOException error){
                System.out.println(error.getMessage());
                continue;
            }

            activityImage.setPriority(i);
            activityImageRepository.save(activityImage);
        }
    }

    @Override
    public void deleteImagesForActivity(Activity activity) {
        List<ActivityImage> existingImages = activity.getImages();

        if (existingImages != null && !existingImages.isEmpty()) {
            for (ActivityImage image : existingImages) {
                String publicId = image.getPublicId();

                // Führe den Löschvorgang für JEDE publicId einzeln durch
                try {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (IOException error) {
                    System.err.println(error.getMessage());
                }
            }
            activityImageRepository.deleteAll(existingImages);
        }
    }


    @Override
    public String saveImageForProfile(MultipartFile image) {
        if(image == null || image.isEmpty()) {
            return null;
        }

        try {
            Map uploadResult = uploadInCloud(image);
            return uploadResult.get("secure_url").toString();

        } catch (IOException error) {
            System.err.println(error.getMessage());
            return null;
        }
    }
}

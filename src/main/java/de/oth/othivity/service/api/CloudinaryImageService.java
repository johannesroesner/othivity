package de.oth.othivity.service.api;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import de.oth.othivity.model.helper.Image;
import de.oth.othivity.model.interfaces.HasImage;
import de.oth.othivity.repository.helper.ImageRepository;
import de.oth.othivity.service.IImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
@Service
public class CloudinaryImageService implements IImageService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;


    private Map uploadInCloud(MultipartFile file) throws IOException {
        Map options = ObjectUtils.asMap("folder", "othivity_images");
        return cloudinary.uploader().upload(file.getBytes(), options);
    }

    private void deleteFromCloud(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    @Override
    public Image saveImage(HasImage entity, Image providedImage) {
        Image image = new Image();
        image.setUrl(providedImage.getUrl());
        image.setPublicId(providedImage.getPublicId());
        return imageRepository.save(image);
    }

    @Override
    public Image saveImage(HasImage entity, MultipartFile file) {
        Image image = new Image();
        try {
            Map uploadResult = uploadInCloud(file);

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();
            image.setUrl(url);
            image.setPublicId(publicId);
            imageRepository.save(image);
            if(entity.getImage() != null)imageRepository.delete(entity.getImage());
        } catch (IOException error){
            System.out.println(error.getMessage());
        }
        return image;
    }

    @Override
    public void deleteImage(Image image) {
        try {
            deleteFromCloud(image.getPublicId());
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }
    }
}

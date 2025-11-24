package de.oth.othivity.listener;

import de.oth.othivity.model.helper.Image;
import de.oth.othivity.service.ImageService;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageEntityListener {

    private static ImageService imageService;

    @Autowired
    public void init(ImageService imageService) {
        ImageEntityListener.imageService = imageService;
    }

    @PreRemove
    public void preRemove(Image image) {
        imageService.deleteImage(image);
    }
}

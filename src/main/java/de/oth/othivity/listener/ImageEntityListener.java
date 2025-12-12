package de.oth.othivity.listener;

import de.oth.othivity.model.helper.Image;
import de.oth.othivity.service.IImageService;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageEntityListener {

    private static IImageService imageService;

    @Autowired
    public void init(IImageService IImageService) {
        ImageEntityListener.imageService = IImageService;
    }

    @PreRemove
    public void preRemove(Image image) {
        imageService.deleteImage(image);
    }
}

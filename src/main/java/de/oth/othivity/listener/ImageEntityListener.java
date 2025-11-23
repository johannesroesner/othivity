package de.oth.othivity.listener;

import de.oth.othivity.model.helper.Image;
import de.oth.othivity.service.ImageService;
import jakarta.persistence.PreRemove;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ImageEntityListener {

    private static ImageService imageService;

    @PreRemove
    public void preRemove(Image image) {
        imageService.deleteImage(image);
    }
}

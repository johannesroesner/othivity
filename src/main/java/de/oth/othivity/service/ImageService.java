package de.oth.othivity.service;

import de.oth.othivity.model.helper.Image;
import de.oth.othivity.model.interfaces.HasImage;
import de.oth.othivity.model.main.Activity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {
    Image saveImage(HasImage entity, MultipartFile file);
    void deleteImage(Image image);
}

package de.oth.othivity.service;

import de.oth.othivity.model.helper.Image;
import de.oth.othivity.model.interfaces.HasImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface IImageService {
    Image saveImage(HasImage entity, Image providedImage);
    Image saveImage(HasImage entity, MultipartFile file);
    void deleteImage(Image image);
}

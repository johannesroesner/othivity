package de.oth.othivity.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUploadValidator {

    private static final long MAX_SIZE = 15 * 1024 * 1024; // 15 megabytes
    private static final long MAX_IMAGE_COUNT = 5;

    public String validate(MultipartFile[] images) {

        if (images == null || images.length == 0) {
            return "field.required";
        }

        if(images.length > MAX_IMAGE_COUNT) {
            return "image.uploadLimit";
        }

        for (MultipartFile file : images) {
            if (!file.isEmpty() && file.getSize() > MAX_SIZE) {
                return "image.sizeExceeded";
            }
        }

        return null;
    }
}

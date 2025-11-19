package de.oth.othivity.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUploadValidator {

    private static final long MAX_SIZE = 15 * 1024 * 1024; // 15 megabytes

    public String validate(MultipartFile[] images) {

        if (images == null || images.length == 0) {
            return "field.required";
        }

        for (MultipartFile file : images) {
            if (!file.isEmpty() && file.getSize() > MAX_SIZE) {
                return "image.sizeExceeded";
            }
        }

        return null;
    }
}

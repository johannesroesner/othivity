package de.oth.othivity.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUploadValidator {

    private static final long MAX_SIZE = 15 * 1024 * 1024; // 15 megabytes

    public String validateRequired(MultipartFile image) {
        if (image == null) return "field.required";
        if (!image.isEmpty() && image.getSize() > MAX_SIZE)return "image.sizeExceeded";
        return null;
    }

    public String validateNotRequired(MultipartFile image) {

        if (image == null) return null;
        if (!image.isEmpty() && image.getSize() > MAX_SIZE) return "image.sizeExceeded";
        return null;
    }
}

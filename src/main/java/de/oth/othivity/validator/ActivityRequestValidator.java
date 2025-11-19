package de.oth.othivity.validator;

import de.oth.othivity.dto.ActivityCreateRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ActivityRequestValidator implements Validator {

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 megabytes

    @Override
    public boolean supports(Class<?> clazz) {
        return ActivityCreateRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ActivityCreateRequest request = (ActivityCreateRequest) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "field.required", "Title is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "field.required", "Description is required");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.street", "field.required", "Street is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.houseNumber", "field.required", "House number is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.city", "field.required", "City is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.postalCode", "field.required", "Postal code is required");

        if (request.getDate() == null) {
            errors.rejectValue("date", "field.required", "Date is required");
        }
        if (request.getLanguage() == null) {
            errors.rejectValue("language", "field.required", "Language is required");
        }
        if (request.getAddress() == null) {
            errors.rejectValue("address", "field.required", "Address is required");
        }

        if (request.getGroupSize() < 2) {
            errors.rejectValue("groupSize", "bad.value", "Group size should be at least 2");
        }

        if (request.getDate() != null && request.getDate().isBefore(java.time.LocalDateTime.now().plusHours(1))) {
            errors.rejectValue("date", "date.future", "Date must be at least one hour in the future.");
        }
    }
}
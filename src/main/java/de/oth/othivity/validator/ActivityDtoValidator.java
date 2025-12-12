package de.oth.othivity.validator;

import de.oth.othivity.dto.ActivityDto;
import de.oth.othivity.model.main.Activity;
import de.oth.othivity.service.IActivityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@AllArgsConstructor
@Component
public class ActivityDtoValidator implements Validator {
    private IActivityService IActivityService;

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 megabytes

    @Override
    public boolean supports(Class<?> clazz) {
        return ActivityDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ActivityDto request = (ActivityDto) target;

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

        if (request.getId() != null) {
            Activity activity = IActivityService.getActivityById(request.getId());
            if (request.getGroupSize() < activity.getTakePart().size()) {
                errors.rejectValue("groupSize", "groupSize.currentExceeds", "Group size cannot be smaller than current number of participants");
            }
        }

        if (request.getGroupSize() < 2) {
            errors.rejectValue("groupSize", "groupSize.min", "Group size should be at least 2");
        }

        if (request.getDate() != null && request.getDate().isBefore(java.time.LocalDateTime.now().plusHours(1))) {
            errors.rejectValue("date", "date.future", "Date must be at least one hour in the future.");
        }
    }
}
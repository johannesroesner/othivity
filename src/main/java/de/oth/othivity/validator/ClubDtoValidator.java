package de.oth.othivity.validator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import de.oth.othivity.dto.ClubDto;

@Component
public class ClubDtoValidator implements Validator {

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 megabytes

    @Override
    public boolean supports(Class<?> clazz) {
        return ClubDto.class.equals(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        ClubDto request = (ClubDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", "Name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "field.required", "Description is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accessLevel", "field.required", "Access level is required");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.street", "field.required", "Street is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.houseNumber", "field.required", "House number is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.city", "field.required", "City is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address.postalCode", "field.required", "Postal code is required");

        if (request.getName() == null) {
            errors.rejectValue("name", "field.required", "Name is required");
        }
        if (request.getDescription() == null) {
            errors.rejectValue("description", "field.required", "Description is required");
        }
        if (request.getAddress() == null) {
            errors.rejectValue("address", "field.required", "Address is required");
        }

        if (request.getAccessLevel() == null) {
            errors.rejectValue("accessLevel", "field.required", "Access level is required");
        }
    }
}

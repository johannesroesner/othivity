package de.oth.othivity.validator;

import de.oth.othivity.dto.ProfileDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ProfileDtoValidator implements Validator {

        @Override
    public boolean supports(Class<?> clazz) {
        return ProfileDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfileDto request = (ProfileDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", "First name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required", "Last name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "field.required", "Username is required");

        if (request.getFirstName() == null) {
            errors.rejectValue("firstName", "field.required", "First name is required");
        }
        if (request.getLastName() == null) {
            errors.rejectValue("lastName", "field.required", "Last name is required");
        }
        if (request.getUsername() == null) {
            errors.rejectValue("username", "field.required", "Username is required");
        }
    }
}
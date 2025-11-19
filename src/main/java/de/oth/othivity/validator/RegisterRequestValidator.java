package de.oth.othivity.validator;

import de.oth.othivity.dto.RegisterRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

public class RegisterRequestValidator implements Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", "First name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required", "Last name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "field.required", "Email is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", "Password is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "matchingPassword", "field.required", "Confirm password is required");

        RegisterRequest request = (RegisterRequest) target;

        if (request.getEmail() != null && !request.getEmail().isEmpty() && !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            errors.rejectValue("email", "field.invalid", "Invalid email format");
        }

        if (request.getPassword() != null && request.getPassword().length() < 8) {
            errors.rejectValue("password", "field.min.length", "Password must be at least 8 characters");
        }

        if (request.getPassword() != null && request.getMatchingPassword() != null && !request.getPassword().equals(request.getMatchingPassword())) {
            errors.rejectValue("matchingPassword", "field.mismatch", "Passwords do not match");
        }
    }
}

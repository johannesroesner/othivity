package de.oth.othivity.validator;

import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.service.ProfileService;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import lombok.AllArgsConstructor;

import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class RegisterDtoValidator implements Validator {

    private final ProfileService profileService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", "First name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required", "Last name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "field.required", "username is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "field.required", "Email is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", "Password is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "matchingPassword", "field.required", "Confirm password is required");

        RegisterDto request = (RegisterDto) target;

        if (request.getUsername() != null && !USERNAME_PATTERN.matcher(request.getUsername()).matches()) {
            errors.rejectValue("username", "register.error.usernameInvalid");
        }

        if (request.getUsername() != null && profileService.isUsernameTaken(request.getUsername())) {
            errors.rejectValue("username", "register.error.usernameExists");
        }


        if (request.getEmail() != null && profileService.isEmailTaken(request.getEmail())) {
            errors.rejectValue("email", "register.error.emailExists");
        }
        
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

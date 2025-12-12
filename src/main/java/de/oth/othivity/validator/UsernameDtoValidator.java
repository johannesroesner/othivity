package de.oth.othivity.validator;

import de.oth.othivity.dto.UsernameDto;

import de.oth.othivity.service.IProfileService;
import lombok.AllArgsConstructor;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@AllArgsConstructor
@Component
public class UsernameDtoValidator implements Validator {

    private final IProfileService IProfileService;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    @Override
    public boolean supports(Class<?> clazz) {
        return UsernameDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "field.required", "Username is required");

        UsernameDto request = (UsernameDto) target;
        
        if (request.getUsername() != null && !USERNAME_PATTERN.matcher(request.getUsername()).matches()) {
            errors.rejectValue("username", "register.error.usernameInvalid");
        }

        if (request.getUsername() != null && IProfileService.isUsernameTaken(request.getUsername())) {
            errors.rejectValue("username", "register.error.usernameExists");
        }
    }
}

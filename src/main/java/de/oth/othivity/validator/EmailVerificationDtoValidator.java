package de.oth.othivity.validator;

import de.oth.othivity.dto.EmailVerificationDto;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class EmailVerificationDtoValidator implements Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public boolean supports(Class<?> clazz) {
        return EmailVerificationDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EmailVerificationDto request = (EmailVerificationDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "token", "field.required", "Token is required");
    }
}

package de.oth.othivity.validator;

import de.oth.othivity.dto.PhoneVerificationDto;
import de.oth.othivity.service.ISmsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@AllArgsConstructor
@Component
public class PhoneVerificationDtoValidator implements Validator {

    private ISmsService ISmsService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PhoneVerificationDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "field.required", "Code");

        PhoneVerificationDto request = (PhoneVerificationDto) target;
        if (!ISmsService.checkVerification(request.getNumber(), request.getCode())) {
            errors.rejectValue("code", "verification.code.notValid", "Invalid verification code");
        }
    }
}

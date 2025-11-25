package de.oth.othivity.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import de.oth.othivity.dto.ClubJoinRequestDto;

@Component
public class ClubJoinRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ClubJoinRequestDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ClubJoinRequestDto request = (ClubJoinRequestDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "text", "field.required", "Message is required");

        if (request.getText() != null && request.getText().length() > 500) {
            errors.rejectValue("text", "field.maxLength", "Message must not exceed 500 characters");
        }

        if (request.getClubId() == null) {
            errors.rejectValue("clubId", "field.required", "Club ID is required");
        }
    }
}
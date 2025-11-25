package de.oth.othivity.validator;

import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.repository.helper.PhoneRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@AllArgsConstructor
@Component
public class ProfileDtoValidator implements Validator {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\+[0-9]{7,15}$");

    private final PhoneRepository phoneRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return ProfileDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfileDto request = (ProfileDto) target;

        if(request.getPhone() != null && request.getPhone().getNumber() != null && request.getPhone().getNumber().isEmpty()) {
                errors.rejectValue("phone.number", "bad.value", "Phone number is not valid");
        }

        if(request.getPhone() != null && request.getPhone().getNumber() != null && !PHONE_NUMBER_PATTERN.matcher(request.getPhone().getNumber()).matches()) {
            errors.rejectValue("phone.number", "phone.formatInvalid", "Phone number format is invalid. It should start with '+' followed by 7 to 15 digits.");
        }



        if(request.getPhone() != null && request.getPhone().getNumber() != null){
            if(phoneRepository.findByNumber(request.getPhone().getNumber()).isPresent()) {
                errors.rejectValue("phone.number", "phone.notUnique", "Phone number is already in use");
            }
        }
    }
}
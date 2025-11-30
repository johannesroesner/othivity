package de.oth.othivity.validator;

import de.oth.othivity.dto.ChatMessageDto;
import de.oth.othivity.dto.ClubDto;
import de.oth.othivity.model.chat.Chat;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ChatMessageDtoValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ChatMessageDto.class.equals(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        ChatMessageDto request = (ChatMessageDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "field.required", "Text is required");

        if(request.getContent()!=null && request.getContent().length()>500) {
            errors.rejectValue("content", "message.toLong", "Text is too long");
        }
    }
}

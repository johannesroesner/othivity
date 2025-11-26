package de.oth.othivity.controller;

import de.oth.othivity.dto.ActivityDto;
import de.oth.othivity.dto.ChatMessageDto;
import de.oth.othivity.model.chat.Chat;
import de.oth.othivity.model.chat.ChatId;
import de.oth.othivity.model.chat.ChatMessage;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.service.ChatService;
import de.oth.othivity.service.SessionService;
import de.oth.othivity.validator.ChatMessageDtoValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;
    private final SessionService sessionService;

    private final ChatMessageDtoValidator chatMessageDtoValidator;

    @InitBinder("chatMessageDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(chatMessageDtoValidator);
    }

    @GetMapping("/chat/{id}")
    public String getChat(HttpSession session, Model model, @PathVariable("id") String id) {
        ChatId chatId = ChatId.fromUrlString(id);
        if(chatId == null) return "redirect:/dashbaord";

        model.addAttribute("allChats", chatService.getAllChatsForProfile(session));
        model.addAttribute("chatMessageDto", new ChatMessageDto());
        model.addAttribute("chat", chatService.getOrCreateChatById(chatId));
        return "chat";
    }

    @PostMapping("/chat/send/{id}")
    public String postChatMessage(@Valid @ModelAttribute("chatMessageDto") ChatMessageDto chatMessageDto, BindingResult bindingResult, HttpSession session, Model model, @PathVariable("id") String id) {
        ChatId chatId = ChatId.fromUrlString(id);
        if(chatId == null) return "redirect:/dashboard";

        Chat chat = chatService.getOrCreateChatById(chatId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("allChats", chatService.getAllChatsForProfile(session));
            model.addAttribute("chatMessageDto", new ChatMessageDto());
            model.addAttribute("chat", chat);
            return "chat";
        }
        chatService.addMessageToChat(chatMessageDto, chat, sessionService.getProfileFromSession(session));
        return  "redirect:/chat/" + id;
    }

}

package de.oth.othivity.controller;

import de.oth.othivity.dto.ChatMessageDto;
import de.oth.othivity.model.chat.Chat;
import de.oth.othivity.model.chat.ChatId;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.service.IChatService;
import de.oth.othivity.service.ISessionService;
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

    private final IChatService chatService;
    private final ISessionService sessionService;

    private final ChatMessageDtoValidator chatMessageDtoValidator;

    @InitBinder("chatMessageDto")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(chatMessageDtoValidator);
    }

    @GetMapping("/chats")
    public String getChatOverview(HttpSession session, Model model) {

        Profile currentProfile = sessionService.getProfileFromSession(session);
        if (currentProfile == null) return "redirect:/login";

        model.addAttribute("allChats", chatService.getAllChatsForProfile(session));
        model.addAttribute("chat", null);
        model.addAttribute("chatMessageDto", new ChatMessageDto());
        model.addAttribute("returnUrl", "/dashboard");

        return "chat";
    }

    @GetMapping("/chat/{id}")
    public String getChat(HttpSession session, Model model, @PathVariable("id") String id) {
        ChatId chatId = ChatId.fromUrlString(id);
        if(chatId == null) return "redirect:/dashbaord";
        Chat chat = chatService.getOrCreateChatById(chatId);

        model.addAttribute("allChats", chatService.getAllChatsForProfile(session));
        model.addAttribute("chatMessageDto", new ChatMessageDto());
        model.addAttribute("chat", chat);

        chatService.setMessagesReadTrue(chat, session);
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

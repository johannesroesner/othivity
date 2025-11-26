package de.oth.othivity.service;

import de.oth.othivity.dto.ChatMessageDto;
import de.oth.othivity.model.chat.Chat;
import de.oth.othivity.model.chat.ChatId;
import de.oth.othivity.model.main.Profile;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    List<Chat> getAllChatsForProfile(HttpSession session);

    Chat getOrCreateChatById(ChatId chatId);

    String buildChatId(Profile profileA, Profile profileB);

    void addMessageToChat(ChatMessageDto chatMessageDto, Chat chat, Profile currentProfile);

}

package de.oth.othivity.service.impl;

import de.oth.othivity.dto.ChatMessageDto;
import de.oth.othivity.model.chat.Chat;
import de.oth.othivity.model.chat.ChatId;
import de.oth.othivity.model.chat.ChatMessage;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.chat.ChatMessageRepository;
import de.oth.othivity.repository.chat.ChatRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.service.ChatService;
import de.oth.othivity.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final SessionService sessionService;

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProfileRepository profileRepository;

    @Override
    public List<Chat> getAllChatsForProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if(profile==null) return null;

        List<Chat> allChats = new ArrayList<>();
        if (profile.getChatAsA() != null) allChats.addAll(profile.getChatAsA());
        if (profile.getChatAsB() != null) allChats.addAll(profile.getChatAsB());
        for (Chat chat : allChats) {
            chat.setUnreadStatusForCurrentUser(hasUnreadMessages(chat, profile));
        }
        return allChats;
    }

    @Override
    public Chat getOrCreateChatById(ChatId chatId) {
        return chatRepository.findById(chatId)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setId(chatId);
                    Profile profileA = profileRepository.findById(chatId.getProfileAId()).orElse(null);
                    Profile profileB = profileRepository.findById(chatId.getProfileBId()).orElse(null);
                    chat.setProfileA(profileA);
                    chat.setProfileB(profileB);
                    return chatRepository.save(chat);
                });
    }

    @Override
    public String buildChatId(Profile profileA, Profile profileB) {
        UUID idA = profileA.getId();
        UUID idB = profileB.getId();

        if (idA == null || idB == null) return null;

        UUID first  = idA.compareTo(idB) < 0 ? idA : idB;
        UUID second = idA.compareTo(idB) < 0 ? idB : idA;

        return first.toString() + "_" + second.toString();
    }

    @Override
    public void addMessageToChat(ChatMessageDto chatMessageDto, Chat chat, Profile currentProfile) {
        if (currentProfile == null ||chat == null || chatMessageDto == null || chatMessageDto.getContent() == null || chatMessageDto.getContent().isBlank()) return;
        ChatMessage message = new ChatMessage();
        message.setContent(chatMessageDto.getContent());
        message.setChat(chat);
        message.setRead(false);
        message.setTimestamp(LocalDateTime.now());

        message.setSender(currentProfile);
        message.setReceiver(chat.getProfileA().equals(currentProfile) ? chat.getProfileB() : chat.getProfileA());

        chat.getMessages().add(message);

        chatRepository.save(chat);
    }

    @Override
    public void setMessagesReadTrue(Chat chat, HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if(profile==null) return;

        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatAndReceiverAndIsReadFalse(chat, profile);
        for (ChatMessage message : unreadMessages) {
            message.setRead(true);
        }

        if (!unreadMessages.isEmpty()) {
            chatMessageRepository.saveAll(unreadMessages);
        }

    }

    @Override
    public long getUnreadMessageCountForProfile(HttpSession session) {
        Profile profile = sessionService.getProfileFromSession(session);
        if(profile==null) return 0;
        return chatMessageRepository.countByReceiverAndIsReadFalse(profile);
    }

    @Override
    public boolean hasUnreadMessages(Chat chat, Profile profile) {;
        if(profile==null || chat == null || chat.getMessages() == null) return false;

        return chat.getMessages().stream()
                .anyMatch(message ->
                        !message.getSender().getId().equals(profile.getId()) &&
                                !message.isRead()
       );
    }

}

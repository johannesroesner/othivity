package de.oth.othivity.repository.chat;


import de.oth.othivity.model.chat.Chat;
import de.oth.othivity.model.chat.ChatMessage;
import de.oth.othivity.model.main.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findByChatAndReceiverAndIsReadFalse(Chat chat, Profile receiver);

    long countByReceiverAndIsReadFalse(Profile receiver);

}

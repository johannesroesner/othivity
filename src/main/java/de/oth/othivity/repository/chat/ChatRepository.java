package de.oth.othivity.repository.chat;


import de.oth.othivity.model.chat.Chat;
import de.oth.othivity.model.chat.ChatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, ChatId> {
}

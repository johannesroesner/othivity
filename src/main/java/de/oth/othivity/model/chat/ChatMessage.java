package de.oth.othivity.model.chat;

import de.oth.othivity.model.main.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "profile_a_id", referencedColumnName = "profile_a_id"),
            @JoinColumn(name = "profile_b_id", referencedColumnName = "profile_b_id")
    })
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Profile sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Profile receiver;

    // receiver perspective
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
package de.oth.othivity.model.chat;

import de.oth.othivity.model.main.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    // a.uuid_b.uuid : a.uuid < b.uuid (128 bit value)
    @EmbeddedId
    private ChatId id;

    @MapsId("profileAId")
    @ManyToOne
    @JoinColumn(name = "profile_a_id", nullable = false)
    private Profile profileA;

    @MapsId("profileBId")
    @ManyToOne
    @JoinColumn(name = "profile_b_id", nullable = false)
    private Profile profileB;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    // view helper
    public String getUrlId(){
        return id.getProfileAId() + "_" + id.getProfileBId();
    }

    public ChatMessage getLatestMessage() {
        if (messages == null || messages.isEmpty()) return null;
        return messages.stream()
                .max((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .orElse(null);
    }

}
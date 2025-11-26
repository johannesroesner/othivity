package de.oth.othivity.model.chat;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatId implements Serializable {

    private UUID profileAId;
    private UUID profileBId;

    public static ChatId of(UUID a, UUID b) {
        if (a.compareTo(b) < 0) return new ChatId(a, b);
        else return new ChatId(b, a);
    }

    public static ChatId fromUrlString(String urlString) {
        String[] parts = urlString.split("_");
        if (parts.length != 2) return null;
        UUID a = UUID.fromString(parts[0]);
        UUID b = UUID.fromString(parts[1]);
        return ChatId.of(UUID.fromString(parts[0]), UUID.fromString(parts[1]));
    }
}

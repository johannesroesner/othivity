package de.oth.othivity.model.pushnotification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import de.oth.othivity.model.main.Profile;
import java.util.UUID;

@Entity
@Table(name = "push_subscription")
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class PushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID id;

    @Column(length = 500) 
    private String endpoint;

    private String p256dh;
    private String auth;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public PushSubscription(String endpoint, String p256dh, String auth, Profile profile) {
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
        this.profile = profile;
    }
}
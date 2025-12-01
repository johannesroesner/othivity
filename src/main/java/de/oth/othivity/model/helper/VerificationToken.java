package de.oth.othivity.model.helper;

import de.oth.othivity.model.main.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.access.method.P;

@Entity
@Table(name = "verification_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    private static final int EXPIRATION = 10; // 10 Minuten gültig

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String token;

    private Date expiryDate;

    @OneToOne(targetEntity = Profile.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "profile_id")
    private Profile profile;

    public VerificationToken(String token, Profile profile) {
        this.token = token;
        this.profile = profile;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
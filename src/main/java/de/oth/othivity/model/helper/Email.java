package de.oth.othivity.model.helper;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import de.oth.othivity.model.main.Profile;

@Entity
@Table(name = "email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    public Email(String address) {
        this.address = address;
        this.verified = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String address;

    @Column(nullable = false)
    private Boolean verified = false;

    public Boolean isVerified() {
    return verified;
    }
}

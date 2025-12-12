package de.oth.othivity.model.helper;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

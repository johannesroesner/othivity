package de.oth.othivity.model.helper;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "phone")
@Getter
@Setter
@AllArgsConstructor
public class Phone {

    public Phone() {
        this.verified = false;
    }

    public Phone(String number) {
        this.number = number;
        this.verified = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private Boolean verified;
}

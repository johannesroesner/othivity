package de.oth.othivity.model.report;

import de.oth.othivity.model.main.Profile;
import jakarta.persistence.*;

import java.util.UUID;

@MappedSuperclass
public abstract class BaseReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 3000)
    private String comment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "issuer_profile_id", nullable = false)
    private Profile issuer;
}

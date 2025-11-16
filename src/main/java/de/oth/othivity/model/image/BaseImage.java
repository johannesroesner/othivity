package de.oth.othivity.model.image;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private int priority;
}

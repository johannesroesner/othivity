package de.oth.othivity.model.helper;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
public abstract class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String publicId;

    @Column(nullable = false)
    private int priority;
}

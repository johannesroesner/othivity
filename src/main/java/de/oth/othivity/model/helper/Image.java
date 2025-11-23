package de.oth.othivity.model.helper;

import de.oth.othivity.listener.ImageEntityListener;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "image")
@EntityListeners(ImageEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String publicId;
}

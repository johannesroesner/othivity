package de.oth.othivity.model.image;

import de.oth.othivity.model.main.Activity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "activity_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityImage extends BaseImage {

    @ManyToOne(optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;
}

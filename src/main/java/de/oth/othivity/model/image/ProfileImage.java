package de.oth.othivity.model.image;

import de.oth.othivity.model.main.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "profile_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImage extends BaseImage {

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}

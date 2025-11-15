package de.oth.othivity.model.image;

import de.oth.othivity.model.main.Club;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "club_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubImage extends BaseImage {

    @ManyToOne(optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
}

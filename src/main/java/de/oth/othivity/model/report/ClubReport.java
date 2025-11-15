package de.oth.othivity.model.report;

import de.oth.othivity.model.main.Club;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "club_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubReport extends BaseReport {

    @ManyToOne(optional = false)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
}

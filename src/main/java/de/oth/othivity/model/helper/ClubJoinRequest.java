package de.oth.othivity.model.helper;

import jakarta.persistence.*;
import lombok.*;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Profile;

import java.util.UUID;

@Entity
@Table(name = "club_join_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubJoinRequest{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

}

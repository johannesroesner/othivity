package de.oth.othivity.model.main;

import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.helper.Image;
import de.oth.othivity.model.interfaces.HasImage;
import de.oth.othivity.model.report.ClubReport;
import de.oth.othivity.model.helper.ClubJoinRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "club")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Club implements HasImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessLevel accessLevel;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToMany
    @JoinTable(
            name = "club_members",
            joinColumns = @JoinColumn(name = "club_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private List<Profile> members = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "club_admins",
            joinColumns = @JoinColumn(name = "club_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private List<Profile> admins = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubReport> reports = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubJoinRequest> joinRequests = new ArrayList<>();
}

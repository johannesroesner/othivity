package de.oth.othivity.model.main;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.report.ProfileReport;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.image.ProfileImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    // profile attributes
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(length = 3000)
    private String aboutMe;

    private String phone;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "startedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> startedActivities = new ArrayList<>();

    @ManyToMany(mappedBy = "takePart")
    private List<Activity> participatingActivities = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Club> clubs = new ArrayList<>();

    @ManyToMany(mappedBy = "admins")
    private List<Club> adminClubs = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileReport> reports = new ArrayList<>();
}

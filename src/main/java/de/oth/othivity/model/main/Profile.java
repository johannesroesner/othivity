package de.oth.othivity.model.main;

import de.oth.othivity.model.enumeration.Role;
import de.oth.othivity.model.chat.Chat;
import de.oth.othivity.model.helper.Image;
import de.oth.othivity.model.helper.Phone;
import de.oth.othivity.model.helper.Email;
import de.oth.othivity.model.helper.Notification;
import de.oth.othivity.model.interfaces.HasImage;
import de.oth.othivity.model.pushnotification.PushSubscription;
import de.oth.othivity.model.report.ProfileReport;
import de.oth.othivity.model.helper.ClubJoinRequest;
import de.oth.othivity.model.security.User;
import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Theme;
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
public class Profile implements HasImage {

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "email_id")
    private Email email;

    @Column(length = 3000)
    private String aboutMe;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Theme theme = Theme.LIGHT;

    @Column(nullable = false)
    private Boolean setupComplete = false;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;
    
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PushSubscription> subscriptions = new ArrayList<>();
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "phone_id")
    private Phone phone;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubJoinRequest> clubJoinRequests = new ArrayList<>();

    @OneToMany(mappedBy = "profileA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chatAsA = new ArrayList<>();

    @OneToMany(mappedBy = "profileB", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chatAsB = new ArrayList<>();

    public String getInitials() {
        return (firstName.substring(0,1) + lastName.substring(0,1)).toUpperCase();
    }
}

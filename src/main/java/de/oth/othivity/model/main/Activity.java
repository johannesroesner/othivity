package de.oth.othivity.model.main;

import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.image.ActivityImage;
import de.oth.othivity.model.report.ActivityReport;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(nullable = false)
    private int groupSize;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityImage> images = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "started_by_profile_id")
    private Profile startedBy;

    @ManyToMany
    @JoinTable(
            name = "activity_participants",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private List<Profile> takePart;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club organizer;

    @ElementCollection(targetClass = Tag.class)
    @CollectionTable(name = "activity_tags", joinColumns = @JoinColumn(name = "activity_id"))
    @Column(name = "tag")
    @Enumerated(EnumType.STRING)
    private List<Tag> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityReport> reports = new ArrayList<>();

    // view helper
    public String getParticipantStatus() {
        return takePart.size() + "/" + groupSize;
    }

    public String getDateString() {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String getTimeString() {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}

package de.oth.othivity.model.main;

import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.helper.Tag;
import de.oth.othivity.model.image.ActivityImage;
import de.oth.othivity.model.report.ActivityReport;
import jakarta.persistence.*;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private Date date;

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

    @ManyToMany
    @JoinTable(
            name = "activity_tags",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityReport> reports = new ArrayList<>();

    // view helper
    public String getParticipantStatus() {
        return takePart.size() + "/" + groupSize;
    }

    public String getTagNames() {
        return tags.stream()
                .map(Tag::getName)
                .collect(java.util.stream.Collectors.joining(", "));
    }

    public String getDateString() {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public String getTimeString() {
        if (date == null) return "";
        return new SimpleDateFormat("HH:mm").format(date);
    }
}

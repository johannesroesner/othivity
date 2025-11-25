package de.oth.othivity.dto;

import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.helper.Image;
import de.oth.othivity.model.main.Club;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {

    private UUID id;

    private String title;

    private String description;

    private LocalDateTime date;

    private Language language;

    private int groupSize;

    private Club organizer;

    private List<Tag> tags = new ArrayList<>();

    private Address address;

    private Image image;
}

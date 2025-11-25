package de.oth.othivity.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.enumeration.AccessLevel;
import de.oth.othivity.model.helper.Image;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubDto {
    
    private UUID id;
    private String name;
    private String description;
    private AccessLevel accessLevel;
    private Address address;
    private Image image;
}

package de.oth.othivity.dto;

import de.oth.othivity.model.enumeration.Language;
import de.oth.othivity.model.enumeration.Tag;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.main.Club;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String aboutMe;

    private String phone;
}

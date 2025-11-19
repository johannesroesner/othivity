package de.oth.othivity.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec.Access;
import org.springframework.web.multipart.MultipartFile;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.enumeration.AccessLevel;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubDto {
    
    private String name;
    private String description;
    private AccessLevel accessLevel;
    private Address address;
    private MultipartFile [] uploadedImages;
}

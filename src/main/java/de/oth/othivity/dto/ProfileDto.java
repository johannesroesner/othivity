package de.oth.othivity.dto;

import de.oth.othivity.model.helper.Phone;
import de.oth.othivity.model.helper.Email;
import de.oth.othivity.model.helper.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private String firstName;

    private String lastName;

    private String username;

    private Email email;

    private String aboutMe;

    private Phone phone;
    
    private Image image;
}

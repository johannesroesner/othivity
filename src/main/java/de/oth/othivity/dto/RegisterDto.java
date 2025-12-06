package de.oth.othivity.dto;

import de.oth.othivity.model.helper.Image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String firstName;

    private String lastName;

    private String username;
    
    private String email;
    
    private String password;
    
    private String matchingPassword;

    private Image image;
}

package de.oth.othivity.api.dto;

import lombok.Data;

@Data
public class ProfileApiDto {
    String id; // uuid as string
    String firstName;
    String lastName;
    String username;
    String email;
    String password;
    String aboutMe;
    String phone;
    String imageUrl;
    String language;
    String theme;
}

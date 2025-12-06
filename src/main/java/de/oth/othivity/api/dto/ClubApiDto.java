package de.oth.othivity.api.dto;

import lombok.Data;

@Data
public class ClubApiDto {
    private String id;
    private String name;
    private String description;
    private String accessLevel;
    private String imageUrl;
    private String[] admins; // uuids as strings
    private String[] members; // uuids as strings
    
    //address
    private String addition;
    private String street;
    private String houseNumber;
    private String postalCode;
    private String city;
    private String country;
    private String latitude;
    private String longitude;
}

package de.oth.othivity.api.dto;

import lombok.Data;

@Data
public class ClubApiDto {
    private String id;
    private String name;
    private String description;
    private String accessLevel;
    private String address;
    private String latitude;
    private String longitude;
    private String imageUrl;
    private String[] admins; // uuids as strings
    private String[] members; // uuids as strings
}

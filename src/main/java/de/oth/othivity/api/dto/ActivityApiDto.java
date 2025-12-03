package de.oth.othivity.api.dto;

import lombok.Data;

@Data
public class ActivityApiDto {
    private String id; // uuid as string
    private String title;
    private String description;
    private String date;
    private String language;
    private int groupSize;
    private String organizerId; // uuid as string
    private String latitude;
    private String longitude;
    private String imageUrl;
    private String[] tags;
    private String startedBy; // uuid as string
    private String[] takePart; // uuids as strings

    //address
    private String addition;
    private String street;
    private String houseNumber;
    private String postalCode;
    private String city;
    private String country;
}

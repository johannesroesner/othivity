package de.oth.othivity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkerDto {
    private Double latitude;
    private Double longitude;
    private String url;
    private String title;
}

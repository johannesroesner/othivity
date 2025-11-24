package de.oth.othivity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubJoinRequestDto {
    
    private UUID clubId;
    private String text;
}
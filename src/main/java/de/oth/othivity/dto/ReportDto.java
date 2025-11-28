package de.oth.othivity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportDto {
    @NotBlank
    private String comment;
}

package de.oth.othivity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationDto {

    private String token;

}

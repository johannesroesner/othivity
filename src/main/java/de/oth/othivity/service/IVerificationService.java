package de.oth.othivity.service;

import de.oth.othivity.dto.PhoneVerificationDto;
import de.oth.othivity.model.main.Profile;
import org.springframework.stereotype.Service;

@Service
public interface IVerificationService {
    PhoneVerificationDto buildPhoneVerificationDto(Profile profile);

    void setVerificationForPhone(Profile profile);
}

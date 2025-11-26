package de.oth.othivity.service.impl;

import de.oth.othivity.dto.PhoneVerificationDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.service.VerificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class VerificationServiceImpl implements VerificationService {

    ProfileRepository profileRepository;

    @Override
    public PhoneVerificationDto buildPhoneVerificationDto(Profile profile) {
        PhoneVerificationDto phoneVerificationDto = new PhoneVerificationDto();
        phoneVerificationDto.setNumber(profile.getPhone().getNumber());
        return phoneVerificationDto;
    }

    @Override
    public void setVerificationForPhone(Profile profile) {
        profile.getPhone().setVerified(true);
        profileRepository.save(profile);
    }
}

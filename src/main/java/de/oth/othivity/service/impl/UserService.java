package de.oth.othivity.service.impl;

import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.service.IUserService;
import de.oth.othivity.service.IProfileService;
import de.oth.othivity.model.main.Profile;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

import de.oth.othivity.dto.RegisterDto;


@AllArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final IProfileService IProfileService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerNewUserAccount(RegisterDto registerDto, Locale locale, boolean needSetup, boolean needVerificationEmail) {

        User user = new User();
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail().toLowerCase());
        
        User savedUser = userRepository.save(user);
        
        Profile profile = IProfileService.createProfileFromUser(savedUser, registerDto, locale, needSetup, needVerificationEmail);

        savedUser.setProfile(profile);
        userRepository.save(savedUser);

        return savedUser;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

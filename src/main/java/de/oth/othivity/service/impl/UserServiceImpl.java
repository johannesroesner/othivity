package de.oth.othivity.service.impl;

import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.service.IUserService;
import de.oth.othivity.service.ProfileService;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.config.AppConfig;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

import de.oth.othivity.dto.RegisterDto;


@AllArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerNewUserAccount(RegisterDto registerDto, Locale locale) {

        User user = new User();
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        
        User savedUser = userRepository.save(user);
        
        Profile profile = profileService.createProfileFromUser(savedUser, registerDto, locale);

        savedUser.setProfile(profile);
        userRepository.save(savedUser);

        return savedUser;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

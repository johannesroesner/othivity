package de.oth.othivity.service.impl;

import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.security.UserRepository;
import de.oth.othivity.service.IUserService;
import de.oth.othivity.service.ProfileService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

import de.oth.othivity.dto.RegisterRequest;
import de.oth.othivity.exception.UserAlreadyExistException;


@AllArgsConstructor
@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerNewUserAccount(RegisterRequest registerRequest) throws UserAlreadyExistException {
        if (emailExists(registerRequest.getEmail())) {
            throw new UserAlreadyExistException("User already exist");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        
        User savedUser = userRepository.save(user);
        
        profileService.createProfileFromUser(savedUser, registerRequest);

        return savedUser;
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}

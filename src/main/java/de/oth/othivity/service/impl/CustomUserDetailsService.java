package de.oth.othivity.service.impl;

import de.oth.othivity.model.security.CustomUserDetails;
import de.oth.othivity.model.security.User;
import de.oth.othivity.repository.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.toLowerCase());
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        return new CustomUserDetails(user);
    }
}

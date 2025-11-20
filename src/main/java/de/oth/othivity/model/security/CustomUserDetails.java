package de.oth.othivity.model.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final UUID profileId; // NEU

    public CustomUserDetails(User user) {
        this.user = user;
        this.profileId = user.getProfile() != null ? user.getProfile().getId() : null;
    }

    public UUID getProfileId() {
        return profileId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map Profile role to Spring Security authority
        if (user.getProfile() != null && user.getProfile().getRole() != null) {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getProfile().getRole().name()) // generates Spring Role with "ROLE_" prefix
            );
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Use email as username
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}

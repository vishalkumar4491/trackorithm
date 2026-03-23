package com.trackorithm.track.security;

import com.trackorithm.track.common.enums.Role;
import com.trackorithm.track.modules.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private final UUID id;
    private final String username;
    private final String passwordHash;
    private final Role role;

    public static UserPrincipal from(User user) {
        // Spring Security needs a non-null username; fall back to email.
        String principalName = user.getUsername() != null && !user.getUsername().isBlank()
                ? user.getUsername()
                : user.getEmail();
        return new UserPrincipal(user.getId(), principalName, user.getPasswordHash(), user.getRole());
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
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
}


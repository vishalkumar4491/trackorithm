package com.trackorithm.track.modules.auth.service;

import com.trackorithm.track.common.enums.AuthProvider;
import com.trackorithm.track.common.enums.Role;
import com.trackorithm.track.common.enums.UserStatus;
import com.trackorithm.track.modules.auth.dto.AuthResponse;
import com.trackorithm.track.modules.auth.dto.LoginRequest;
import com.trackorithm.track.modules.auth.dto.RegisterRequest;
import com.trackorithm.track.modules.user.entity.User;
import com.trackorithm.track.modules.user.repo.UserRepository;
import com.trackorithm.track.security.JwtProperties;
import com.trackorithm.track.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (request.phoneNumber() != null && !request.phoneNumber().isBlank() && userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new IllegalArgumentException("Phone number already in use");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPhoneNumber(blankToNull(request.phoneNumber()));
        user.setName(blankToNull(request.name()));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.USER);

        userRepository.save(user);

        String token = issueAccessToken(user);
        return AuthResponse.bearer(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identifier(), request.password())
            );
        } catch (BadCredentialsException ex) {
            throw ex;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new IllegalStateException("Unexpected principal type");
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalStateException("User missing"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException("User is not active");
        }

        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        String token = issueAccessToken(user);
        return AuthResponse.bearer(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    private String issueAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.accessTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(user.getId().toString())
                .claim("roles", List.of(user.getRole().name()))
                .claim("username", user.getUsername())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

package com.trackorithm.track.modules.user.controller;

import com.trackorithm.track.modules.user.repo.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MeController {
    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return userRepository.findById(userId)
                .map(u -> Map.<String, Object>of(
                        "userId", u.getId(),
                        "username", u.getUsername(),
                        "email", u.getEmail(),
                        "role", u.getRole().name()
                ))
                .orElseGet(() -> Map.of("userId", userId));
    }
}


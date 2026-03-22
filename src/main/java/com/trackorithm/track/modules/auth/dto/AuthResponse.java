package com.trackorithm.track.modules.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UUID userId,
        String username,
        String email,
        String role
) {
    public static AuthResponse bearer(String token, UUID userId, String username, String email, String role) {
        return new AuthResponse(token, "Bearer", userId, username, email, role);
    }
}


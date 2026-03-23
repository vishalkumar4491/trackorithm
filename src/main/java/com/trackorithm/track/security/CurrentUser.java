package com.trackorithm.track.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUser {
    public UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    public boolean isAdmin(Jwt jwt) {
        Object roles = jwt.getClaim("roles");
        if (roles instanceof Iterable<?> it) {
            for (Object r : it) {
                if ("ADMIN".equals(String.valueOf(r))) {
                    return true;
                }
            }
        }
        return false;
    }
}


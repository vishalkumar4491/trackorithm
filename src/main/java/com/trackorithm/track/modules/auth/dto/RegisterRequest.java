package com.trackorithm.track.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email String email,
        String phoneNumber,
        @NotBlank @Size(min = 8, max = 100) String password,
        String name
) {
}

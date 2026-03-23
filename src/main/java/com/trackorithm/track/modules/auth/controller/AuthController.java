package com.trackorithm.track.modules.auth.controller;

import com.trackorithm.track.modules.auth.dto.AuthResponse;
import com.trackorithm.track.modules.auth.dto.LoginRequest;
import com.trackorithm.track.modules.auth.dto.RegisterRequest;
import com.trackorithm.track.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}


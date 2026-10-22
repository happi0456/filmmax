package com.filmmax.controller;

import com.filmmax.dto.AuthDtos.AuthResponse;
import com.filmmax.dto.AuthDtos.LoginRequest;
import com.filmmax.dto.AuthDtos.RegisterRequest;
import com.filmmax.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    // Protected: needs a valid "Authorization: Bearer <token>" header
    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        String role = auth.getAuthorities().iterator().next()
                .getAuthority().replace("ROLE_", "");
        return Map.of("username", auth.getName(), "role", role);
    }
}
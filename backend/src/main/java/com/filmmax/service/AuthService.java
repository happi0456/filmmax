package com.filmmax.service;

import com.filmmax.dao.UserDao;
import com.filmmax.dto.AuthDtos.AuthResponse;
import com.filmmax.dto.AuthDtos.LoginRequest;
import com.filmmax.dto.AuthDtos.RegisterRequest;
import com.filmmax.model.User;
import com.filmmax.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserDao userDao, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userDao.existsByUsernameOrEmail(req.username(), req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email already exists");
        }
        // New users default to STYLIST. A Director can change roles later.
        userDao.insert(req.username(), req.email(),
                passwordEncoder.encode(req.password()), req.fullName(), "STYLIST");
        return new AuthResponse(jwtService.generateToken(req.username(), "STYLIST"),
                req.username(), "STYLIST");
    }

    public AuthResponse login(LoginRequest req) {
        User user = userDao.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!user.isEnabled() || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new AuthResponse(jwtService.generateToken(user.getUsername(), user.getRole()),
                user.getUsername(), user.getRole());
    }
}
package com.sharvari.auth_service.service;

import com.sharvari.auth_service.dto.AuthResponse;
import com.sharvari.auth_service.dto.LoginRequest;
import com.sharvari.auth_service.dto.RegisterRequest;
import com.sharvari.auth_service.model.User;
import com.sharvari.auth_service.repository.UserRepository;
import com.sharvari.auth_service.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

        User saved = userRepository.save(user);
        log.info("Registered new user: {} with role: {}", saved.getUsername(), saved.getRole());

        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole());
        return new AuthResponse(token, saved.getUsername(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", request.getUsername());
                    return new RuntimeException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - wrong password for user: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        log.info("User logged in: {}", user.getUsername());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}

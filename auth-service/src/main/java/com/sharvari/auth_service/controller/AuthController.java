package com.sharvari.auth_service.controller;

import com.sharvari.auth_service.dto.AuthResponse;
import com.sharvari.auth_service.dto.LoginRequest;
import com.sharvari.auth_service.dto.RegisterRequest;
import com.sharvari.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Service", description = "Handles user registration, login, and JWT issuance")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Register a new user",
            description = "Creates a new user account with a hashed password and returns a signed JWT")
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Login",
            description = "Validates credentials and returns a fresh signed JWT")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}

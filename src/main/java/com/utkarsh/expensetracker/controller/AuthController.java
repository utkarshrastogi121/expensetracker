package com.utkarsh.expensetracker.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utkarsh.expensetracker.service.AuthService;
import com.utkarsh.expensetracker.dto.SignupRequest;
import com.utkarsh.expensetracker.dto.LoginRequest;
import com.utkarsh.expensetracker.dto.AuthResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody SignupRequest request) {
        return new AuthResponse(
                authService.signup(request)
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return new AuthResponse(
                authService.login(request)
        );
    }
}
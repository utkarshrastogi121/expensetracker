package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.dto.AuthResponse;
import com.utkarsh.expensetracker.dto.LoginRequest;
import com.utkarsh.expensetracker.dto.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
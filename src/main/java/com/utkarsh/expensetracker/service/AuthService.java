package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.dto.LoginRequest;
import com.utkarsh.expensetracker.dto.SignupRequest;

public interface AuthService {

    String signup(SignupRequest request);

    String login(LoginRequest request);
}

package com.utkarsh.expensetracker.service.impl;

import com.utkarsh.expensetracker.security.JwtService;
import com.utkarsh.expensetracker.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.utkarsh.expensetracker.exception.GlobalExceptionHandler.BudgetConfigurationException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utkarsh.expensetracker.service.AuthService;
import com.utkarsh.expensetracker.dto.AuthResponse;
import com.utkarsh.expensetracker.dto.UserDTO;
import com.utkarsh.expensetracker.dto.LoginRequest;
import com.utkarsh.expensetracker.dto.SignupRequest;
import com.utkarsh.expensetracker.entity.User;
import com.utkarsh.expensetracker.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BudgetConfigurationException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getEmail());
        UserDTO userDto = mapToUserDTO(savedUser);

        return new AuthResponse(token, userDto);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BudgetConfigurationException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());
        UserDTO userDto = mapToUserDTO(user);

        return new AuthResponse(token, userDto);
    }

    // Helper to map entity to your existing clean UserDTO
    private UserDTO mapToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
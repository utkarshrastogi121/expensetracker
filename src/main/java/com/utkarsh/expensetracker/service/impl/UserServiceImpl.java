package com.utkarsh.expensetracker.service.impl;

import com.utkarsh.expensetracker.dto.UserDTO;
import com.utkarsh.expensetracker.entity.User;
import com.utkarsh.expensetracker.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.utkarsh.expensetracker.repository.UserRepository;
import com.utkarsh.expensetracker.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for email: " + email));

        return mapToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserProfile(String email, User updateDetails) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for email: " + email));

        if (updateDetails.getName() != null && !updateDetails.getName().isBlank()) {
            user.setName(updateDetails.getName());
        }

        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
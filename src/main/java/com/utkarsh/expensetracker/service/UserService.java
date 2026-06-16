package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.dto.UserDTO;
import com.utkarsh.expensetracker.entity.User;

public interface UserService {
    UserDTO getCurrentUserProfile(String email);
    UserDTO updateUserProfile(String email, User updateDetails);
}
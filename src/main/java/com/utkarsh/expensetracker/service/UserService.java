package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.entity.User;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
}
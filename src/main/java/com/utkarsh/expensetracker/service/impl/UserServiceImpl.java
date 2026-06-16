package com.utkarsh.expensetracker.service.impl;

import com.utkarsh.expensetracker.entity.User;
import com.utkarsh.expensetracker.repository.UserRepository;
import com.utkarsh.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @CachePut(value = "user", key = "#result.id")
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
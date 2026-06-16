package com.utkarsh.expensetracker.controller;

import com.utkarsh.expensetracker.dto.UserDTO;
import com.utkarsh.expensetracker.entity.User;
import com.utkarsh.expensetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO userProfile = userService.getCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody User updateDetails
    ) {
        UserDTO updatedProfile = userService.updateUserProfile(userDetails.getUsername(), updateDetails);
        return ResponseEntity.ok(updatedProfile);
    }
}
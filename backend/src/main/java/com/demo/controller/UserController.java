package com.demo.controller;

import com.demo.dto.ApiResponse;
import com.demo.entity.User;
import com.demo.security.CurrentUser;
import com.demo.security.UserPrincipal;
import com.demo.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
        if (user != null) {
            // Remove sensitive information
            user.setPassword(null);
            return ResponseEntity.ok(ApiResponse.success(user));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserProfile(@CurrentUser UserPrincipal currentUser) {
        User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
        if (user != null) {
            // Create a safe profile object without sensitive data
            User profile = new User();
            profile.setId(user.getId());
            profile.setUsername(user.getUsername());
            profile.setEmail(user.getEmail());
            profile.setFirstName(user.getFirstName());
            profile.setLastName(user.getLastName());
            profile.setCreateTime(user.getCreateTime());
            profile.setLastLoginTime(user.getLastLoginTime());
            profile.setEnabled(user.isEnabled());
            
            return ResponseEntity.ok(ApiResponse.success(profile));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUserProfile(@CurrentUser UserPrincipal currentUser, 
                                                @RequestBody User profileUpdate) {
        User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
        if (user != null) {
            // Only update allowed fields
            user.setFirstName(profileUpdate.getFirstName());
            user.setLastName(profileUpdate.getLastName());
            user.setEmail(profileUpdate.getEmail());
            
            User updatedUser = userService.save(user);
            updatedUser.setPassword(null);
            
            return ResponseEntity.ok(ApiResponse.success(updatedUser));
        }
        return ResponseEntity.notFound().build();
    }
}
package com.demo.service.interfaces;

import com.demo.entity.User;

import java.util.Optional;

public interface UserService {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    User save(User user);
    
    void updateLastLoginTime(String userId);
    
    void incrementFailedLoginAttempts(String username);
    
    void resetFailedLoginAttempts(String username);
    
    boolean isAccountLocked(String username);
}
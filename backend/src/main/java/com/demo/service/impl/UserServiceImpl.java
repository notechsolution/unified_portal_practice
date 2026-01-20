package com.demo.service.impl;

import com.demo.entity.User;
import com.demo.repository.UserRepository;
import com.demo.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateLastLoginTime(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Override
    public void incrementFailedLoginAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setAccountNonLocked(false);
                user.setLockoutTime(LocalDateTime.now());
            }
            userRepository.save(user);
        });
    }

    @Override
    public void resetFailedLoginAttempts(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setAccountNonLocked(true);
            user.setLockoutTime(null);
            userRepository.save(user);
        });
    }

    @Override
    public boolean isAccountLocked(String username) {
        return userRepository.findByUsername(username).map(user -> {
            if (!user.isAccountNonLocked() && user.getLockoutTime() != null) {
                LocalDateTime lockoutTime = user.getLockoutTime();
                LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(lockoutTime.plusMinutes(30))) {
                    user.setAccountNonLocked(true);
                    user.setLockoutTime(null);
                    userRepository.save(user);
                    return false;
                }
                return true;
            }
            return false;
        }).orElse(false);
    }
}
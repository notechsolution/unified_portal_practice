package com.demo.controller;

import com.demo.dto.ApiResponse;
import com.demo.dto.LoginRequest;
import com.demo.dto.LoginResponse;
import com.demo.dto.RegisterRequest;
import com.demo.entity.User;
import com.demo.repository.UserRepository;
import com.demo.security.JwtTokenProvider;
import com.demo.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Check if account is locked
        if (userService.isAccountLocked(loginRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(423, "账户已被锁定，由于多次登录失败"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            // Reset failed login attempts on successful login
            userService.resetFailedLoginAttempts(loginRequest.getUsername());
            
            // Update last login time
            User user = userService.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user != null) {
                userService.updateLastLoginTime(user.getId());
            }

            // Create standardized response format
            LoginResponse loginResponse = new LoginResponse(jwt, loginRequest.getUsername(), user.getEmail(), user.getAvatar());
            return ResponseEntity.ok(ApiResponse.success("登录成功", loginResponse));
        } catch (Exception e) {
            // Increment failed login attempts
            userService.incrementFailedLoginAttempts(loginRequest.getUsername());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户名或密码错误"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Validate username
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username is required"));
        }
        
        if (registerRequest.getUsername().length() < 3 || registerRequest.getUsername().length() > 20) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username must be between 3 and 20 characters"));
        }
        
        if (!registerRequest.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username can only contain letters, numbers, and underscores"));
        }

        // Validate email
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is required"));
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!registerRequest.getEmail().matches(emailRegex)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid email format"));
        }

        // Validate password
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Password must be at least 6 characters long"));
        }
        
        if (registerRequest.getPassword().length() > 20) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Password must be no more than 20 characters long"));
        }

        // Check if username already exists
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username is already taken"));
        }

        // Check if email already exists
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email is already in use"));
        }

        try {
            // Create new user
            User user = new User();
            user.setUsername(registerRequest.getUsername().trim());
            user.setEmail(registerRequest.getEmail().trim().toLowerCase());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName() != null ? registerRequest.getFirstName().trim() : "");
            user.setLastName(registerRequest.getLastName() != null ? registerRequest.getLastName().trim() : "");
            user.setEnabled(true);
            user.setAccountNonLocked(true);
            user.setFailedLoginAttempts(0);
            user.setRoles(Set.of("USER")); // Default role for self-registered users

            User result = userService.save(user);

            return ResponseEntity.ok(ApiResponse.success("注册成功", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam String username) {
        boolean available = !userService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        boolean available = !userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a stateless JWT-based authentication system, logout is typically handled
        // on the client side by removing the token. However, we can provide this
        // endpoint for completeness and potential future server-side token blacklisting.
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo() {
        // This endpoint would typically return the current authenticated user's information
        // For now, we'll return a simple message indicating the user is authenticated
        return ResponseEntity.ok(ApiResponse.success("User is authenticated", null));
    }
}
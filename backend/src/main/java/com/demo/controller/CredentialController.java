package com.demo.controller;

import com.demo.dto.ApiResponse;
import com.demo.dto.AutoLoginRequest;
import com.demo.dto.AutoLoginResponse;
import com.demo.entity.UserSystemCredential;
import com.demo.service.interfaces.AutoLoginService;
import com.demo.service.interfaces.SecurityAuditService;
import com.demo.service.interfaces.UserSystemCredentialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    @Autowired
    private UserSystemCredentialService credentialService;
    
    @Autowired
    private AutoLoginService autoLoginService;

    @Autowired
    private SecurityAuditService auditService;

    /**
     * 获取用户的所有系统凭证
     */
    @GetMapping
    public ResponseEntity<?> getUserCredentials(Authentication authentication) {
        String userId = authentication.getName();
        List<UserSystemCredential> credentials = credentialService.findByUserId(userId);
        
        // 记录访问日志
        auditService.logCredentialAccess(userId, null, "VIEW_ALL_CREDENTIALS", null);
        
        // 返回时隐藏敏感信息
        credentials.forEach(credential -> {
            credential.setPassword("***");
        });
        
        return ResponseEntity.ok(ApiResponse.success(credentials));
    }

    /**
     * 获取特定系统的凭证
     */
    @GetMapping("/system/{systemId}")
    public ResponseEntity<?> getSystemCredential(@PathVariable String systemId, Authentication authentication) {
        String userId = authentication.getName();
        Optional<UserSystemCredential> credential = credentialService.findByUserIdAndSystemId(userId, systemId);
        
        if (credential.isPresent()) {
            // 返回时隐藏敏感信息
            credential.get().setPassword("***");
            return ResponseEntity.ok(ApiResponse.success(credential.get()));
        } else {
            return ResponseEntity.ok(ApiResponse.success(null));
        }
    }

    /**
     * 保存或更新系统凭证
     */
    @PostMapping
    public ResponseEntity<?> saveCredential(@Valid @RequestBody UserSystemCredential credential, Authentication authentication) {
        String userId = authentication.getName();
        credential.setUserId(userId);
        
        // 如果已存在该系统的凭证，先删除旧的
        if (credentialService.existsByUserIdAndSystemId(userId, credential.getSystemId())) {
            credentialService.deleteByUserIdAndSystemId(userId, credential.getSystemId());
        }
        
        UserSystemCredential savedCredential = credentialService.saveCredential(credential);
        
        // 记录创建日志
        auditService.logCredentialModification(userId, savedCredential.getId(), "CREATE_CREDENTIAL");
        
        // 返回时隐藏敏感信息
        savedCredential.setUsername("***");
        savedCredential.setPassword("***");
        
        return ResponseEntity.ok(ApiResponse.success("凭证保存成功", savedCredential));
    }

    /**
     * 更新凭证信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCredential(@PathVariable String id, 
                                            @RequestBody Map<String, Object> updates,
                                            Authentication authentication) {
        String userId = authentication.getName();
        
        // 验证凭证属于当前用户
        Optional<UserSystemCredential> existingCredential = credentialService.findById(id);
        if (!existingCredential.isPresent() || !existingCredential.get().getUserId().equals(userId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("无权更新此凭证"));
        }
        
        String username = (String) updates.get("username");
        String password = (String) updates.get("password");
        String description = (String) updates.get("description");
        Boolean enabled = updates.get("enabled") != null ? (Boolean) updates.get("enabled") : null;
        String loginUrl = (String) updates.get("loginUrl");
        
        UserSystemCredential updatedCredential = credentialService.updateCredential(id, username, password, description, enabled, loginUrl);
        
        if (updatedCredential != null) {
            // 记录更新日志
            auditService.logCredentialModification(userId, id, "UPDATE_CREDENTIAL");
            
            // 返回时隐藏敏感信息
            updatedCredential.setUsername("***");
            updatedCredential.setPassword("***");
            return ResponseEntity.ok(ApiResponse.success("凭证更新成功", updatedCredential));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新失败"));
        }
    }

    /**
     * 删除凭证
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCredential(@PathVariable String id, Authentication authentication) {
        String userId = authentication.getName();
        
        // 验证凭证属于当前用户
        Optional<UserSystemCredential> existingCredential = credentialService.findById(id);
        if (!existingCredential.isPresent() || !existingCredential.get().getUserId().equals(userId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("无权删除此凭证"));
        }
        
        credentialService.deleteById(id);
        
        // 记录删除日志
        auditService.logCredentialModification(userId, id, "DELETE_CREDENTIAL");
        
        return ResponseEntity.ok(ApiResponse.success("凭证删除成功"));
    }

    /**
     * 检查特定系统的凭证是否存在
     */
    @GetMapping("/check/{systemId}")
    public ResponseEntity<?> checkCredentialExists(@PathVariable String systemId, Authentication authentication) {
        String userId = authentication.getName();
        boolean exists = credentialService.existsByUserIdAndSystemId(userId, systemId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    /**
     * 执行自动登录
     */
    @PostMapping("/auto-login")
    public ResponseEntity<?> performAutoLogin(@Valid @RequestBody AutoLoginRequest request, Authentication authentication) {
        String userId = authentication.getName();
        
        // 检查凭证是否存在
        if (!credentialService.existsByUserIdAndSystemId(userId, request.getSystemId())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("未找到该系统的登录凭证"));
        }
        
        // 执行自动登录
        AutoLoginResponse response = autoLoginService.performAutoLogin(userId, request.getSystemId());
        
        if (response.isSuccess()) {
            // 记录成功的自动登录
            auditService.logAutoLoginAttempt(userId, request.getSystemId(), true, "自动登录成功");
            return ResponseEntity.ok(ApiResponse.success("自动登录成功", response));
        } else {
            // 记录失败的自动登录
            auditService.logAutoLoginAttempt(userId, request.getSystemId(), false, response.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
        }
    }

    /**
     * 获取用户的凭证统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getCredentialStatistics(Authentication authentication) {
        String userId = authentication.getName();
        long totalCount = credentialService.findByUserId(userId).size();
        long enabledCount = credentialService.findByUserId(userId).stream()
            .filter(UserSystemCredential::isEnabled)
            .count();
        
        Map<String, Object> statistics = Map.of(
            "totalCount", totalCount,
            "enabledCount", enabledCount,
            "disabledCount", totalCount - enabledCount
        );
        
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    /**
     * 测试凭证是否有效
     */
    @PostMapping("/{id}/test")
    public ResponseEntity<?> testCredential(@PathVariable String id, Authentication authentication) {
        String userId = authentication.getName();
        
        // 验证凭证属于当前用户
        Optional<UserSystemCredential> existingCredential = credentialService.findById(id);
        if (!existingCredential.isPresent() || !existingCredential.get().getUserId().equals(userId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("无权测试此凭证"));
        }
        
        // 执行测试登录
        AutoLoginResponse response = autoLoginService.performAutoLogin(userId, existingCredential.get().getSystemId());
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success("凭证测试成功", response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("凭证测试失败: " + response.getMessage()));
        }
    }
}
package com.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "user_system_credentials")
public class UserSystemCredential {
    
    @Id
    private String id;
    private String userId;
    private String systemId;
    private String systemName;
    private String username; // 加密存储
    private String password; // 加密存储
    private String loginUrl;
    private String systemType = "WEB_FORM"; // 仅支持WEB类型
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsedAt;
    private int autoLoginCount;
    private String description;
    private String formFieldMapping; // JSON格式的表单字段映射
    private String usernameXPath; // 用户名输入框XPath
    private String passwordXPath; // 密码输入框XPath
    private String submitButtonXPath; // 提交按钮XPath
    private String loginFormXPath; // 登录表单XPath
    
    public UserSystemCredential() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
        this.autoLoginCount = 0;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public String getSystemName() {
        return systemName;
    }
    
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getLoginUrl() {
        return loginUrl;
    }
    
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
    
    public String getSystemType() {
        return systemType;
    }
    
    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }
    
    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
    
    public int getAutoLoginCount() {
        return autoLoginCount;
    }
    
    public void setAutoLoginCount(int autoLoginCount) {
        this.autoLoginCount = autoLoginCount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFormFieldMapping() {
        return formFieldMapping;
    }
    
    public void setFormFieldMapping(String formFieldMapping) {
        this.formFieldMapping = formFieldMapping;
    }
    
    public String getUsernameXPath() {
        return usernameXPath;
    }
    
    public void setUsernameXPath(String usernameXPath) {
        this.usernameXPath = usernameXPath;
    }
    
    public String getPasswordXPath() {
        return passwordXPath;
    }
    
    public void setPasswordXPath(String passwordXPath) {
        this.passwordXPath = passwordXPath;
    }
    
    public String getSubmitButtonXPath() {
        return submitButtonXPath;
    }
    
    public void setSubmitButtonXPath(String submitButtonXPath) {
        this.submitButtonXPath = submitButtonXPath;
    }
    
    public String getLoginFormXPath() {
        return loginFormXPath;
    }
    
    public void setLoginFormXPath(String loginFormXPath) {
        this.loginFormXPath = loginFormXPath;
    }
}
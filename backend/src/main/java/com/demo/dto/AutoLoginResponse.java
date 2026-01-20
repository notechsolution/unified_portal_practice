package com.demo.dto;

public class AutoLoginResponse {
    
    private boolean success;
    private String message;
    private String redirectUrl;
    private String token;
    private Object additionalData;
    
    public AutoLoginResponse() {}
    
    public AutoLoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public AutoLoginResponse(boolean success, String message, String redirectUrl) {
        this.success = success;
        this.message = message;
        this.redirectUrl = redirectUrl;
    }
    
    public static AutoLoginResponse success(String redirectUrl, String message) {
        return new AutoLoginResponse(true, message, redirectUrl);
    }
    
    public static AutoLoginResponse success(String message) {
        return new AutoLoginResponse(true, message);
    }
    
    public static AutoLoginResponse error(String message) {
        return new AutoLoginResponse(false, message);
    }
    
    public static AutoLoginResponse successWithXPath(String loginUrl, String username, String password, 
            String usernameXPath, String passwordXPath, String submitButtonXPath, String loginFormXPath) {
        AutoLoginResponse response = new AutoLoginResponse(true, "准备前端JavaScript注入");
        response.setRedirectUrl(loginUrl);
        
        // 创建XPath配置对象
        XPathConfig xpathConfig = new XPathConfig(
            loginUrl, username, password, usernameXPath, passwordXPath, submitButtonXPath, loginFormXPath
        );
        response.setAdditionalData(xpathConfig);
        
        return response;
    }
    
    // XPath配置内部类
    public static class XPathConfig {
        private String loginUrl;
        private String username;
        private String password;
        private String usernameXPath;
        private String passwordXPath;
        private String submitButtonXPath;
        private String loginFormXPath;
        
        public XPathConfig(String loginUrl, String username, String password, String usernameXPath, 
                String passwordXPath, String submitButtonXPath, String loginFormXPath) {
            this.loginUrl = loginUrl;
            this.username = username;
            this.password = password;
            this.usernameXPath = usernameXPath;
            this.passwordXPath = passwordXPath;
            this.submitButtonXPath = submitButtonXPath;
            this.loginFormXPath = loginFormXPath;
        }
        
        // Getters
        public String getLoginUrl() { return loginUrl; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getUsernameXPath() { return usernameXPath; }
        public String getPasswordXPath() { return passwordXPath; }
        public String getSubmitButtonXPath() { return submitButtonXPath; }
        public String getLoginFormXPath() { return loginFormXPath; }
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Object getAdditionalData() {
        return additionalData;
    }
    
    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }
}
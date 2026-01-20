package com.demo.dto;

public class AutoLoginRequest {
    private String systemId;
    private String redirectUrl; // 可选的重定向URL
    
    public AutoLoginRequest() {}
    
    public AutoLoginRequest(String systemId) {
        this.systemId = systemId;
    }
    
    public AutoLoginRequest(String systemId, String redirectUrl) {
        this.systemId = systemId;
        this.redirectUrl = redirectUrl;
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
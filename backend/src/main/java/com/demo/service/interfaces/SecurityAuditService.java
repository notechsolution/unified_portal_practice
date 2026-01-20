package com.demo.service.interfaces;

public interface SecurityAuditService {
    void logCredentialAccess(String userId, String credentialId, String action, String systemId);
    void logAutoLoginAttempt(String userId, String systemId, boolean success, String message);
    void logCredentialModification(String userId, String credentialId, String action);
}
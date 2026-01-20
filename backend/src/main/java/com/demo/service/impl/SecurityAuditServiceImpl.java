package com.demo.service.impl;

import com.demo.entity.SecurityAuditLog;
import com.demo.repository.SecurityAuditLogRepository;
import com.demo.service.interfaces.SecurityAuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
public class SecurityAuditServiceImpl implements SecurityAuditService {

    @Autowired
    private SecurityAuditLogRepository auditLogRepository;

    @Override
    public void logCredentialAccess(String userId, String credentialId, String action, String systemId) {
        SecurityAuditLog auditLog = new SecurityAuditLog();
        auditLog.setUserId(userId);
        auditLog.setCredentialId(credentialId);
        auditLog.setAction(action);
        auditLog.setSystemId(systemId);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setIpAddress(getCurrentIpAddress());
        auditLog.setUserAgent(getCurrentUserAgent());
        
        auditLogRepository.save(auditLog);
    }

    @Override
    public void logAutoLoginAttempt(String userId, String systemId, boolean success, String message) {
        SecurityAuditLog auditLog = new SecurityAuditLog();
        auditLog.setUserId(userId);
        auditLog.setSystemId(systemId);
        auditLog.setAction(success ? "AUTO_LOGIN_SUCCESS" : "AUTO_LOGIN_FAILED");
        auditLog.setSuccess(success);
        auditLog.setMessage(message);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setIpAddress(getCurrentIpAddress());
        auditLog.setUserAgent(getCurrentUserAgent());
        
        auditLogRepository.save(auditLog);
    }

    @Override
    public void logCredentialModification(String userId, String credentialId, String action) {
        SecurityAuditLog auditLog = new SecurityAuditLog();
        auditLog.setUserId(userId);
        auditLog.setCredentialId(credentialId);
        auditLog.setAction(action);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setIpAddress(getCurrentIpAddress());
        auditLog.setUserAgent(getCurrentUserAgent());
        
        auditLogRepository.save(auditLog);
    }

    private String getCurrentIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // 忽略异常，返回默认值
        }
        return "0.0.0.0";
    }

    private String getCurrentUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            // 忽略异常，返回默认值
        }
        return "Unknown";
    }
}
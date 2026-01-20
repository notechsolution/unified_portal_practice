package com.demo.service.impl;

import com.demo.dto.AutoLoginRequest;
import com.demo.dto.AutoLoginResponse;
import com.demo.entity.UserSystemCredential;
import com.demo.service.interfaces.AutoLoginService;
import com.demo.service.interfaces.CredentialEncryptionService;
import com.demo.service.interfaces.SecurityAuditService;
import com.demo.service.interfaces.UserSystemCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutoLoginServiceImpl implements AutoLoginService {

    @Autowired
    private UserSystemCredentialService credentialService;
    
    @Autowired
    private SecurityAuditService auditService;

    @Override
    public AutoLoginResponse performAutoLogin(String userId, String systemId) {
        try {
            // 1. 获取用户凭证
            Optional<UserSystemCredential> credentialOpt = 
                credentialService.findByUserIdAndSystemId(userId, systemId);
            
            if (!credentialOpt.isPresent()) {
                auditService.logAutoLoginAttempt(userId, systemId, false, "未找到该系统的登录凭证");
                return AutoLoginResponse.error("未找到该系统的登录凭证");
            }
            
            UserSystemCredential credential = credentialOpt.get();
            if (!credential.isEnabled()) {
                auditService.logAutoLoginAttempt(userId, systemId, false, "该系统凭证已禁用");
                return AutoLoginResponse.error("该系统凭证已禁用");
            }
            
            // 2. 验证是否为WEB类型
            if (!"WEB_FORM".equals(credential.getSystemType())) {
                auditService.logAutoLoginAttempt(userId, systemId, false, "不支持的系统类型");
                return AutoLoginResponse.error("不支持的系统类型");
            }
            
            // 3. 验证XPath配置是否完整
            if (credential.getUsernameXPath() == null || credential.getPasswordXPath() == null || 
                credential.getSubmitButtonXPath() == null) {
                auditService.logAutoLoginAttempt(userId, systemId, false, "XPath配置不完整");
                return AutoLoginResponse.error("XPath配置不完整，请先配置登录元素的XPath");
            }
            
            // 4. 更新最后使用时间
            credentialService.updateLastUsed(credential.getId());
            
            // 5. 返回前端JavaScript注入所需的信息
            auditService.logAutoLoginAttempt(userId, systemId, true, "准备前端JavaScript注入");
            
            return AutoLoginResponse.successWithXPath(
                credential.getLoginUrl(),
                credential.getUsername(),
                credential.getPassword(),
                credential.getUsernameXPath(),
                credential.getPasswordXPath(),
                credential.getSubmitButtonXPath(),
                credential.getLoginFormXPath()
            );
            
        } catch (Exception e) {
            auditService.logAutoLoginAttempt(userId, systemId, false, "自动登录失败: " + e.getMessage());
            return AutoLoginResponse.error("自动登录失败: " + e.getMessage());
        }
    }
}
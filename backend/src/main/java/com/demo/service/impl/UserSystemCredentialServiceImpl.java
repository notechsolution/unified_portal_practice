package com.demo.service.impl;

import com.demo.entity.UserSystemCredential;
import com.demo.repository.UserSystemCredentialRepository;
import com.demo.service.interfaces.CredentialEncryptionService;
import com.demo.service.interfaces.UserSystemCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserSystemCredentialServiceImpl implements UserSystemCredentialService {

    @Autowired
    private UserSystemCredentialRepository credentialRepository;
    
    @Autowired
    private CredentialEncryptionService encryptionService;

    @Override
    public UserSystemCredential saveCredential(UserSystemCredential credential) {
        // 加密敏感信息
        credential.setUsername(credential.getUsername());
        credential.setPassword(encryptionService.encrypt(credential.getPassword()));
        credential.setUpdatedAt(LocalDateTime.now());
        
        return credentialRepository.save(credential);
    }

    @Override
    public Optional<UserSystemCredential> findById(String id) {
        Optional<UserSystemCredential> credential = credentialRepository.findById(id);
        credential.ifPresent(this::decryptCredential);
        return credential;
    }

    @Override
    public List<UserSystemCredential> findByUserId(String userId) {
        List<UserSystemCredential> credentials = credentialRepository.findByUserId(userId);
        credentials.forEach(this::decryptCredential);
        return credentials;
    }

    @Override
    public Optional<UserSystemCredential> findByUserIdAndSystemId(String userId, String systemId) {
        Optional<UserSystemCredential> credential = credentialRepository.findByUserIdAndSystemId(userId, systemId);
        credential.ifPresent(this::decryptCredential);
        return credential;
    }

    @Override
    public boolean existsByUserIdAndSystemId(String userId, String systemId) {
        return credentialRepository.existsByUserIdAndSystemId(userId, systemId);
    }

    @Override
    public void deleteById(String id) {
        credentialRepository.deleteById(id);
    }

    @Override
    public void deleteByUserIdAndSystemId(String userId, String systemId) {
        credentialRepository.deleteByUserIdAndSystemId(userId, systemId);
    }

    @Override
    public UserSystemCredential updateLastUsed(String id) {
        Optional<UserSystemCredential> credentialOpt = credentialRepository.findById(id);
        if (credentialOpt.isPresent()) {
            UserSystemCredential credential = credentialOpt.get();
            credential.setLastUsedAt(LocalDateTime.now());
            credential.setAutoLoginCount(credential.getAutoLoginCount() + 1);
            return credentialRepository.save(credential);
        }
        return null;
    }

    @Override
    public UserSystemCredential updateCredential(String id, String username, String password, String description, Boolean enabled, String loginUrl) {
        Optional<UserSystemCredential> credentialOpt = credentialRepository.findById(id);
        if (credentialOpt.isPresent()) {
            UserSystemCredential credential = credentialOpt.get();
            if (username != null) {
                credential.setUsername(username);
            }
            if (password != null) {
                credential.setPassword(encryptionService.encrypt(password));
            }
            if (description != null) {
                credential.setDescription(description);
            }
            if (enabled != null) {
                credential.setEnabled(enabled);
            }
            if (loginUrl != null) {
                credential.setLoginUrl(loginUrl);
            }
            credential.setUpdatedAt(LocalDateTime.now());
            return credentialRepository.save(credential);
        }
        return null;
    }

    private void decryptCredential(UserSystemCredential credential) {
        credential.setUsername(credential.getUsername());
        credential.setPassword(encryptionService.decrypt(credential.getPassword()));
    }
}
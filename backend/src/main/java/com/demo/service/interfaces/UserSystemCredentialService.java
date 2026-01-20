package com.demo.service.interfaces;

import com.demo.entity.UserSystemCredential;
import java.util.List;
import java.util.Optional;

public interface UserSystemCredentialService {
    UserSystemCredential saveCredential(UserSystemCredential credential);
    Optional<UserSystemCredential> findById(String id);
    List<UserSystemCredential> findByUserId(String userId);
    Optional<UserSystemCredential> findByUserIdAndSystemId(String userId, String systemId);
    boolean existsByUserIdAndSystemId(String userId, String systemId);
    void deleteById(String id);
    void deleteByUserIdAndSystemId(String userId, String systemId);
    UserSystemCredential updateLastUsed(String id);
    UserSystemCredential updateCredential(String id, String username, String password, String description, Boolean enabled, String loginUrl);
}
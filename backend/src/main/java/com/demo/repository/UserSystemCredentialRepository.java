package com.demo.repository;

import com.demo.entity.UserSystemCredential;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSystemCredentialRepository extends MongoRepository<UserSystemCredential, String> {
    
    List<UserSystemCredential> findByUserId(String userId);
    
    Optional<UserSystemCredential> findByUserIdAndSystemId(String userId, String systemId);
    
    boolean existsByUserIdAndSystemId(String userId, String systemId);
    
    void deleteByUserIdAndSystemId(String userId, String systemId);
    
    List<UserSystemCredential> findByUserIdAndEnabled(String userId, boolean enabled);
    
    long countByUserId(String userId);
    
    long countByUserIdAndEnabled(String userId, boolean enabled);
}
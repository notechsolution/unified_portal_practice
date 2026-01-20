package com.demo.repository;

import com.demo.entity.SecurityAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SecurityAuditLogRepository extends MongoRepository<SecurityAuditLog, String> {
    List<SecurityAuditLog> findByUserIdOrderByTimestampDesc(String userId);
    List<SecurityAuditLog> findByUserIdAndActionOrderByTimestampDesc(String userId, String action);
    List<SecurityAuditLog> findByUserIdAndTimestampBetweenOrderByTimestampDesc(String userId, LocalDateTime start, LocalDateTime end);
    long countByUserIdAndActionAndTimestampBetween(String userId, String action, LocalDateTime start, LocalDateTime end);
}
package com.demo.controller;

import com.demo.dto.ApiResponse;
import com.demo.entity.SecurityAuditLog;
import com.demo.repository.SecurityAuditLogRepository;
import com.demo.service.interfaces.SecurityAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class SecurityAuditController {

    @Autowired
    private SecurityAuditLogRepository auditLogRepository;
    
    @Autowired
    private SecurityAuditService auditService;

    /**
     * 获取用户的审计日志
     */
    @GetMapping("/my-logs")
    public ResponseEntity<?> getMyAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            Authentication authentication) {
        
        String userId = authentication.getName();
        List<SecurityAuditLog> logs;
        
        if (action != null && startDate != null && endDate != null) {
            logs = auditLogRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                userId, startDate, endDate);
            // 过滤指定action的日志
            logs = logs.stream()
                .filter(log -> action.equals(log.getAction()))
                .toList();
        } else if (action != null) {
            logs = auditLogRepository.findByUserIdAndActionOrderByTimestampDesc(userId, action);
        } else {
            logs = auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * 获取用户的登录统计
     */
    @GetMapping("/login-statistics")
    public ResponseEntity<?> getLoginStatistics(
            @RequestParam(defaultValue = "30") int days,
            Authentication authentication) {
        
        String userId = authentication.getName();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        long successCount = auditLogRepository.countByUserIdAndActionAndTimestampBetween(
            userId, "AUTO_LOGIN_SUCCESS", startDate, endDate);
        
        long failedCount = auditLogRepository.countByUserIdAndActionAndTimestampBetween(
            userId, "AUTO_LOGIN_FAILED", startDate, endDate);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("successCount", successCount);
        statistics.put("failedCount", failedCount);
        statistics.put("totalCount", successCount + failedCount);
        statistics.put("successRate", successCount > 0 ? (double) successCount / (successCount + failedCount) * 100 : 0);
        statistics.put("periodDays", days);
        
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    /**
     * 获取最近的自动登录活动
     */
    @GetMapping("/recent-activity")
    public ResponseEntity<?> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        
        String userId = authentication.getName();
        List<SecurityAuditLog> logs = auditLogRepository.findByUserIdAndActionOrderByTimestampDesc(
            userId, "AUTO_LOGIN_SUCCESS");
        
        // 只返回最近的limit条记录
        if (logs.size() > limit) {
            logs = logs.subList(0, limit);
        }
        
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
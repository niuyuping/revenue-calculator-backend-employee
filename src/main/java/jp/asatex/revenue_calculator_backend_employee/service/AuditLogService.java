package jp.asatex.revenue_calculator_backend_employee.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志服务
 * 提供结构化的审计日志记录功能
 */
@Service
public class AuditLogService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 记录用户操作审计日志
     * @param operation 操作类型
     * @param resource 资源类型
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @param details 详细信息
     */
    public void logUserOperation(String operation, String resource, String resourceId, 
                                String userId, Map<String, Object> details) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("timestamp", Instant.now().toString());
            auditData.put("operation", operation);
            auditData.put("resource", resource);
            auditData.put("resourceId", resourceId);
            auditData.put("userId", userId);
            auditData.put("sessionId", MDC.get("sessionId"));
            auditData.put("requestId", MDC.get("requestId"));
            auditData.put("ipAddress", MDC.get("ipAddress"));
            auditData.put("userAgent", MDC.get("userAgent"));
            auditData.put("details", details);

            String auditJson = objectMapper.writeValueAsString(auditData);
            auditLogger.info("用户操作审计: {}", auditJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("审计日志序列化失败", e);
        }
    }

    /**
     * 记录数据访问审计日志
     * @param operation 操作类型 (CREATE, READ, UPDATE, DELETE)
     * @param table 表名
     * @param recordId 记录ID
     * @param userId 用户ID
     * @param oldValues 旧值
     * @param newValues 新值
     */
    public void logDataAccess(String operation, String table, String recordId, 
                             String userId, Map<String, Object> oldValues, Map<String, Object> newValues) {
        try {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("timestamp", Instant.now().toString());
            auditData.put("operation", operation);
            auditData.put("table", table);
            auditData.put("recordId", recordId);
            auditData.put("userId", userId);
            auditData.put("sessionId", MDC.get("sessionId"));
            auditData.put("requestId", MDC.get("requestId"));
            auditData.put("ipAddress", MDC.get("ipAddress"));
            auditData.put("oldValues", oldValues);
            auditData.put("newValues", newValues);

            String auditJson = objectMapper.writeValueAsString(auditData);
            auditLogger.info("数据访问审计: {}", auditJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("数据访问审计日志序列化失败", e);
        }
    }

    /**
     * 记录安全事件日志
     * @param eventType 事件类型
     * @param severity 严重程度 (LOW, MEDIUM, HIGH, CRITICAL)
     * @param description 描述
     * @param userId 用户ID
     * @param details 详细信息
     */
    public void logSecurityEvent(String eventType, String severity, String description, 
                                String userId, Map<String, Object> details) {
        try {
            Map<String, Object> securityData = new HashMap<>();
            securityData.put("timestamp", Instant.now().toString());
            securityData.put("eventType", eventType);
            securityData.put("severity", severity);
            securityData.put("description", description);
            securityData.put("userId", userId);
            securityData.put("sessionId", MDC.get("sessionId"));
            securityData.put("requestId", MDC.get("requestId"));
            securityData.put("ipAddress", MDC.get("ipAddress"));
            securityData.put("userAgent", MDC.get("userAgent"));
            securityData.put("details", details);

            String securityJson = objectMapper.writeValueAsString(securityData);
            securityLogger.warn("安全事件: {}", securityJson);
        } catch (JsonProcessingException e) {
            securityLogger.error("安全日志序列化失败", e);
        }
    }

    /**
     * 记录性能日志
     * @param operation 操作名称
     * @param duration 执行时间（毫秒）
     * @param status 状态 (SUCCESS, FAILURE, TIMEOUT)
     * @param details 详细信息
     */
    public void logPerformance(String operation, long duration, String status, Map<String, Object> details) {
        try {
            Map<String, Object> performanceData = new HashMap<>();
            performanceData.put("timestamp", Instant.now().toString());
            performanceData.put("operation", operation);
            performanceData.put("duration", duration);
            performanceData.put("status", status);
            performanceData.put("sessionId", MDC.get("sessionId"));
            performanceData.put("requestId", MDC.get("requestId"));
            performanceData.put("details", details);

            String performanceJson = objectMapper.writeValueAsString(performanceData);
            performanceLogger.info("性能监控: {}", performanceJson);
        } catch (JsonProcessingException e) {
            performanceLogger.error("性能日志序列化失败", e);
        }
    }

    /**
     * 记录API调用审计日志
     * @param method HTTP方法
     * @param uri 请求URI
     * @param statusCode 响应状态码
     * @param duration 执行时间（毫秒）
     * @param userId 用户ID
     * @param requestSize 请求大小
     * @param responseSize 响应大小
     */
    public void logApiCall(String method, String uri, int statusCode, long duration, 
                          String userId, long requestSize, long responseSize) {
        try {
            Map<String, Object> apiData = new HashMap<>();
            apiData.put("timestamp", Instant.now().toString());
            apiData.put("method", method);
            apiData.put("uri", uri);
            apiData.put("statusCode", statusCode);
            apiData.put("duration", duration);
            apiData.put("userId", userId);
            apiData.put("requestSize", requestSize);
            apiData.put("responseSize", responseSize);
            apiData.put("sessionId", MDC.get("sessionId"));
            apiData.put("requestId", MDC.get("requestId"));
            apiData.put("ipAddress", MDC.get("ipAddress"));
            apiData.put("userAgent", MDC.get("userAgent"));

            String apiJson = objectMapper.writeValueAsString(apiData);
            auditLogger.info("API调用审计: {}", apiJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("API调用审计日志序列化失败", e);
        }
    }

    /**
     * 记录业务操作审计日志
     * @param businessOperation 业务操作
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param userId 用户ID
     * @param result 操作结果
     * @param details 详细信息
     */
    public void logBusinessOperation(String businessOperation, String entityType, String entityId, 
                                   String userId, String result, Map<String, Object> details) {
        try {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("timestamp", Instant.now().toString());
            businessData.put("businessOperation", businessOperation);
            businessData.put("entityType", entityType);
            businessData.put("entityId", entityId);
            businessData.put("userId", userId);
            businessData.put("result", result);
            businessData.put("sessionId", MDC.get("sessionId"));
            businessData.put("requestId", MDC.get("requestId"));
            businessData.put("ipAddress", MDC.get("ipAddress"));
            businessData.put("details", details);

            String businessJson = objectMapper.writeValueAsString(businessData);
            auditLogger.info("业务操作审计: {}", businessJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("业务操作审计日志序列化失败", e);
        }
    }

    /**
     * 记录系统事件日志
     * @param eventType 事件类型
     * @param component 组件名称
     * @param description 描述
     * @param details 详细信息
     */
    public void logSystemEvent(String eventType, String component, String description, 
                              Map<String, Object> details) {
        try {
            Map<String, Object> systemData = new HashMap<>();
            systemData.put("timestamp", Instant.now().toString());
            systemData.put("eventType", eventType);
            systemData.put("component", component);
            systemData.put("description", description);
            systemData.put("sessionId", MDC.get("sessionId"));
            systemData.put("requestId", MDC.get("requestId"));
            systemData.put("details", details);

            String systemJson = objectMapper.writeValueAsString(systemData);
            auditLogger.info("系统事件: {}", systemJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("系统事件日志序列化失败", e);
        }
    }

    /**
     * 记录错误审计日志
     * @param errorType 错误类型
     * @param component 组件名称
     * @param errorMessage 错误消息
     * @param userId 用户ID
     * @param details 详细信息
     */
    public void logError(String errorType, String component, String errorMessage, 
                        String userId, Map<String, Object> details) {
        try {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("timestamp", Instant.now().toString());
            errorData.put("errorType", errorType);
            errorData.put("component", component);
            errorData.put("errorMessage", errorMessage);
            errorData.put("userId", userId);
            errorData.put("sessionId", MDC.get("sessionId"));
            errorData.put("requestId", MDC.get("requestId"));
            errorData.put("ipAddress", MDC.get("ipAddress"));
            errorData.put("details", details);

            String errorJson = objectMapper.writeValueAsString(errorData);
            auditLogger.error("错误审计: {}", errorJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("错误审计日志序列化失败", e);
        }
    }
}

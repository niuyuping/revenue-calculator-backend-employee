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
 * Audit log service
 * Provides structured audit logging functionality
 */
@Service
public class AuditLogService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Log user operation audit
     * @param operation Operation type
     * @param resource Resource type
     * @param resourceId Resource ID
     * @param userId User ID
     * @param details Details
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
            auditLogger.info("User operation audit: {}", auditJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("Audit log serialization failed", e);
        }
    }

    /**
     * Log data access audit
     * @param operation Operation type (CREATE, READ, UPDATE, DELETE)
     * @param table Table name
     * @param recordId Record ID
     * @param userId User ID
     * @param oldValues Old values
     * @param newValues New values
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
            auditLogger.info("Data access audit: {}", auditJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("Data access audit log serialization failed", e);
        }
    }

    /**
     * Log security event
     * @param eventType Event type
     * @param severity Severity (LOW, MEDIUM, HIGH, CRITICAL)
     * @param description Description
     * @param userId User ID
     * @param details Details
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
            securityLogger.warn("Security event: {}", securityJson);
        } catch (JsonProcessingException e) {
            securityLogger.error("Security log serialization failed", e);
        }
    }

    /**
     * Log performance
     * @param operation Operation name
     * @param duration Execution time (milliseconds)
     * @param status Status (SUCCESS, FAILURE, TIMEOUT)
     * @param details Details
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
            performanceLogger.info("Performance monitoring: {}", performanceJson);
        } catch (JsonProcessingException e) {
            performanceLogger.error("Performance log serialization failed", e);
        }
    }

    /**
     * Log API call audit
     * @param method HTTP method
     * @param uri Request URI
     * @param statusCode Response status code
     * @param duration Execution time (milliseconds)
     * @param userId User ID
     * @param requestSize Request size
     * @param responseSize Response size
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
            auditLogger.info("API call audit: {}", apiJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("API call audit log serialization failed", e);
        }
    }

    /**
     * Log business operation audit
     * @param businessOperation Business operation
     * @param entityType Entity type
     * @param entityId Entity ID
     * @param userId User ID
     * @param result Operation result
     * @param details Details
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
            auditLogger.info("Business operation audit: {}", businessJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("Business operation audit log serialization failed", e);
        }
    }

    /**
     * Log system event
     * @param eventType Event type
     * @param component Component name
     * @param description Description
     * @param details Details
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
            auditLogger.info("System event: {}", systemJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("System event log serialization failed", e);
        }
    }

    /**
     * Log error audit
     * @param errorType Error type
     * @param component Component name
     * @param errorMessage Error message
     * @param userId User ID
     * @param details Details
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
            auditLogger.error("Error audit: {}", errorJson);
        } catch (JsonProcessingException e) {
            auditLogger.error("Error audit log serialization failed", e);
        }
    }
}

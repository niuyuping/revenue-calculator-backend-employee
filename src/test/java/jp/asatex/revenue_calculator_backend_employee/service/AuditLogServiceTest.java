package jp.asatex.revenue_calculator_backend_employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuditLogService test class
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogService Test")
class AuditLogServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        // Set MDC
        MDC.put("sessionId", "test-session-123");
        MDC.put("requestId", "test-request-456");
        MDC.put("ipAddress", "192.168.1.100");
        MDC.put("userAgent", "Test-Agent/1.0");
    }

    @Test
    @DisplayName("Logging user operation audit should work correctly")
    void testLogUserOperation() throws Exception {
        // Given
        String operation = "CREATE";
        String resource = "Employee";
        String resourceId = "123";
        String userId = "user123";
        Map<String, Object> details = new HashMap<>();
        details.put("name", "Test Employee");
        details.put("employeeNumber", "EMP001");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"operation\":\"CREATE\"}");

        // When
        auditLogService.logUserOperation(operation, resource, resourceId, userId, details);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("Logging data access audit should work correctly")
    void testLogDataAccess() throws Exception {
        // Given
        String operation = "UPDATE";
        String table = "employees";
        String recordId = "123";
        String userId = "user123";
        Map<String, Object> oldValues = new HashMap<>();
        oldValues.put("name", "Old Name");
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("name", "New Name");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"operation\":\"UPDATE\"}");

        // When
        auditLogService.logDataAccess(operation, table, recordId, userId, oldValues, newValues);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("Recording security event logs should work correctly")
    void testLogSecurityEvent() throws Exception {
        // Given
        String eventType = "LOGIN_FAILED";
        String severity = "HIGH";
        String description = "Too many login failures";
        String userId = "user123";
        Map<String, Object> details = new HashMap<>();
        details.put("attempts", 5);
        details.put("ipAddress", "192.168.1.100");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"eventType\":\"LOGIN_FAILED\"}");

        // When
        auditLogService.logSecurityEvent(eventType, severity, description, userId, details);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("Recording performance logs should work correctly")
    void testLogPerformance() throws Exception {
        // Given
        String operation = "CREATE_EMPLOYEE";
        long duration = 150;
        String status = "SUCCESS";
        Map<String, Object> details = new HashMap<>();
        details.put("employeeId", "123");
        details.put("processingTime", duration);

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"operation\":\"CREATE_EMPLOYEE\"}");

        // When
        auditLogService.logPerformance(operation, duration, status, details);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("Recording API call audit logs should work correctly")
    void testLogApiCall() throws Exception {
        // Given
        String method = "POST";
        String uri = "/api/v1/employee";
        int statusCode = 201;
        long duration = 200;
        String userId = "user123";
        long requestSize = 1024;
        long responseSize = 512;

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"method\":\"POST\"}");

        // When
        auditLogService.logApiCall(method, uri, statusCode, duration, userId, requestSize, responseSize);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("Recording business operation audit logs should work correctly")
    void testLogBusinessOperation() throws Exception {
        // Given
        String businessOperation = "CREATE_EMPLOYEE";
        String entityType = "Employee";
        String entityId = "123";
        String userId = "user123";
        String result = "SUCCESS";
        Map<String, Object> details = new HashMap<>();
        details.put("employeeNumber", "EMP001");
        details.put("name", "Test Employee");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"businessOperation\":\"CREATE_EMPLOYEE\"}");

        // When
        auditLogService.logBusinessOperation(businessOperation, entityType, entityId, userId, result, details);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("Recording system event logs should work correctly")
    void testLogSystemEvent() throws Exception {
        // Given
        String eventType = "STARTUP";
        String component = "Application";
        String description = "Application startup completed";
        Map<String, Object> details = new HashMap<>();
        details.put("version", "1.0.0");
        details.put("startupTime", "2024-01-01T10:00:00Z");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"eventType\":\"STARTUP\"}");

        // When
        auditLogService.logSystemEvent(eventType, component, description, details);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("Recording error audit logs should work correctly")
    void testLogError() throws Exception {
        // Given
        String errorType = "VALIDATION_ERROR";
        String component = "EmployeeService";
        String errorMessage = "Employee number format is incorrect";
        String userId = "user123";
        Map<String, Object> details = new HashMap<>();
        details.put("employeeNumber", "invalid-number");
        details.put("validationRule", "pattern");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"errorType\":\"VALIDATION_ERROR\"}");

        // When
        auditLogService.logError(errorType, component, errorMessage, userId, details);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("Should record error logs when JSON serialization fails")
    void testJsonSerializationFailure() throws Exception {
        // Given
        String operation = "CREATE";
        String resource = "Employee";
        String resourceId = "123";
        String userId = "user123";
        Map<String, Object> details = new HashMap<>();

        when(objectMapper.writeValueAsString(any())).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Serialization failed") {});

        // When & Then
        // Verify that the method does not throw an exception, but handles errors internally
        try {
            auditLogService.logUserOperation(operation, resource, resourceId, userId, details);
        } catch (Exception e) {
            // Should not throw an exception
            throw new AssertionError("Method should not throw an exception", e);
        }

        // Verify method is called
        verify(objectMapper, times(1)).writeValueAsString(any());
    }
}

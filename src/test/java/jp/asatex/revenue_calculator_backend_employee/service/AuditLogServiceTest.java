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
 * AuditLogService 测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogService テスト")
class AuditLogServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        // 设置MDC
        MDC.put("sessionId", "test-session-123");
        MDC.put("requestId", "test-request-456");
        MDC.put("ipAddress", "192.168.1.100");
        MDC.put("userAgent", "Test-Agent/1.0");
    }

    @Test
    @DisplayName("记录用户操作审计日志应该正确工作")
    void testLogUserOperation() throws Exception {
        // Given
        String operation = "CREATE";
        String resource = "Employee";
        String resourceId = "123";
        String userId = "user123";
        Map<String, Object> details = new HashMap<>();
        details.put("name", "测试员工");
        details.put("employeeNumber", "EMP001");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"operation\":\"CREATE\"}");

        // When
        auditLogService.logUserOperation(operation, resource, resourceId, userId, details);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("记录数据访问审计日志应该正确工作")
    void testLogDataAccess() throws Exception {
        // Given
        String operation = "UPDATE";
        String table = "employees";
        String recordId = "123";
        String userId = "user123";
        Map<String, Object> oldValues = new HashMap<>();
        oldValues.put("name", "旧名称");
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("name", "新名称");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"operation\":\"UPDATE\"}");

        // When
        auditLogService.logDataAccess(operation, table, recordId, userId, oldValues, newValues);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("记录安全事件日志应该正确工作")
    void testLogSecurityEvent() throws Exception {
        // Given
        String eventType = "LOGIN_FAILED";
        String severity = "HIGH";
        String description = "登录失败次数过多";
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
    @DisplayName("记录性能日志应该正确工作")
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
    @DisplayName("记录API调用审计日志应该正确工作")
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
    @DisplayName("记录业务操作审计日志应该正确工作")
    void testLogBusinessOperation() throws Exception {
        // Given
        String businessOperation = "CREATE_EMPLOYEE";
        String entityType = "Employee";
        String entityId = "123";
        String userId = "user123";
        String result = "SUCCESS";
        Map<String, Object> details = new HashMap<>();
        details.put("employeeNumber", "EMP001");
        details.put("name", "测试员工");

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"businessOperation\":\"CREATE_EMPLOYEE\"}");

        // When
        auditLogService.logBusinessOperation(businessOperation, entityType, entityId, userId, result, details);

        // Then
        verify(objectMapper, times(1)).writeValueAsString(any());
    }

    @Test
    @DisplayName("记录系统事件日志应该正确工作")
    void testLogSystemEvent() throws Exception {
        // Given
        String eventType = "STARTUP";
        String component = "Application";
        String description = "应用启动完成";
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
    @DisplayName("记录错误审计日志应该正确工作")
    void testLogError() throws Exception {
        // Given
        String errorType = "VALIDATION_ERROR";
        String component = "EmployeeService";
        String errorMessage = "员工号格式不正确";
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
    @DisplayName("JSON序列化失败时应该记录错误日志")
    void testJsonSerializationFailure() throws Exception {
        // Given
        String operation = "CREATE";
        String resource = "Employee";
        String resourceId = "123";
        String userId = "user123";
        Map<String, Object> details = new HashMap<>();

        when(objectMapper.writeValueAsString(any())).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("序列化失败") {});

        // When & Then
        // 验证方法不会抛出异常，而是内部处理错误
        try {
            auditLogService.logUserOperation(operation, resource, resourceId, userId, details);
        } catch (Exception e) {
            // 不应该抛出异常
            throw new AssertionError("方法不应该抛出异常", e);
        }

        // 验证方法被调用
        verify(objectMapper, times(1)).writeValueAsString(any());
    }
}

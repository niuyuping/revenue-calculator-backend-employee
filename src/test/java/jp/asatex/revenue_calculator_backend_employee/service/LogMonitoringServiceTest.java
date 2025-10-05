package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LogMonitoringService 测试类
 */
@DisplayName("LogMonitoringService テスト")
class LogMonitoringServiceTest {

    private LogMonitoringService logMonitoringService;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        logMonitoringService = new LogMonitoringService(meterRegistry);
    }

    @Test
    @DisplayName("记录审计日志应该正确工作")
    void testRecordAuditLog() {
        // 记录审计日志
        logMonitoringService.recordAuditLog("CREATE", "Employee");
        logMonitoringService.recordAuditLog("UPDATE", "Employee");

        // 验证计数器增加
        Counter counter = meterRegistry.find("logs.audit").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("记录安全日志应该正确工作")
    void testRecordSecurityLog() {
        // 记录安全日志
        logMonitoringService.recordSecurityLog("LOGIN_FAILED", "HIGH");
        logMonitoringService.recordSecurityLog("UNAUTHORIZED_ACCESS", "CRITICAL");

        // 验证计数器增加
        Counter counter = meterRegistry.find("logs.security").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("记录性能日志应该正确工作")
    void testRecordPerformanceLog() {
        // 记录性能日志
        logMonitoringService.recordPerformanceLog("CREATE_EMPLOYEE", 150);
        logMonitoringService.recordPerformanceLog("UPDATE_EMPLOYEE", 200);

        // 验证计数器增加
        Counter counter = meterRegistry.find("logs.performance").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);

        // 验证计时器记录
        Timer timer = meterRegistry.find("logs.processing.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("记录错误日志应该正确工作")
    void testRecordErrorLog() {
        // 记录错误日志
        logMonitoringService.recordErrorLog("VALIDATION_ERROR", "EmployeeService");
        logMonitoringService.recordErrorLog("BUSINESS_ERROR", "EmployeeService");

        // 验证计数器增加
        Counter counter = meterRegistry.find("logs.error").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("记录请求日志应该正确工作")
    void testRecordRequestLog() {
        // 记录请求日志
        logMonitoringService.recordRequestLog("POST", "/api/v1/employee", 201);
        logMonitoringService.recordRequestLog("GET", "/api/v1/employee/1", 200);

        // 验证计数器增加
        Counter counter = meterRegistry.find("logs.request").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("记录应用日志应该正确工作")
    void testRecordApplicationLog() {
        // 记录应用日志
        logMonitoringService.recordApplicationLog("INFO", "EmployeeService");
        logMonitoringService.recordApplicationLog("DEBUG", "EmployeeRepository");

        // 验证统计信息
        LogMonitoringService.LogStats stats = logMonitoringService.getLogStats();
        assertThat(stats.getLogCounts().get("APPLICATION")).isEqualTo(2);
    }

    @Test
    @DisplayName("获取日志统计信息应该正确工作")
    void testGetLogStats() {
        // 执行一些操作
        logMonitoringService.recordAuditLog("CREATE", "Employee");
        logMonitoringService.recordSecurityLog("LOGIN_FAILED", "HIGH");
        logMonitoringService.recordPerformanceLog("CREATE_EMPLOYEE", 150);
        logMonitoringService.recordErrorLog("VALIDATION_ERROR", "EmployeeService");
        logMonitoringService.recordRequestLog("POST", "/api/v1/employee", 201);
        logMonitoringService.recordApplicationLog("INFO", "EmployeeService");

        // 获取统计信息
        LogMonitoringService.LogStats stats = logMonitoringService.getLogStats();

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalLogs()).isEqualTo(6);
        assertThat(stats.getTotalErrors()).isEqualTo(1);
        assertThat(stats.getErrorRate()).isEqualTo(1.0 / 6.0);
        assertThat(stats.getLogCounts().get("AUDIT")).isEqualTo(1);
        assertThat(stats.getLogCounts().get("SECURITY")).isEqualTo(1);
        assertThat(stats.getLogCounts().get("PERFORMANCE")).isEqualTo(1);
        assertThat(stats.getLogCounts().get("ERROR")).isEqualTo(1);
        assertThat(stats.getLogCounts().get("REQUEST")).isEqualTo(1);
        assertThat(stats.getLogCounts().get("APPLICATION")).isEqualTo(1);
    }

    @Test
    @DisplayName("重置日志统计信息应该正确工作")
    void testResetLogStats() {
        // 执行一些操作
        logMonitoringService.recordAuditLog("CREATE", "Employee");
        logMonitoringService.recordErrorLog("VALIDATION_ERROR", "EmployeeService");

        // 验证统计信息
        LogMonitoringService.LogStats statsBefore = logMonitoringService.getLogStats();
        assertThat(statsBefore.getTotalLogs()).isEqualTo(2);
        assertThat(statsBefore.getTotalErrors()).isEqualTo(1);

        // 重置统计信息
        logMonitoringService.resetLogStats();

        // 验证重置后的统计信息
        LogMonitoringService.LogStats statsAfter = logMonitoringService.getLogStats();
        assertThat(statsAfter.getTotalLogs()).isEqualTo(0);
        assertThat(statsAfter.getTotalErrors()).isEqualTo(0);
    }

    @Test
    @DisplayName("获取日志健康状态应该正确工作")
    void testGetLogHealthStatus() {
        // 测试健康状态
        logMonitoringService.recordAuditLog("CREATE", "Employee");
        logMonitoringService.recordAuditLog("UPDATE", "Employee");
        logMonitoringService.recordAuditLog("DELETE", "Employee");

        LogMonitoringService.LogHealthStatus healthStatus = logMonitoringService.getLogHealthStatus();
        assertThat(healthStatus).isNotNull();
        assertThat(healthStatus.getStatus()).isEqualTo("HEALTHY");
        assertThat(healthStatus.getErrorRate()).isEqualTo(0.0);
        assertThat(healthStatus.getTotalLogs()).isEqualTo(3);
        assertThat(healthStatus.getTotalErrors()).isEqualTo(0);
    }

    @Test
    @DisplayName("高错误率时应该返回警告状态")
    void testGetLogHealthStatusWithHighErrorRate() {
        // 记录大量日志，其中包含一些错误
        for (int i = 0; i < 10; i++) {
            logMonitoringService.recordAuditLog("CREATE", "Employee");
        }
        for (int i = 0; i < 2; i++) {
            logMonitoringService.recordErrorLog("VALIDATION_ERROR", "EmployeeService");
        }

        LogMonitoringService.LogHealthStatus healthStatus = logMonitoringService.getLogHealthStatus();
        assertThat(healthStatus).isNotNull();
        assertThat(healthStatus.getErrorRate()).isEqualTo(2.0 / 12.0);
        assertThat(healthStatus.getTotalLogs()).isEqualTo(12);
        assertThat(healthStatus.getTotalErrors()).isEqualTo(2);
    }

    @Test
    @DisplayName("日志统计信息toString应该正确格式化")
    void testLogStatsToString() {
        logMonitoringService.recordAuditLog("CREATE", "Employee");
        logMonitoringService.recordErrorLog("VALIDATION_ERROR", "EmployeeService");

        LogMonitoringService.LogStats stats = logMonitoringService.getLogStats();
        String statsString = stats.toString();
        
        assertThat(statsString).contains("totalLogs=2");
        assertThat(statsString).contains("totalErrors=1");
        assertThat(statsString).contains("errorRate=");
        assertThat(statsString).contains("avgProcessingTime=");
    }

    @Test
    @DisplayName("日志健康状态toString应该正确格式化")
    void testLogHealthStatusToString() {
        logMonitoringService.recordAuditLog("CREATE", "Employee");

        LogMonitoringService.LogHealthStatus healthStatus = logMonitoringService.getLogHealthStatus();
        String healthString = healthStatus.toString();
        
        assertThat(healthString).contains("status=");
        assertThat(healthString).contains("errorRate=");
        assertThat(healthString).contains("totalLogs=");
        assertThat(healthString).contains("totalErrors=");
        assertThat(healthString).contains("timestamp=");
    }
}

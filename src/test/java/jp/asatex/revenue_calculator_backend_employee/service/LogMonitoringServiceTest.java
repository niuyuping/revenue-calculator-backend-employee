package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LogMonitoringService test class
 */
@DisplayName("LogMonitoringService Test")
class LogMonitoringServiceTest {

    private LogMonitoringService logMonitoringService;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        logMonitoringService = new LogMonitoringService(meterRegistry);
    }

    @Test
    @DisplayName("Recording audit logs should work correctly")
    void testRecordAuditLog() {
        // Record audit log
        logMonitoringService.recordAuditLog("CREATE", "Employee");
        logMonitoringService.recordAuditLog("UPDATE", "Employee");

        // Verify counter increment
        Counter counter = meterRegistry.find("logs.audit").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Recording security logs should work correctly")
    void testRecordSecurityLog() {
        // Record security log
        logMonitoringService.recordSecurityLog("LOGIN_FAILED", "HIGH");
        logMonitoringService.recordSecurityLog("UNAUTHORIZED_ACCESS", "CRITICAL");

        // Verify counter increment
        Counter counter = meterRegistry.find("logs.security").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Recording performance logs should work correctly")
    void testRecordPerformanceLog() {
        // Record performance log
        logMonitoringService.recordPerformanceLog("CREATE_EMPLOYEE", 150);
        logMonitoringService.recordPerformanceLog("UPDATE_EMPLOYEE", 200);

        // Verify counter increment
        Counter counter = meterRegistry.find("logs.performance").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);

        // Verify timer recording
        Timer timer = meterRegistry.find("logs.processing.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Recording error logs should work correctly")
    void testRecordErrorLog() {
        // Record error log
        logMonitoringService.recordErrorLog("VALIDATION_ERROR", "EmployeeService");
        logMonitoringService.recordErrorLog("BUSINESS_ERROR", "EmployeeService");

        // Verify counter increment
        Counter counter = meterRegistry.find("logs.error").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Recording request logs should work correctly")
    void testRecordRequestLog() {
        // Record request log
        logMonitoringService.recordRequestLog("POST", "/api/v1/employee", 201);
        logMonitoringService.recordRequestLog("GET", "/api/v1/employee/1", 200);

        // Verify counter increment
        Counter counter = meterRegistry.find("logs.request").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Recording application logs should work correctly")
    void testRecordApplicationLog() {
        // Record application log
        logMonitoringService.recordApplicationLog("INFO", "EmployeeService");
        logMonitoringService.recordApplicationLog("DEBUG", "EmployeeRepository");

        // Verify statistics
        LogMonitoringService.LogStats stats = logMonitoringService.getLogStats();
        assertThat(stats.getLogCounts().get("APPLICATION")).isEqualTo(2);
    }

    @Test
    @DisplayName("Getting log statistics should work correctly")
    void testGetLogStats() {
        // Execute some operations
        logMonitoringService.recordAuditLog("CREATE", "Employee");
        logMonitoringService.recordSecurityLog("LOGIN_FAILED", "HIGH");
        logMonitoringService.recordPerformanceLog("CREATE_EMPLOYEE", 150);
        logMonitoringService.recordErrorLog("VALIDATION_ERROR", "EmployeeService");
        logMonitoringService.recordRequestLog("POST", "/api/v1/employee", 201);
        logMonitoringService.recordApplicationLog("INFO", "EmployeeService");

        // Get statistics
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
    @DisplayName("Resetting log statistics should work correctly")
    void testResetLogStats() {
        // Execute some operations
        logMonitoringService.recordAuditLog("CREATE", "Employee");
        logMonitoringService.recordErrorLog("VALIDATION_ERROR", "EmployeeService");

        // Verify statistics
        LogMonitoringService.LogStats statsBefore = logMonitoringService.getLogStats();
        assertThat(statsBefore.getTotalLogs()).isEqualTo(2);
        assertThat(statsBefore.getTotalErrors()).isEqualTo(1);

        // Reset statistics
        logMonitoringService.resetLogStats();

        // Verify statistics after reset
        LogMonitoringService.LogStats statsAfter = logMonitoringService.getLogStats();
        assertThat(statsAfter.getTotalLogs()).isEqualTo(0);
        assertThat(statsAfter.getTotalErrors()).isEqualTo(0);
    }

    @Test
    @DisplayName("Getting log health status should work correctly")
    void testGetLogHealthStatus() {
        // Test health status
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
    @DisplayName("Should return warning status when error rate is high")
    void testGetLogHealthStatusWithHighErrorRate() {
        // Record many logs, including some errors
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
    @DisplayName("Log statistics toString should format correctly")
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
    @DisplayName("Log health status toString should format correctly")
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

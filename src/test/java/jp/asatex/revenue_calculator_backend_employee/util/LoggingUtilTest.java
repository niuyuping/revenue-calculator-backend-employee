package jp.asatex.revenue_calculator_backend_employee.util;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Logging utility class test
 */
class LoggingUtilTest {

    @Test
    void testLogBusinessOperation() {
        // Test business operation logging
        LoggingUtil.logBusinessOperation("TEST_OPERATION", "Test details: %s", "test-value");
        // This test mainly verifies that the method does not throw an exception
    }

    @Test
    void testLogPerformance() {
        // Test performance logging
        Duration shortDuration = Duration.ofMillis(100);
        Duration longDuration = Duration.ofMillis(1500);
        
        LoggingUtil.logPerformance("SHORT_OPERATION", shortDuration, "Short operation details");
        LoggingUtil.logPerformance("LONG_OPERATION", longDuration, "Long operation details");
        // This test mainly verifies that the method does not throw an exception
    }

    @Test
    void testLogError() {
        // Test error logging
        Exception testException = new RuntimeException("Test error");
        LoggingUtil.logError("TEST_ERROR", testException, "Test error details");
        // This test mainly verifies that the method does not throw an exception
    }

    @Test
    void testLogDataAccess() {
        // Test data access logging
        LoggingUtil.logDataAccess("SELECT", "employees", 123L);
        LoggingUtil.logDataAccess("INSERT", "employees", 456L);
        // This test mainly verifies that the method does not throw an exception
    }

    @Test
    void testLogSecurity() {
        // Test security logging
        LoggingUtil.logSecurity("LOGIN_ATTEMPT", "User attempted to login");
        LoggingUtil.logSecurity("ACCESS_DENIED", "Access denied for user");
        // This test mainly verifies that the method does not throw an exception
    }

    @Test
    void testSetAndClearContext() {
        // Test MDC context setting and clearing
        LoggingUtil.setContext("testKey", "testValue");
        assertThat(MDC.get("testKey")).isEqualTo("testValue");
        
        LoggingUtil.clearContext();
        assertThat(MDC.get("testKey")).isNull();
    }

    @Test
    void testLogOperationTime() {
        // Test operation time logging
        Mono<String> testMono = Mono.just("test-result")
                .delayElement(Duration.ofMillis(100));
        
        StepVerifier.create(LoggingUtil.logOperationTime("TEST_OPERATION", testMono))
                .expectNext("test-result")
                .verifyComplete();
    }

    @Test
    void testLogOperationTimeWithContext() {
        // Test operation time logging with context
        Map<String, String> context = new HashMap<>();
        context.put("userId", "123");
        context.put("operation", "test");
        
        Mono<String> testMono = Mono.just("test-result")
                .delayElement(Duration.ofMillis(50));
        
        StepVerifier.create(LoggingUtil.logOperationTimeWithContext("TEST_OPERATION", testMono, context))
                .expectNext("test-result")
                .verifyComplete();
    }

    @Test
    void testLogApiCall() {
        // Test API call logging
        LoggingUtil.logApiCall("/api/v1/employee", "GET", 200, Duration.ofMillis(100));
        LoggingUtil.logApiCall("/api/v1/employee", "POST", 201, Duration.ofMillis(200));
        LoggingUtil.logApiCall("/api/v1/employee", "GET", 404, Duration.ofMillis(50));
        LoggingUtil.logApiCall("/api/v1/employee", "GET", 200, Duration.ofMillis(1500));
        // This test mainly verifies that the method does not throw an exception
    }
}


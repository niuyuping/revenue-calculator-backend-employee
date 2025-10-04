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
 * 日志工具类测试
 */
class LoggingUtilTest {

    @Test
    void testLogBusinessOperation() {
        // 测试业务操作日志记录
        LoggingUtil.logBusinessOperation("TEST_OPERATION", "Test details: %s", "test-value");
        // 这个测试主要验证方法不会抛出异常
    }

    @Test
    void testLogPerformance() {
        // 测试性能日志记录
        Duration shortDuration = Duration.ofMillis(100);
        Duration longDuration = Duration.ofMillis(1500);
        
        LoggingUtil.logPerformance("SHORT_OPERATION", shortDuration, "Short operation details");
        LoggingUtil.logPerformance("LONG_OPERATION", longDuration, "Long operation details");
        // 这个测试主要验证方法不会抛出异常
    }

    @Test
    void testLogError() {
        // 测试错误日志记录
        Exception testException = new RuntimeException("Test error");
        LoggingUtil.logError("TEST_ERROR", testException, "Test error details");
        // 这个测试主要验证方法不会抛出异常
    }

    @Test
    void testLogDataAccess() {
        // 测试数据访问日志记录
        LoggingUtil.logDataAccess("SELECT", "employees", 123L);
        LoggingUtil.logDataAccess("INSERT", "employees", 456L);
        // 这个测试主要验证方法不会抛出异常
    }

    @Test
    void testLogSecurity() {
        // 测试安全日志记录
        LoggingUtil.logSecurity("LOGIN_ATTEMPT", "User attempted to login");
        LoggingUtil.logSecurity("ACCESS_DENIED", "Access denied for user");
        // 这个测试主要验证方法不会抛出异常
    }

    @Test
    void testSetAndClearContext() {
        // 测试MDC上下文设置和清除
        LoggingUtil.setContext("testKey", "testValue");
        assertThat(MDC.get("testKey")).isEqualTo("testValue");
        
        LoggingUtil.clearContext();
        assertThat(MDC.get("testKey")).isNull();
    }

    @Test
    void testLogOperationTime() {
        // 测试操作时间日志记录
        Mono<String> testMono = Mono.just("test-result")
                .delayElement(Duration.ofMillis(100));
        
        StepVerifier.create(LoggingUtil.logOperationTime("TEST_OPERATION", testMono))
                .expectNext("test-result")
                .verifyComplete();
    }

    @Test
    void testLogOperationTimeWithContext() {
        // 测试带上下文的操作时间日志记录
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
        // 测试API调用日志记录
        LoggingUtil.logApiCall("/api/v1/employee", "GET", 200, Duration.ofMillis(100));
        LoggingUtil.logApiCall("/api/v1/employee", "POST", 201, Duration.ofMillis(200));
        LoggingUtil.logApiCall("/api/v1/employee", "GET", 404, Duration.ofMillis(50));
        LoggingUtil.logApiCall("/api/v1/employee", "GET", 200, Duration.ofMillis(1500));
        // 这个测试主要验证方法不会抛出异常
    }
}


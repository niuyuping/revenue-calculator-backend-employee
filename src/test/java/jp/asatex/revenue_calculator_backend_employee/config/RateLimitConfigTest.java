package jp.asatex.revenue_calculator_backend_employee.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RateLimitConfig 测试类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {"spring.flyway.enabled=false"})
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("RateLimitConfig テスト")
class RateLimitConfigTest {

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Test
    @DisplayName("限流器注册表应该正确配置")
    void testRateLimiterRegistryConfiguration() {
        assertThat(rateLimiterRegistry).isNotNull();
    }

    @Test
    @DisplayName("员工API限流器应该正确配置")
    void testEmployeeApiRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-api");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(100);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("员工搜索API限流器应该正确配置")
    void testEmployeeSearchRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-search");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(50);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("员工创建API限流器应该正确配置")
    void testEmployeeCreateRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-create");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(20);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(2);
    }

    @Test
    @DisplayName("员工更新API限流器应该正确配置")
    void testEmployeeUpdateRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-update");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(30);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("员工删除API限流器应该正确配置")
    void testEmployeeDeleteRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-delete");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(10);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(2);
    }

    @Test
    @DisplayName("分页查询API限流器应该正确配置")
    void testEmployeePaginationRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-pagination");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(200);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("监控API限流器应该正确配置")
    void testMonitoringApiRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("monitoring-api");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(10);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("全局API限流器应该正确配置")
    void testGlobalApiRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("global-api");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(1000);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("所有限流器都应该在注册表中")
    void testAllRateLimitersInRegistry() {
        assertThat(rateLimiterRegistry.rateLimiter("employee-api")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-search")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-create")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-update")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-delete")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-pagination")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("monitoring-api")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("global-api")).isNotNull();
    }
}

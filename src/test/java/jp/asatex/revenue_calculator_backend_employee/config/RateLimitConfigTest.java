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
 * RateLimitConfig test class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {"spring.flyway.enabled=false"})
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("RateLimitConfig Test")
class RateLimitConfigTest {

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @Test
    @DisplayName("Rate limiter registry should be configured correctly")
    void testRateLimiterRegistryConfiguration() {
        assertThat(rateLimiterRegistry).isNotNull();
    }


    @Test
    @DisplayName("Employee search API rate limiter should be configured correctly")
    void testEmployeeSearchRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-search");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(50);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("Employee creation API rate limiter should be configured correctly")
    void testEmployeeCreateRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-create");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(20);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(2);
    }

    @Test
    @DisplayName("Employee update API rate limiter should be configured correctly")
    void testEmployeeUpdateRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-update");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(30);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("Employee deletion API rate limiter should be configured correctly")
    void testEmployeeDeleteRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-delete");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(10);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(2);
    }

    @Test
    @DisplayName("Pagination query API rate limiter should be configured correctly")
    void testEmployeePaginationRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("employee-pagination");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(200);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }

    @Test
    @DisplayName("Monitoring API rate limiter should be configured correctly")
    void testMonitoringApiRateLimiterConfiguration() {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("monitoring-api");
        assertThat(rateLimiter).isNotNull();
        RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
        assertThat(config.getLimitForPeriod()).isEqualTo(10);
        assertThat(config.getLimitRefreshPeriod().toMinutes()).isEqualTo(1);
        assertThat(config.getTimeoutDuration().toSeconds()).isEqualTo(1);
    }


    @Test
    @DisplayName("All rate limiters should be in the registry")
    void testAllRateLimitersInRegistry() {
        assertThat(rateLimiterRegistry.rateLimiter("employee-search")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-create")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-update")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-delete")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("employee-pagination")).isNotNull();
        assertThat(rateLimiterRegistry.rateLimiter("monitoring-api")).isNotNull();
    }
}

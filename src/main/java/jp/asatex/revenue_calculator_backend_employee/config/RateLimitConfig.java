package jp.asatex.revenue_calculator_backend_employee.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Rate limiting configuration class
 * Configures rate limiting strategies for different API endpoints
 */
@Configuration
public class RateLimitConfig {

    /**
     * Configure rate limiter registry
     * @return RateLimiterRegistry
     */
    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    /**
     * Employee API rate limiter
     * 100 requests per minute
     */
    @Bean("employee-api")
    public RateLimiter employeeApiRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(100) // Number of requests allowed per time window
                .limitRefreshPeriod(Duration.ofMinutes(1)) // Time window size
                .timeoutDuration(Duration.ofSeconds(1)) // Wait time
                .build();

        return registry.rateLimiter("employee-api", config);
    }

    /**
     * Employee search API rate limiter
     * 50 requests per minute
     */
    @Bean("employee-search")
    public RateLimiter employeeSearchRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(50)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        return registry.rateLimiter("employee-search", config);
    }

    /**
     * Employee creation API rate limiter
     * 20 requests per minute
     */
    @Bean("employee-create")
    public RateLimiter employeeCreateRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(20)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(2))
                .build();

        return registry.rateLimiter("employee-create", config);
    }

    /**
     * Employee update API rate limiter
     * 30 requests per minute
     */
    @Bean("employee-update")
    public RateLimiter employeeUpdateRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(30)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        return registry.rateLimiter("employee-update", config);
    }

    /**
     * Employee deletion API rate limiter
     * 10 requests per minute
     */
    @Bean("employee-delete")
    public RateLimiter employeeDeleteRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(2))
                .build();

        return registry.rateLimiter("employee-delete", config);
    }

    /**
     * Pagination query API rate limiter
     * 200 requests per minute
     */
    @Bean("employee-pagination")
    public RateLimiter employeePaginationRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(200)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        return registry.rateLimiter("employee-pagination", config);
    }

    /**
     * Monitoring API rate limiter
     * 10 requests per minute
     */
    @Bean("monitoring-api")
    public RateLimiter monitoringApiRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        return registry.rateLimiter("monitoring-api", config);
    }

    /**
     * Global API rate limiter
     * 1000 requests per minute
     */
    @Bean("global-api")
    public RateLimiter globalApiRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(1000)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        return registry.rateLimiter("global-api", config);
    }
}
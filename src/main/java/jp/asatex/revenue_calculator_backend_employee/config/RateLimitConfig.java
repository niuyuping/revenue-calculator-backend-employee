package jp.asatex.revenue_calculator_backend_employee.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Rate limiting configuration class
 * Configures rate limiting strategies for different API endpoints with monitoring
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
     * Rate limit triggered counter for monitoring
     * Tracks when rate limits are exceeded
     */
    @Bean
    public Counter rateLimitTriggeredCounter(MeterRegistry meterRegistry) {
        return Counter.builder("rate.limit.triggered.total")
                .description("Total number of rate limit triggers by limiter name")
                .tag("service", "revenue-calculator-employee")
                .tag("component", "rate-limiter")
                .register(meterRegistry);
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
}
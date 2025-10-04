package jp.asatex.revenue_calculator_backend_employee.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 限流配置类
 * 配置API限流策略
 */
@Configuration
public class RateLimitConfig {

    /**
     * 限流器注册表
     */
    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    /**
     * 员工API限流器
     * 每分钟最多100个请求
     */
    @Bean
    public RateLimiter employeeApiRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(100) // 每个时间窗口最多100个请求
                .limitRefreshPeriod(Duration.ofMinutes(1)) // 时间窗口为1分钟
                .timeoutDuration(Duration.ofSeconds(1)) // 等待时间1秒
                .build();

        return registry.rateLimiter("employee-api", config);
    }

    /**
     * 员工搜索API限流器
     * 每分钟最多50个请求
     */
    @Bean
    public RateLimiter employeeSearchRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(50) // 每个时间窗口最多50个请求
                .limitRefreshPeriod(Duration.ofMinutes(1)) // 时间窗口为1分钟
                .timeoutDuration(Duration.ofSeconds(1)) // 等待时间1秒
                .build();

        return registry.rateLimiter("employee-search", config);
    }

    /**
     * 员工创建/更新API限流器
     * 每分钟最多20个请求
     */
    @Bean
    public RateLimiter employeeWriteRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(20) // 每个时间窗口最多20个请求
                .limitRefreshPeriod(Duration.ofMinutes(1)) // 时间窗口为1分钟
                .timeoutDuration(Duration.ofSeconds(2)) // 等待时间2秒
                .build();

        return registry.rateLimiter("employee-write", config);
    }
}

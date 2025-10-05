package jp.asatex.revenue_calculator_backend_employee.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 限流配置类
 * 配置不同API端点的限流策略
 */
@Configuration
public class RateLimitConfig {

    /**
     * 配置限流器注册表
     * @return RateLimiterRegistry
     */
    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    /**
     * 员工API限流器
     * 每分钟100次请求
     */
    @Bean("employee-api")
    public RateLimiter employeeApiRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(100) // 每个时间窗口允许的请求数
                .limitRefreshPeriod(Duration.ofMinutes(1)) // 时间窗口大小
                .timeoutDuration(Duration.ofSeconds(1)) // 等待时间
                .build();

        return registry.rateLimiter("employee-api", config);
    }

    /**
     * 员工搜索API限流器
     * 每分钟50次请求
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
     * 员工创建API限流器
     * 每分钟20次请求
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
     * 员工更新API限流器
     * 每分钟30次请求
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
     * 员工删除API限流器
     * 每分钟10次请求
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
     * 分页查询API限流器
     * 每分钟200次请求
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
     * 监控API限流器
     * 每分钟10次请求
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
     * 全局API限流器
     * 每分钟1000次请求
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
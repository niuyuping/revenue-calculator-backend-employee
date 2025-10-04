package jp.asatex.revenue_calculator_backend_employee.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义指标配置类
 * 配置业务相关的监控指标
 */
@Configuration
public class MetricsConfig {

    /**
     * 员工操作计数器
     */
    @Bean
    public Counter employeeOperationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.operations.total")
                .description("Total number of employee operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 员工创建计数器
     */
    @Bean
    public Counter employeeCreateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.create.total")
                .description("Total number of employee creations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 员工更新计数器
     */
    @Bean
    public Counter employeeUpdateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.update.total")
                .description("Total number of employee updates")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 员工删除计数器
     */
    @Bean
    public Counter employeeDeleteCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.delete.total")
                .description("Total number of employee deletions")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 员工查询计数器
     */
    @Bean
    public Counter employeeQueryCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.query.total")
                .description("Total number of employee queries")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 员工操作计时器
     */
    @Bean
    public Timer employeeOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("employee.operations.duration")
                .description("Duration of employee operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 数据库操作计时器
     */
    @Bean
    public Timer databaseOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("database.operations.duration")
                .description("Duration of database operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 缓存命中计数器
     */
    @Bean
    public Counter cacheHitCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache.hits.total")
                .description("Total number of cache hits")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 缓存未命中计数器
     */
    @Bean
    public Counter cacheMissCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache.misses.total")
                .description("Total number of cache misses")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * 限流触发计数器
     */
    @Bean
    public Counter rateLimitTriggeredCounter(MeterRegistry meterRegistry) {
        return Counter.builder("rate.limit.triggered.total")
                .description("Total number of rate limit triggers")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }
}

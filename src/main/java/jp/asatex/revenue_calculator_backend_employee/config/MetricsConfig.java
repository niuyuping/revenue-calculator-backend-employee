package jp.asatex.revenue_calculator_backend_employee.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom metrics configuration class
 * Configures business-related monitoring metrics
 */
@Configuration
public class MetricsConfig {

    /**
     * Employee operation counter
     */
    @Bean
    public Counter employeeOperationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.operations.total")
                .description("Total number of employee operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * Employee creation counter
     */
    @Bean
    public Counter employeeCreateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.create.total")
                .description("Total number of employee creations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * Employee update counter
     */
    @Bean
    public Counter employeeUpdateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.update.total")
                .description("Total number of employee updates")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * Employee deletion counter
     */
    @Bean
    public Counter employeeDeleteCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.delete.total")
                .description("Total number of employee deletions")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * Employee query counter
     */
    @Bean
    public Counter employeeQueryCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.query.total")
                .description("Total number of employee queries")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * Employee operation timer
     */
    @Bean
    public Timer employeeOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("employee.operations.duration")
                .description("Duration of employee operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    /**
     * Database operation timer
     */
    @Bean
    public Timer databaseOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("database.operations.duration")
                .description("Duration of database operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }


    /**
     * Rate limit trigger counter
     */
    @Bean
    public Counter rateLimitTriggeredCounter(MeterRegistry meterRegistry) {
        return Counter.builder("rate.limit.triggered.total")
                .description("Total number of rate limit triggers")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }
}

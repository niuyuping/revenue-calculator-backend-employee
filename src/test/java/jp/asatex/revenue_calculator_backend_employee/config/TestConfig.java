package jp.asatex.revenue_calculator_backend_employee.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration class
 * Provides necessary beans for testing
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    @Profile("test")
    public Counter employeeOperationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.operations.total")
                .description("Total number of employee operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    @Bean
    @Primary
    @Profile("test")
    public Counter employeeCreateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.create.total")
                .description("Total number of employee creations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    @Bean
    @Primary
    @Profile("test")
    public Counter employeeUpdateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.update.total")
                .description("Total number of employee updates")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    @Bean
    @Primary
    @Profile("test")
    public Counter employeeDeleteCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.delete.total")
                .description("Total number of employee deletions")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    @Bean
    @Primary
    @Profile("test")
    public Counter employeeQueryCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.query.total")
                .description("Total number of employee queries")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    @Bean
    @Primary
    @Profile("test")
    public Timer employeeOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("employee.operations.duration")
                .description("Duration of employee operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

    @Bean
    @Primary
    @Profile("test")
    public Timer databaseOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("database.operations.duration")
                .description("Duration of database operations")
                .tag("service", "revenue-calculator-employee")
                .register(meterRegistry);
    }

}

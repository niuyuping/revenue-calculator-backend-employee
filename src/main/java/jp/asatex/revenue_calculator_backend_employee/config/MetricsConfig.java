package jp.asatex.revenue_calculator_backend_employee.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom metrics configuration class
 * Configures business-related monitoring metrics for employee management system
 */
@Configuration
public class MetricsConfig {

    /**
     * Employee operation counter - tracks all employee-related operations
     */
    @Bean
    public Counter employeeOperationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.operations.total")
                .description("Total number of employee operations (create, update, delete, query)")
                .tag("service", "revenue-calculator-employee")
                .tag("component", "employee-service")
                .register(meterRegistry);
    }

    /**
     * Employee creation counter - tracks employee creation operations
     */
    @Bean
    public Counter employeeCreateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.create.total")
                .description("Total number of employee creation operations")
                .tag("service", "revenue-calculator-employee")
                .tag("component", "employee-service")
                .tag("operation", "create")
                .register(meterRegistry);
    }

    /**
     * Employee update counter - tracks employee update operations
     */
    @Bean
    public Counter employeeUpdateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.update.total")
                .description("Total number of employee update operations")
                .tag("service", "revenue-calculator-employee")
                .tag("component", "employee-service")
                .tag("operation", "update")
                .register(meterRegistry);
    }

    /**
     * Employee deletion counter - tracks employee deletion operations
     */
    @Bean
    public Counter employeeDeleteCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.delete.total")
                .description("Total number of employee deletion operations")
                .tag("service", "revenue-calculator-employee")
                .tag("component", "employee-service")
                .tag("operation", "delete")
                .register(meterRegistry);
    }

    /**
     * Employee query counter - tracks employee query operations
     */
    @Bean
    public Counter employeeQueryCounter(MeterRegistry meterRegistry) {
        return Counter.builder("employee.query.total")
                .description("Total number of employee query operations (get, search, list)")
                .tag("service", "revenue-calculator-employee")
                .tag("component", "employee-service")
                .tag("operation", "query")
                .register(meterRegistry);
    }

    /**
     * Employee operation timer - tracks duration of employee operations
     */
    @Bean
    public Timer employeeOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("employee.operations.duration")
                .description("Duration of employee operations in seconds")
                .tag("service", "revenue-calculator-employee")
                .tag("component", "employee-service")
                .register(meterRegistry);
    }
}

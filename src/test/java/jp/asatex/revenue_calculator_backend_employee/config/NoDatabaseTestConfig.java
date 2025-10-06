package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * No database test configuration
 * Used for configuration tests that do not require database connections
 */
@TestConfiguration
@Profile("test")
public class NoDatabaseTestConfig {

    @Bean
    @Primary
    @Profile("test")
    public String disableFlyway() {
        // Placeholder Bean to disable Flyway
        return "flyway-disabled";
    }
}

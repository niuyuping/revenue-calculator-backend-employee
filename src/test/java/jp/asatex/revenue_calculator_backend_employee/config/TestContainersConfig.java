package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * TestContainers configuration class
 * Provides PostgreSQL container for testing
 */
@TestConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource") // TestContainers automatically manages container lifecycle in Spring Boot test environment
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("asatex-revenue-test")
                .withUsername("db_user")
                .withPassword("local")
                .waitingFor(Wait.forListeningPort())
                .withReuse(true);
        
        // Start container
        container.start();
        return container;
    }

}

package jp.asatex.revenue_calculator_backend_employee.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Logging configuration test
 * Verifies logging configuration and request logging functionality
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = {"spring.flyway.enabled=false"})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
class LoggingConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testRequestLoggingFilter() {
        // Test if request logging filter works correctly
        webTestClient.get()
                .uri("/api/v1/employee/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Request-ID");
    }

    @Test
    void testEmployeeApiLogging() {
        // Test employee API logging
        webTestClient.get()
                .uri("/api/v1/employee")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testErrorLogging() {
        // Test error logging - using a non-existent employee ID
        webTestClient.get()
                .uri("/api/v1/employee/999999")
                .exchange()
                .expectStatus().isNotFound();
    }
}


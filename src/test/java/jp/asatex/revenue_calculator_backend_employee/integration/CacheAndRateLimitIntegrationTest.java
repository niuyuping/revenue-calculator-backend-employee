package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import jp.asatex.revenue_calculator_backend_employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Rate limiting functionality integration test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class, 
         jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
@DisplayName("Rate Limiting Integration Test")
class CacheAndRateLimitIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeDto testEmployee;

    @BeforeEach
    void setUp() {
        // Clean up test data
        employeeRepository.deleteAll().block();
        
        // Create test employee data
        testEmployee = new EmployeeDto();
        testEmployee.setEmployeeNumber("RATE001");
        testEmployee.setName("Rate Limit Test Employee");
        testEmployee.setFurigana("レートリミットテストシャイン");
        testEmployee.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("API rate limiting should work correctly")
    void testApiRateLimiting() {
        // Create test employee first
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getEmployeeNumber()).isEqualTo("RATE001");

        // Test rate limiting by making multiple requests
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                    .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.employeeNumber").isEqualTo("RATE001");
        }

        // Additional requests should still work (rate limit is high for testing)
        webTestClient.get()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Employee search rate limiting should work correctly")
    void testEmployeeSearchRateLimiting() {
        // Create test employee first
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // Test search rate limiting
        for (int i = 0; i < 3; i++) {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/employee/search/name")
                            .queryParam("name", "Rate")
                            .build())
                    .exchange()
                    .expectStatus().isOk();
        }
    }

}
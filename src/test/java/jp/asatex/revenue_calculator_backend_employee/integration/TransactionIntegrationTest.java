package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
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
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Transaction functionality integration test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class, 
         jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
@DisplayName("Transaction Functionality Integration Test")
class TransactionIntegrationTest {

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
        testEmployee.setEmployeeNumber("TEST001");
        testEmployee.setName("Test Employee");
        testEmployee.setFurigana("test employee");
        testEmployee.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Creating employee transaction should commit successfully")
    void testCreateEmployeeTransactionCommit() {
        // Create employee
        EmployeeDto createdEmployee = webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(testEmployee))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdEmployee).isNotNull();
        if (createdEmployee != null) {
            assertThat(createdEmployee.getEmployeeId()).isNotNull();
            assertThat(createdEmployee.getEmployeeNumber()).isEqualTo("TEST001");
        }

        // Verify employee is actually saved to database
        Employee savedEmployee = employeeRepository.findByEmployeeNumber("TEST001").block();
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("Test Employee");
    }

    @Test
    @DisplayName("Creating duplicate employee number should rollback transaction")
    void testCreateDuplicateEmployeeTransactionRollback() {
        // First create an employee
        employeeService.createEmployee(testEmployee).block();

        // Try to create employee with duplicate employee number
        EmployeeDto duplicateEmployee = new EmployeeDto();
        duplicateEmployee.setEmployeeNumber("TEST001"); // Duplicate employee number
        duplicateEmployee.setName("Duplicate Employee");
        duplicateEmployee.setFurigana("duplicate employee");
        duplicateEmployee.setBirthday(LocalDate.of(1990, 1, 1));

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(duplicateEmployee))
                .exchange()
                .expectStatus().isEqualTo(409);

        // Verify only one employee is saved
        Long count = employeeRepository.count().block();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Update employee transaction should commit successfully")
    void testUpdateEmployeeTransactionCommit() {
        // First create employee
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // Update employee information
        createdEmployee.setName("Updated Employee");
        createdEmployee.setFurigana("updated employee");

        EmployeeDto updatedEmployee = webTestClient.put()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createdEmployee))
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(updatedEmployee).isNotNull();
        if (updatedEmployee != null) {
            assertThat(updatedEmployee.getName()).isEqualTo("Updated Employee");
            assertThat(updatedEmployee.getFurigana()).isEqualTo("updated employee");
        }

        // Verify update in database
        Employee savedEmployee = employeeRepository.findById(createdEmployee.getEmployeeId()).block();
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("Updated Employee");
    }

    @Test
    @DisplayName("Delete employee transaction should commit successfully")
    void testDeleteEmployeeTransactionCommit() {
        // First create employee
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // Delete employee
        webTestClient.delete()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .exchange()
                .expectStatus().isNoContent();

        // Verify employee has been deleted
        Employee deletedEmployee = employeeRepository.findById(createdEmployee.getEmployeeId()).block();
        assertThat(deletedEmployee).isNull();
    }

    @Test
    @DisplayName("Deleting non-existent employee should rollback transaction")
    void testDeleteNonExistentEmployeeTransactionRollback() {
        // Try to delete non-existent employee
        webTestClient.delete()
                .uri("/api/v1/employee/{id}", 99999L)
                .exchange()
                .expectStatus().isNotFound();

        // Verify database state has not changed
        Long count = employeeRepository.count().block();
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("Service layer transaction annotations should work correctly")
    void testServiceLayerTransactionAnnotations() {
        // Test employee creation transaction
        StepVerifier.create(employeeService.createEmployee(testEmployee))
                .assertNext(employee -> {
                    assertThat(employee).isNotNull();
                    assertThat(employee.getEmployeeNumber()).isEqualTo("TEST001");
                })
                .verifyComplete();

        // Verify employee is saved
        Employee savedEmployee = employeeRepository.findByEmployeeNumber("TEST001").block();
        assertThat(savedEmployee).isNotNull();
    }

    @Test
    @DisplayName("Transaction exceptions should be handled correctly")
    void testTransactionExceptionHandling() {
        // Create invalid employee data to trigger exception
        EmployeeDto invalidEmployee = new EmployeeDto();
        // Do not set required fields, should trigger validation exception

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidEmployee))
                .exchange()
                .expectStatus().isBadRequest();

        // Verify no data is saved
        Long count = employeeRepository.count().block();
        assertThat(count).isEqualTo(0);
    }
}

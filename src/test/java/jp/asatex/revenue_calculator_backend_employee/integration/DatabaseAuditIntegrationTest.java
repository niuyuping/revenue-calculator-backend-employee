package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.config.TestConfig;
import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.entity.DatabaseAuditLog;
import jp.asatex.revenue_calculator_backend_employee.repository.DatabaseAuditLogRepository;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import jp.asatex.revenue_calculator_backend_employee.service.DatabaseAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Database audit functionality integration test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({TestConfig.class, jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
@DisplayName("Database Audit Integration Test")
class DatabaseAuditIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Autowired
    private DatabaseAuditLogRepository databaseAuditLogRepository;

    @Autowired
    private DatabaseAuditService databaseAuditService;

    @BeforeEach
    void setUp() {
        // Clean up database
        employeeRepository.deleteAll().block();
        databaseAuditLogRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Should record database audit log when creating employee")
    void testCreateEmployeeAuditLog() {
        // Given
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("AUDIT001");
        newEmployee.setName("Audit Test Employee");
        newEmployee.setFurigana("audit test employee"); // Using katakana that conforms to validation rules
        newEmployee.setBirthday(LocalDate.of(1990, 1, 1));

        // When
        EmployeeDto createdEmployee = webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newEmployee))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertThat(createdEmployee).isNotNull();
        if (createdEmployee != null) {
            assertThat(createdEmployee.getEmployeeId()).isNotNull();
        }

        // Wait for audit log recording
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify audit log
        Mono<DatabaseAuditLog> auditLogMono = databaseAuditLogRepository.findByTableName("employees")
                .filter(log -> "INSERT".equals(log.getOperationType()))
                .next();

        StepVerifier.create(auditLogMono)
                .assertNext(auditLog -> {
                    assertThat(auditLog.getOperationType()).isEqualTo("INSERT");
                    assertThat(auditLog.getTableName()).isEqualTo("employees");
                    assertThat(auditLog.getOperationStatus()).isEqualTo("SUCCESS");
                    assertThat(auditLog.getNewValues()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should record database audit log when updating employee")
    void testUpdateEmployeeAuditLog() {
        // Given - First create an employee
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("AUDIT002");
        newEmployee.setName("Original Employee");
        newEmployee.setFurigana("original employee");
        newEmployee.setBirthday(LocalDate.of(1991, 2, 2));

        EmployeeDto createdEmployee = webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newEmployee))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdEmployee).isNotNull();

        // Wait for audit log recording
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When - Update employee
        EmployeeDto updatedEmployeeDto = new EmployeeDto();
        updatedEmployeeDto.setEmployeeNumber("AUDIT002");
        updatedEmployeeDto.setName("Updated Employee");
        updatedEmployeeDto.setFurigana("updated employee");
        updatedEmployeeDto.setBirthday(LocalDate.of(1991, 2, 2));

        EmployeeDto updatedEmployee = webTestClient.put()
                .uri("/api/v1/employee/{id}", createdEmployee != null ? createdEmployee.getEmployeeId() : null)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updatedEmployeeDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertThat(updatedEmployee).isNotNull();
        if (updatedEmployee != null) {
            assertThat(updatedEmployee.getName()).isEqualTo("Updated Employee");
        }

        // Wait for audit log recording
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify audit log
        Mono<DatabaseAuditLog> auditLogMono = databaseAuditLogRepository.findByTableName("employees")
                .filter(log -> "UPDATE".equals(log.getOperationType()))
                .next();

        StepVerifier.create(auditLogMono)
                .assertNext(auditLog -> {
                    assertThat(auditLog.getOperationType()).isEqualTo("UPDATE");
                    assertThat(auditLog.getTableName()).isEqualTo("employees");
                    assertThat(auditLog.getOperationStatus()).isEqualTo("SUCCESS");
                    assertThat(auditLog.getOldValues()).isNotNull();
                    assertThat(auditLog.getNewValues()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should record database audit log when deleting employee")
    void testDeleteEmployeeAuditLog() {
        // Given - First create an employee
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("AUDIT003");
        newEmployee.setName("Employee To Delete");
        newEmployee.setFurigana("employee to delete");
        newEmployee.setBirthday(LocalDate.of(1992, 3, 3));

        EmployeeDto createdEmployee = webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newEmployee))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdEmployee).isNotNull();

        // Wait for audit log recording
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When - Delete employee
        webTestClient.delete()
                .uri("/api/v1/employee/{id}", createdEmployee != null ? createdEmployee.getEmployeeId() : null)
                .exchange()
                .expectStatus().isNoContent();

        // Wait for audit log recording
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then - Verify audit log
        Mono<DatabaseAuditLog> auditLogMono = databaseAuditLogRepository.findByTableName("employees")
                .filter(log -> "DELETE".equals(log.getOperationType()))
                .next();

        StepVerifier.create(auditLogMono)
                .assertNext(auditLog -> {
                    assertThat(auditLog.getOperationType()).isEqualTo("DELETE");
                    assertThat(auditLog.getTableName()).isEqualTo("employees");
                    assertThat(auditLog.getOperationStatus()).isEqualTo("SUCCESS");
                    assertThat(auditLog.getOldValues()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should record database audit log when querying employee")
    void testGetEmployeeAuditLog() {
        // Given - First create an employee
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("AUDIT004");
        newEmployee.setName("Query Test Employee");
        newEmployee.setFurigana("query test employee");
        newEmployee.setBirthday(LocalDate.of(1993, 4, 4));

        EmployeeDto createdEmployee = webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(newEmployee))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdEmployee).isNotNull();

        // Wait for audit log recording
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When - Query employee
        EmployeeDto retrievedEmployee = webTestClient.get()
                .uri("/api/v1/employee/{id}", createdEmployee != null ? createdEmployee.getEmployeeId() : null)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertThat(retrievedEmployee).isNotNull();
        if (retrievedEmployee != null) {
            assertThat(retrievedEmployee.getName()).isEqualTo("Query Test Employee");
        }

        // Wait for audit log recording
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify audit log
        Mono<DatabaseAuditLog> auditLogMono = databaseAuditLogRepository.findByTableName("employees")
                .filter(log -> "SELECT".equals(log.getOperationType()))
                .next();

        StepVerifier.create(auditLogMono)
                .assertNext(auditLog -> {
                    assertThat(auditLog.getOperationType()).isEqualTo("SELECT");
                    assertThat(auditLog.getTableName()).isEqualTo("employees");
                    assertThat(auditLog.getOperationStatus()).isEqualTo("SUCCESS");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Getting database audit statistics should work correctly")
    void testGetDatabaseAuditStatistics() {
        // Given - Create some audit logs
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "Test Employee 1"), "INSERT INTO employees...", 100L, 1).block();
        databaseAuditService.logUpdateOperation("employees", "1", 
                Map.of("name", "Old Name"), Map.of("name", "New Name"), 
                "UPDATE employees...", 80L, 1).block();
        databaseAuditService.logDeleteOperation("employees", "1", 
                Map.of("name", "Deleted Employee"), "DELETE FROM employees...", 60L, 1).block();

        // When
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = webTestClient.get()
                .uri("/api/v1/audit/database/stats")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertThat(stats).isNotNull();
        if (stats != null) {
            assertThat(stats.get("totalInserts")).isEqualTo(1);
            assertThat(stats.get("totalUpdates")).isEqualTo(1);
            assertThat(stats.get("totalDeletes")).isEqualTo(1);
            assertThat(stats.get("totalSuccess")).isEqualTo(3);
        }
    }

    @Test
    @DisplayName("Querying audit logs by operation type should work correctly")
    void testGetAuditLogsByOperationType() {
        // Given - Create some audit logs
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "Test Employee"), "INSERT INTO employees...", 100L, 1).block();
        databaseAuditService.logUpdateOperation("employees", "1", 
                Map.of("name", "Old Name"), Map.of("name", "New Name"), 
                "UPDATE employees...", 80L, 1).block();

        // When
        webTestClient.get()
                .uri("/api/v1/audit/database/logs/operation/INSERT")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].operationType").isEqualTo("INSERT")
                .jsonPath("$[0].tableName").isEqualTo("employees");
    }

    @Test
    @DisplayName("Querying audit logs by table name should work correctly")
    void testGetAuditLogsByTableName() {
        // Given - Create some audit logs
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "Test Employee"), "INSERT INTO employees...", 100L, 1).block();

        // When
        webTestClient.get()
                .uri("/api/v1/audit/database/logs/table/employees")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].tableName").isEqualTo("employees");
    }

    @Test
    @DisplayName("Querying audit logs by time range should work correctly")
    void testGetAuditLogsByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "Test Employee"), "INSERT INTO employees...", 100L, 1).block();

        // When
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/audit/database/logs/time-range")
                        .queryParam("startTime", startTime.toString())
                        .queryParam("endTime", endTime.toString())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].tableName").isEqualTo("employees");
    }

    @Test
    @DisplayName("Querying recent audit logs should work correctly")
    void testGetRecentAuditLogs() {
        // Given - Create some audit logs
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "Test Employee 1"), "INSERT INTO employees...", 100L, 1).block();
        databaseAuditService.logInsertOperation("employees", "2", 
                Map.of("name", "Test Employee 2"), "INSERT INTO employees...", 100L, 1).block();

        // When
        webTestClient.get()
                .uri("/api/v1/audit/database/logs/recent?limit=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Cleaning up expired audit logs should work correctly")
    void testCleanupOldAuditLogs() {
        // Given - Create some audit logs
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "Test Employee"), "INSERT INTO employees...", 100L, 1).block();

        // When
        webTestClient.delete()
                .uri("/api/v1/audit/database/logs/cleanup?retentionDays=0")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Audit log cleanup completed")
                .jsonPath("$.deletedCount").isNumber();
    }
}

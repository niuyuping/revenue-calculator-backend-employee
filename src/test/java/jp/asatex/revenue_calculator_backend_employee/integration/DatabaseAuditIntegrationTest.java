package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.config.TestConfig;
import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.entity.DatabaseAuditLog;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.repository.DatabaseAuditLogRepository;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import jp.asatex.revenue_calculator_backend_employee.service.DatabaseAuditService;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 数据库审计功能集成测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({TestConfig.class, jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
@DisplayName("数据库审计功能集成测试")
class DatabaseAuditIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DatabaseAuditLogRepository databaseAuditLogRepository;

    @Autowired
    private DatabaseAuditService databaseAuditService;

    @BeforeEach
    void setUp() {
        // 清理数据库
        employeeRepository.deleteAll().block();
        databaseAuditLogRepository.deleteAll().block();
    }

    @Test
    @DisplayName("创建员工时应该记录数据库审计日志")
    void testCreateEmployeeAuditLog() {
        // Given
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("AUDIT001");
        newEmployee.setName("审计测试员工");
        newEmployee.setFurigana("オーディットテストユウイン"); // 使用符合验证规则的片假名
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
        assertThat(createdEmployee.getEmployeeId()).isNotNull();

        // 等待审计日志记录
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证审计日志
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
    @DisplayName("更新员工时应该记录数据库审计日志")
    void testUpdateEmployeeAuditLog() {
        // Given - 先创建一个员工
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("AUDIT002");
        newEmployee.setName("原始员工");
        newEmployee.setFurigana("ゲンシユウイン");
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

        // 等待审计日志记录
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When - 更新员工
        EmployeeDto updatedEmployeeDto = new EmployeeDto();
        updatedEmployeeDto.setEmployeeNumber("AUDIT002");
        updatedEmployeeDto.setName("更新后的员工");
        updatedEmployeeDto.setFurigana("コウシンゴノユウイン");
        updatedEmployeeDto.setBirthday(LocalDate.of(1991, 2, 2));

        EmployeeDto updatedEmployee = webTestClient.put()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updatedEmployeeDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getName()).isEqualTo("更新后的员工");

        // 等待审计日志记录
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证审计日志
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
    @DisplayName("删除员工时应该记录数据库审计日志")
    void testDeleteEmployeeAuditLog() {
        // Given - 先创建一个员工
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("AUDIT003");
        newEmployee.setName("待删除员工");
        newEmployee.setFurigana("サクジョマチユウイン");
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

        // 等待审计日志记录
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When - 删除员工
        webTestClient.delete()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .exchange()
                .expectStatus().isNoContent();

        // 等待审计日志记录
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then - 验证审计日志
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
    @DisplayName("查询员工时应该记录数据库审计日志")
    void testGetEmployeeAuditLog() {
        // Given - 先创建一个员工
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("AUDIT004");
        newEmployee.setName("查询测试员工");
        newEmployee.setFurigana("クエリテストユウイン");
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

        // 等待审计日志记录
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When - 查询员工
        EmployeeDto retrievedEmployee = webTestClient.get()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertThat(retrievedEmployee).isNotNull();
        assertThat(retrievedEmployee.getName()).isEqualTo("查询测试员工");

        // 等待审计日志记录
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证审计日志
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
    @DisplayName("获取数据库审计统计信息应该正确工作")
    void testGetDatabaseAuditStatistics() {
        // Given - 创建一些审计日志
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "测试员工1"), "INSERT INTO employees...", 100L, 1).block();
        databaseAuditService.logUpdateOperation("employees", "1", 
                Map.of("name", "旧名称"), Map.of("name", "新名称"), 
                "UPDATE employees...", 80L, 1).block();
        databaseAuditService.logDeleteOperation("employees", "1", 
                Map.of("name", "被删除员工"), "DELETE FROM employees...", 60L, 1).block();

        // When
        Map<String, Object> stats = webTestClient.get()
                .uri("/api/v1/audit/database/stats")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.get("totalInserts")).isEqualTo(1);
        assertThat(stats.get("totalUpdates")).isEqualTo(1);
        assertThat(stats.get("totalDeletes")).isEqualTo(1);
        assertThat(stats.get("totalSuccess")).isEqualTo(3);
    }

    @Test
    @DisplayName("根据操作类型查询审计日志应该正确工作")
    void testGetAuditLogsByOperationType() {
        // Given - 创建一些审计日志
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "测试员工"), "INSERT INTO employees...", 100L, 1).block();
        databaseAuditService.logUpdateOperation("employees", "1", 
                Map.of("name", "旧名称"), Map.of("name", "新名称"), 
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
    @DisplayName("根据表名查询审计日志应该正确工作")
    void testGetAuditLogsByTableName() {
        // Given - 创建一些审计日志
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "测试员工"), "INSERT INTO employees...", 100L, 1).block();

        // When
        webTestClient.get()
                .uri("/api/v1/audit/database/logs/table/employees")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].tableName").isEqualTo("employees");
    }

    @Test
    @DisplayName("根据时间范围查询审计日志应该正确工作")
    void testGetAuditLogsByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "测试员工"), "INSERT INTO employees...", 100L, 1).block();

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
    @DisplayName("查询最近的审计日志应该正确工作")
    void testGetRecentAuditLogs() {
        // Given - 创建一些审计日志
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "测试员工1"), "INSERT INTO employees...", 100L, 1).block();
        databaseAuditService.logInsertOperation("employees", "2", 
                Map.of("name", "测试员工2"), "INSERT INTO employees...", 100L, 1).block();

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
    @DisplayName("清理过期审计日志应该正确工作")
    void testCleanupOldAuditLogs() {
        // Given - 创建一些审计日志
        databaseAuditService.logInsertOperation("employees", "1", 
                Map.of("name", "测试员工"), "INSERT INTO employees...", 100L, 1).block();

        // When
        webTestClient.delete()
                .uri("/api/v1/audit/database/logs/cleanup?retentionDays=0")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("审计日志清理完成")
                .jsonPath("$.deletedCount").isNumber();
    }
}

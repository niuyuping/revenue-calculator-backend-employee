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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 事务功能集成测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("事务功能集成测试")
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
        // 清理测试数据
        employeeRepository.deleteAll().block();
        
        // 创建测试员工数据
        testEmployee = new EmployeeDto();
        testEmployee.setEmployeeNumber("TEST001");
        testEmployee.setName("测试员工");
        testEmployee.setFurigana("テストユウイン");
        testEmployee.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("创建员工事务应该成功提交")
    void testCreateEmployeeTransactionCommit() {
        // 创建员工
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
        assertThat(createdEmployee.getEmployeeId()).isNotNull();
        assertThat(createdEmployee.getEmployeeNumber()).isEqualTo("TEST001");

        // 验证员工确实被保存到数据库
        assertThat(createdEmployee).isNotNull();
        Employee savedEmployee = employeeRepository.findByEmployeeNumber("TEST001").block();
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("测试员工");
    }

    @Test
    @DisplayName("创建重复员工号应该回滚事务")
    void testCreateDuplicateEmployeeTransactionRollback() {
        // 先创建一个员工
        employeeService.createEmployee(testEmployee).block();

        // 尝试创建重复员工号的员工
        EmployeeDto duplicateEmployee = new EmployeeDto();
        duplicateEmployee.setEmployeeNumber("TEST001"); // 重复的员工号
        duplicateEmployee.setName("重复员工");
        duplicateEmployee.setFurigana("ジュウフクユウイン");
        duplicateEmployee.setBirthday(LocalDate.of(1990, 1, 1));

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(duplicateEmployee))
                .exchange()
                .expectStatus().isEqualTo(409);

        // 验证只有一个员工被保存
        Long count = employeeRepository.count().block();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("更新员工事务应该成功提交")
    void testUpdateEmployeeTransactionCommit() {
        // 先创建员工
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // 更新员工信息
        createdEmployee.setName("更新后的员工");
        createdEmployee.setFurigana("コウシンゴノユウイン");

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
        assertThat(updatedEmployee.getName()).isEqualTo("更新后的员工");
        assertThat(updatedEmployee.getFurigana()).isEqualTo("コウシンゴノユウイン");

        // 验证数据库中的更新
        assertThat(createdEmployee).isNotNull();
        Employee savedEmployee = employeeRepository.findById(createdEmployee.getEmployeeId()).block();
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("更新后的员工");
    }

    @Test
    @DisplayName("删除员工事务应该成功提交")
    void testDeleteEmployeeTransactionCommit() {
        // 先创建员工
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // 删除员工
        webTestClient.delete()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .exchange()
                .expectStatus().isNoContent();

        // 验证员工已被删除
        Employee deletedEmployee = employeeRepository.findById(createdEmployee.getEmployeeId()).block();
        assertThat(deletedEmployee).isNull();
    }

    @Test
    @DisplayName("删除不存在的员工应该回滚事务")
    void testDeleteNonExistentEmployeeTransactionRollback() {
        // 尝试删除不存在的员工
        webTestClient.delete()
                .uri("/api/v1/employee/{id}", 99999L)
                .exchange()
                .expectStatus().isNotFound();

        // 验证数据库状态没有改变
        Long count = employeeRepository.count().block();
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("服务层事务注解应该正确工作")
    void testServiceLayerTransactionAnnotations() {
        // 测试创建员工的事务
        StepVerifier.create(employeeService.createEmployee(testEmployee))
                .assertNext(employee -> {
                    assertThat(employee).isNotNull();
                    assertThat(employee.getEmployeeNumber()).isEqualTo("TEST001");
                })
                .verifyComplete();

        // 验证员工被保存
        Employee savedEmployee = employeeRepository.findByEmployeeNumber("TEST001").block();
        assertThat(savedEmployee).isNotNull();
    }

    @Test
    @DisplayName("事务异常应该被正确处理")
    void testTransactionExceptionHandling() {
        // 创建一个无效的员工数据来触发异常
        EmployeeDto invalidEmployee = new EmployeeDto();
        // 不设置必填字段，应该触发验证异常

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidEmployee))
                .exchange()
                .expectStatus().isBadRequest();

        // 验证没有数据被保存
        Long count = employeeRepository.count().block();
        assertThat(count).isEqualTo(0);
    }
}

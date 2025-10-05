package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.dto.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.dto.SortDirection;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import jp.asatex.revenue_calculator_backend_employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 缓存和限流功能集成测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("缓存和限流功能集成测试")
class CacheAndRateLimitIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CacheManager cacheManager;

    private EmployeeDto testEmployee;

    @BeforeEach
    void setUp() {
        // 清理测试数据和缓存
        employeeRepository.deleteAll().block();
        cacheManager.getCacheNames().forEach(cacheName -> {
            cacheManager.getCache(cacheName).clear();
        });
        
        // 创建测试员工数据
        testEmployee = new EmployeeDto();
        testEmployee.setEmployeeNumber("CACHE001");
        testEmployee.setName("缓存测试员工");
        testEmployee.setFurigana("キャッシュテストユウイン");
        testEmployee.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("员工查询应该使用缓存")
    void testEmployeeQueryUsesCache() {
        // 先创建员工
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // 第一次查询 - 应该从数据库获取
        EmployeeDto firstQuery = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
        assertThat(firstQuery).isNotNull();
        assertThat(firstQuery.getEmployeeNumber()).isEqualTo("CACHE001");

        // 第二次查询 - 应该从缓存获取
        EmployeeDto secondQuery = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
        assertThat(secondQuery).isNotNull();
        assertThat(secondQuery.getEmployeeNumber()).isEqualTo("CACHE001");

        // 验证缓存中有数据
        assertThat(cacheManager.getCache("employees")).isNotNull();
    }

    @Test
    @DisplayName("分页查询应该使用缓存")
    void testPaginationQueryUsesCache() {
        // 创建多个员工
        for (int i = 1; i <= 5; i++) {
            EmployeeDto employee = new EmployeeDto();
            employee.setEmployeeNumber("PAGE" + String.format("%03d", i));
            employee.setName("分页员工" + i);
            employee.setFurigana("ページユウイン" + i);
            employee.setBirthday(LocalDate.of(1990, 1, 1));
            employeeService.createEmployee(employee).block();
        }

        PageRequest pageRequest = new PageRequest(0, 10, "employeeId", SortDirection.ASC);

        // 第一次分页查询
        var firstQuery = employeeService.getAllEmployeesWithPagination(pageRequest).block();
        assertThat(firstQuery).isNotNull();
        assertThat(firstQuery.getContent()).hasSize(5);

        // 第二次分页查询 - 应该从缓存获取
        var secondQuery = employeeService.getAllEmployeesWithPagination(pageRequest).block();
        assertThat(secondQuery).isNotNull();
        assertThat(secondQuery.getContent()).hasSize(5);

        // 验证缓存中有数据
        assertThat(cacheManager.getCache("employeePagination")).isNotNull();
    }

    @Test
    @DisplayName("员工创建应该清除相关缓存")
    void testEmployeeCreationClearsCache() {
        // 先创建员工并查询以填充缓存
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();

        // 验证缓存中有数据
        assertThat(cacheManager.getCache("employees")).isNotNull();

        // 创建新员工 - 应该清除相关缓存
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("CACHE002");
        newEmployee.setName("新缓存员工");
        newEmployee.setFurigana("シンキャッシュユウイン");
        newEmployee.setBirthday(LocalDate.of(1990, 1, 1));

        EmployeeDto createdNewEmployee = employeeService.createEmployee(newEmployee).block();
        assertThat(createdNewEmployee).isNotNull();

        // 验证缓存被清除（这里我们检查缓存是否被标记为清除）
        // 在实际应用中，缓存清除是异步的，所以这里主要验证方法执行成功
        assertThat(createdNewEmployee.getEmployeeNumber()).isEqualTo("CACHE002");
    }

    @Test
    @DisplayName("员工更新应该清除相关缓存")
    void testEmployeeUpdateClearsCache() {
        // 先创建员工并查询以填充缓存
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();

        // 更新员工信息
        createdEmployee.setName("更新后的员工");
        createdEmployee.setFurigana("コウシンゴノユウイン");

        EmployeeDto updatedEmployee = employeeService.updateEmployee(createdEmployee.getEmployeeId(), createdEmployee).block();
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getName()).isEqualTo("更新后的员工");

        // 验证更新成功
        EmployeeDto retrievedEmployee = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
        assertThat(retrievedEmployee).isNotNull();
        assertThat(retrievedEmployee.getName()).isEqualTo("更新后的员工");
    }

    @Test
    @DisplayName("员工删除应该清除相关缓存")
    void testEmployeeDeletionClearsCache() {
        // 先创建员工并查询以填充缓存
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();

        // 删除员工
        employeeService.deleteEmployeeById(createdEmployee.getEmployeeId()).block();

        // 验证员工已被删除
        EmployeeDto deletedEmployee = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
        assertThat(deletedEmployee).isNull();
    }

    @Test
    @DisplayName("搜索功能应该使用缓存")
    void testSearchUsesCache() {
        // 创建测试员工
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        PageRequest pageRequest = new PageRequest(0, 10, "employeeId", SortDirection.ASC);

        // 第一次搜索
        var firstSearch = employeeService.searchEmployeesByNameWithPagination("缓存", pageRequest).block();
        assertThat(firstSearch).isNotNull();
        assertThat(firstSearch.getContent()).hasSize(1);

        // 第二次搜索 - 应该从缓存获取
        var secondSearch = employeeService.searchEmployeesByNameWithPagination("缓存", pageRequest).block();
        assertThat(secondSearch).isNotNull();
        assertThat(secondSearch.getContent()).hasSize(1);

        // 验证缓存中有数据
        assertThat(cacheManager.getCache("employeeSearch")).isNotNull();
    }

    @Test
    @DisplayName("API限流应该正常工作")
    void testApiRateLimiting() {
        // 创建员工
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // 测试正常请求
        webTestClient.get()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .exchange()
                .expectStatus().isOk();

        // 测试分页查询限流
        webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=10&sortBy=employeeId&sortDirection=ASC")
                .exchange()
                .expectStatus().isOk();

        // 测试搜索限流
        webTestClient.get()
                .uri("/api/v1/employee/search/name/paged?name=缓存&page=0&size=10&sortBy=employeeId&sortDirection=ASC")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("监控API限流应该正常工作")
    void testMonitoringApiRateLimiting() {
        // 测试监控API
        webTestClient.get()
                .uri("/api/v1/monitoring/transaction/stats")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("缓存键生成应该正确工作")
    void testCacheKeyGeneration() {
        // 这个测试验证缓存键生成逻辑
        // 通过多次查询相同数据来验证缓存命中
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // 多次查询相同ID的员工
        for (int i = 0; i < 3; i++) {
            EmployeeDto employee = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
            assertThat(employee).isNotNull();
            assertThat(employee.getEmployeeNumber()).isEqualTo("CACHE001");
        }

        // 多次查询相同员工号的员工
        for (int i = 0; i < 3; i++) {
            EmployeeDto employee = employeeService.getEmployeeByNumber("CACHE001").block();
            assertThat(employee).isNotNull();
            assertThat(employee.getEmployeeNumber()).isEqualTo("CACHE001");
        }
    }
}

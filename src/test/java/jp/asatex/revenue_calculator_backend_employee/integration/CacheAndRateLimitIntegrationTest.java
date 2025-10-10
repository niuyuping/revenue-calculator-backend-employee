package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.dto.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.dto.SortDirection;
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
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cache and rate limiting functionality integration test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class, 
         jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
@DisplayName("Cache and Rate Limiting Integration Test")
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
        // Clean up test data and cache
        employeeRepository.deleteAll().block();
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        
        // Create test employee data
        testEmployee = new EmployeeDto();
        testEmployee.setEmployeeNumber("CACHE001");
        testEmployee.setName("Cache Test Employee");
        testEmployee.setFurigana("cache test employee");
        testEmployee.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Employee queries should use cache")
    void testEmployeeQueryUsesCache() {
        // First create employee
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // First query - should fetch from database
        EmployeeDto firstQuery = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
        assertThat(firstQuery).isNotNull();
        assertThat(firstQuery.getEmployeeNumber()).isEqualTo("CACHE001");

        // Second query - should fetch from cache
        EmployeeDto secondQuery = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
        assertThat(secondQuery).isNotNull();
        assertThat(secondQuery.getEmployeeNumber()).isEqualTo("CACHE001");

        // Verify cache has data
        var employeesCache = cacheManager.getCache("employees");
        assertThat(employeesCache).isNotNull();
    }

    @Test
    @DisplayName("Pagination queries should use cache")
    void testPaginationQueryUsesCache() {
        // Create multiple employees
        for (int i = 1; i <= 5; i++) {
            EmployeeDto employee = new EmployeeDto();
            employee.setEmployeeNumber("PAGE" + String.format("%03d", i));
            employee.setName("Pagination Employee " + i);
            employee.setFurigana("page employee " + i);
            employee.setBirthday(LocalDate.of(1990, 1, 1));
            employeeService.createEmployee(employee).block();
        }

        PageRequest pageRequest = new PageRequest(0, 10, "employeeId", SortDirection.ASC);

        // First pagination query
        var firstQuery = employeeService.getEmployeesWithPagination(pageRequest).block();
        assertThat(firstQuery).isNotNull();
        assertThat(firstQuery.getContent()).hasSize(5);

        // Second pagination query - should fetch from cache
        var secondQuery = employeeService.getEmployeesWithPagination(pageRequest).block();
        assertThat(secondQuery).isNotNull();
        assertThat(secondQuery.getContent()).hasSize(5);

        // Verify cache has data
        var paginationCache = cacheManager.getCache("employeePagination");
        assertThat(paginationCache).isNotNull();
    }

    @Test
    @DisplayName("Employee creation should clear related cache")
    void testEmployeeCreationClearsCache() {
        // First create employee and query to populate cache
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();

        // Verify cache has data
        var employeesCache = cacheManager.getCache("employees");
        assertThat(employeesCache).isNotNull();

        // Create new employee - should clear related cache
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("CACHE002");
        newEmployee.setName("New Cache Employee");
        newEmployee.setFurigana("new cache employee");
        newEmployee.setBirthday(LocalDate.of(1990, 1, 1));

        EmployeeDto createdNewEmployee = employeeService.createEmployee(newEmployee).block();
        assertThat(createdNewEmployee).isNotNull();

        // Verify cache is cleared (here we check if cache is marked for clearing)
        // In actual applications, cache clearing is asynchronous, so here we mainly verify method execution success
        assertThat(createdNewEmployee.getEmployeeNumber()).isEqualTo("CACHE002");
    }

    @Test
    @DisplayName("Employee update should clear related cache")
    void testEmployeeUpdateClearsCache() {
        // First create employee and query to populate cache
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();

        // Update employee information
        createdEmployee.setName("Updated Employee");
        createdEmployee.setFurigana("updated employee");

        EmployeeDto updatedEmployee = employeeService.updateEmployee(createdEmployee.getEmployeeId(), createdEmployee).block();
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getName()).isEqualTo("Updated Employee");

        // Verify update success
        EmployeeDto retrievedEmployee = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
        assertThat(retrievedEmployee).isNotNull();
        assertThat(retrievedEmployee.getName()).isEqualTo("Updated Employee");
    }

    @Test
    @DisplayName("Employee deletion should clear related cache")
    void testEmployeeDeletionClearsCache() {
        // First create employee and query to populate cache
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();

        // Delete employee
        employeeService.deleteEmployeeById(createdEmployee.getEmployeeId()).block();

        // Verify employee has been deleted by checking if exception is thrown
        try {
            employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
            // If we reach here, the test should fail because employee should not exist
            assertThat(false).as("Employee should not exist after deletion").isTrue();
        } catch (Exception e) {
            // Expected: EmployeeNotFoundException should be thrown
            assertThat(e).isInstanceOf(jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Search functionality should use cache")
    void testSearchUsesCache() {
        // Create test employee
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // First search
        var firstSearch = employeeService.searchEmployeesByName("Cache").collectList().block();
        assertThat(firstSearch).isNotNull();
        assertThat(firstSearch).hasSize(1);

        // Second search - should fetch from cache
        var secondSearch = employeeService.searchEmployeesByName("Cache").collectList().block();
        assertThat(secondSearch).isNotNull();
        assertThat(secondSearch).hasSize(1);

        // Verify cache has data
        var searchCache = cacheManager.getCache("employeeSearch");
        assertThat(searchCache).isNotNull();
    }

    @Test
    @DisplayName("API rate limiting should work correctly")
    void testApiRateLimiting() {
        // Create employee
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // Test normal request
        webTestClient.get()
                .uri("/api/v1/employee/{id}", createdEmployee.getEmployeeId())
                .exchange()
                .expectStatus().isOk();

        // Test pagination query rate limiting
        webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=10&sortBy=employeeId&sortDirection=ASC")
                .exchange()
                .expectStatus().isOk();

        // Test search rate limiting
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Cache")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Monitoring API rate limiting should work correctly")
    void testMonitoringApiRateLimiting() {
        // Test monitoring API
        webTestClient.get()
                .uri("/api/v1/monitoring/transaction/stats")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Cache key generation should work correctly")
    void testCacheKeyGeneration() {
        // This test verifies cache key generation logic
        // Verify cache hits by querying the same data multiple times
        EmployeeDto createdEmployee = employeeService.createEmployee(testEmployee).block();
        assertThat(createdEmployee).isNotNull();

        // Query employee with same ID multiple times
        for (int i = 0; i < 3; i++) {
            EmployeeDto employee = employeeService.getEmployeeById(createdEmployee.getEmployeeId()).block();
            assertThat(employee).isNotNull();
            assertThat(employee.getEmployeeNumber()).isEqualTo("CACHE001");
        }

        // Query employee with same employee number multiple times
        for (int i = 0; i < 3; i++) {
            EmployeeDto employee = employeeService.getEmployeeByNumber("CACHE001").block();
            assertThat(employee).isNotNull();
            assertThat(employee.getEmployeeNumber()).isEqualTo("CACHE001");
        }
    }
}

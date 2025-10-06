package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundException;
import jp.asatex.revenue_calculator_backend_employee.service.EmployeeService;
import jp.asatex.revenue_calculator_backend_employee.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Employee controller test class
 */
@WebFluxTest({EmployeeController.class, jp.asatex.revenue_calculator_backend_employee.exception.GlobalExceptionHandler.class, jp.asatex.revenue_calculator_backend_employee.config.ValidationConfig.class})
class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private AuditLogService auditLogService;

    @Test
    void testGetAllEmployees() {
        // Prepare test data
        EmployeeDto employee1 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));
        EmployeeDto employee2 = new EmployeeDto(2L, "EMP002", "Sato Hanako", "sato hanako", LocalDate.of(1985, 12, 3));

        when(employeeService.getAllEmployees()).thenReturn(Flux.just(employee1, employee2));

        // Execute test
        webTestClient.get()
                .uri("/api/v1/employee")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .hasSize(2);
    }

    @Test
    void testGetEmployeeById() {
        // Prepare test data
        EmployeeDto employee = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));

        when(employeeService.getEmployeeById(1L)).thenReturn(Mono.just(employee));

        // Execute test
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .isEqualTo(employee);
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        when(employeeService.getEmployeeById(999L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/employee/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateEmployee() {
        // Prepare test data
        EmployeeDto newEmployee = new EmployeeDto(null, "EMP006", "New Employee", "new employee", LocalDate.of(2000, 1, 1));
        EmployeeDto createdEmployee = new EmployeeDto(6L, "EMP006", "New Employee", "new employee", LocalDate.of(2000, 1, 1));

        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(Mono.just(createdEmployee));

        // Execute test
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newEmployee)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .isEqualTo(createdEmployee);
    }

    @Test
    void testUpdateEmployee() {
        // Prepare test data
        EmployeeDto updatedEmployee = new EmployeeDto(1L, "EMP001", "Tanaka Taro (Updated)", "tanaka taro (updated)", LocalDate.of(1990, 5, 15));

        when(employeeService.updateEmployee(anyLong(), any(EmployeeDto.class))).thenReturn(Mono.just(updatedEmployee));

        // Execute test
        webTestClient.put()
                .uri("/api/v1/employee/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEmployee)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .isEqualTo(updatedEmployee);
    }

    @Test
    void testGetEmployeeByNumber() {
        // Prepare test data
        EmployeeDto employee = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));

        when(employeeService.getEmployeeByNumber("EMP001")).thenReturn(Mono.just(employee));

        // Execute test
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP001")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .isEqualTo(employee);
    }


    @Test
    void testDeleteEmployee() {
        when(employeeService.deleteEmployeeById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteEmployeeByNumber() {
        when(employeeService.deleteEmployeeByNumber("EMP001")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/employee/number/EMP001")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testSearchEmployeesByName() {
        // Prepare test data
        EmployeeDto employee1 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));
        EmployeeDto employee2 = new EmployeeDto(2L, "EMP002", "Tanaka Hanako", "tanaka hanako", LocalDate.of(1985, 12, 3));

        when(employeeService.searchEmployeesByName("Tanaka")).thenReturn(Flux.just(employee1, employee2));

        // Execute test
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Tanaka")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .hasSize(2);
    }

    @Test
    void testSearchEmployeesByNameNotFound() {
        when(employeeService.searchEmployeesByName("Yamada")).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Yamada")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .hasSize(0);
    }

    @Test
    void testSearchEmployeesByFurigana() {
        // Prepare test data
        EmployeeDto employee1 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));
        EmployeeDto employee2 = new EmployeeDto(2L, "EMP002", "Tanaka Hanako", "tanaka hanako", LocalDate.of(1985, 12, 3));

        when(employeeService.searchEmployeesByFurigana("tanaka")).thenReturn(Flux.just(employee1, employee2));

        // Execute test
        webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=tanaka")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .hasSize(2);
    }

    @Test
    void testSearchEmployeesByFuriganaNotFound() {
        when(employeeService.searchEmployeesByFurigana("yamada")).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=yamada")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .hasSize(0);
    }

    @Test
    void testGetEmployeeByNumberNotFound() {
        when(employeeService.getEmployeeByNumber("NOTEXIST")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/employee/number/NOTEXIST")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployeeNotFound() {
        EmployeeDto updatedEmployee = new EmployeeDto(999L, "EMP999", "Test Employee", "test employee", LocalDate.of(1990, 5, 15));

        when(employeeService.updateEmployee(anyLong(), any(EmployeeDto.class))).thenReturn(
                Mono.error(new EmployeeNotFoundException("Employee not found, ID: 999"))
        );

        webTestClient.put()
                .uri("/api/v1/employee/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEmployee)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(employeeService.deleteEmployeeById(999L)).thenReturn(Mono.error(new EmployeeNotFoundException("Employee does not exist, ID: 999")));

        webTestClient.delete()
                .uri("/api/v1/employee/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployeeByNumberNotFound() {
        when(employeeService.deleteEmployeeByNumber("NOTEXIST")).thenReturn(Mono.error(new EmployeeNotFoundException("Employee does not exist, number: NOTEXIST")));

        webTestClient.delete()
                .uri("/api/v1/employee/number/NOTEXIST")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testHealthCheck() {
        webTestClient.get()
                .uri("/api/v1/employee/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Employee API is running");
    }
}

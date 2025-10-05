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
 * Employeeコントローラーテストクラス
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
        // テストデータの準備
        EmployeeDto employee1 = new EmployeeDto(1L, "EMP001", "田中太郎", "タナカタロウ", LocalDate.of(1990, 5, 15));
        EmployeeDto employee2 = new EmployeeDto(2L, "EMP002", "佐藤花子", "サトウハナコ", LocalDate.of(1985, 12, 3));

        when(employeeService.getAllEmployees()).thenReturn(Flux.just(employee1, employee2));

        // テストの実行
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
        // テストデータの準備
        EmployeeDto employee = new EmployeeDto(1L, "EMP001", "田中太郎", "タナカタロウ", LocalDate.of(1990, 5, 15));

        when(employeeService.getEmployeeById(1L)).thenReturn(Mono.just(employee));

        // テストの実行
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
        // テストデータの準備
        EmployeeDto newEmployee = new EmployeeDto(null, "EMP006", "新入社員", "シンニュウシャイン", LocalDate.of(2000, 1, 1));
        EmployeeDto createdEmployee = new EmployeeDto(6L, "EMP006", "新入社員", "シンニュウシャイン", LocalDate.of(2000, 1, 1));

        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(Mono.just(createdEmployee));

        // テストの実行
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
        // テストデータの準備
        EmployeeDto updatedEmployee = new EmployeeDto(1L, "EMP001", "田中太郎（更新）", "タナカタロウ（コウシン）", LocalDate.of(1990, 5, 15));

        when(employeeService.updateEmployee(anyLong(), any(EmployeeDto.class))).thenReturn(Mono.just(updatedEmployee));

        // テストの実行
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
        // テストデータの準備
        EmployeeDto employee = new EmployeeDto(1L, "EMP001", "田中太郎", "タナカタロウ", LocalDate.of(1990, 5, 15));

        when(employeeService.getEmployeeByNumber("EMP001")).thenReturn(Mono.just(employee));

        // テストの実行
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
        // テストデータの準備
        EmployeeDto employee1 = new EmployeeDto(1L, "EMP001", "田中太郎", "タナカタロウ", LocalDate.of(1990, 5, 15));
        EmployeeDto employee2 = new EmployeeDto(2L, "EMP002", "田中花子", "タナカハナコ", LocalDate.of(1985, 12, 3));

        when(employeeService.searchEmployeesByName("田中")).thenReturn(Flux.just(employee1, employee2));

        // テストの実行
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=田中")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .hasSize(2);
    }

    @Test
    void testSearchEmployeesByNameNotFound() {
        when(employeeService.searchEmployeesByName("山田")).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=山田")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .hasSize(0);
    }

    @Test
    void testSearchEmployeesByFurigana() {
        // テストデータの準備
        EmployeeDto employee1 = new EmployeeDto(1L, "EMP001", "田中太郎", "タナカタロウ", LocalDate.of(1990, 5, 15));
        EmployeeDto employee2 = new EmployeeDto(2L, "EMP002", "田中花子", "タナカハナコ", LocalDate.of(1985, 12, 3));

        when(employeeService.searchEmployeesByFurigana("タナカ")).thenReturn(Flux.just(employee1, employee2));

        // テストの実行
        webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=タナカ")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .hasSize(2);
    }

    @Test
    void testSearchEmployeesByFuriganaNotFound() {
        when(employeeService.searchEmployeesByFurigana("ヤマダ")).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=ヤマダ")
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
        EmployeeDto updatedEmployee = new EmployeeDto(999L, "EMP999", "存在しない従業員", "ソンザイシナイジュウギョウイン", LocalDate.of(1990, 5, 15));

        when(employeeService.updateEmployee(anyLong(), any(EmployeeDto.class))).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/api/v1/employee/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEmployee)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(employeeService.deleteEmployeeById(999L)).thenReturn(Mono.error(new EmployeeNotFoundException("従業員が存在しません、ID: 999")));

        webTestClient.delete()
                .uri("/api/v1/employee/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployeeByNumberNotFound() {
        when(employeeService.deleteEmployeeByNumber("NOTEXIST")).thenReturn(Mono.error(new EmployeeNotFoundException("従業員が存在しません、番号: NOTEXIST")));

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

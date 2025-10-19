package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.common.PageRequest;
import jp.asatex.revenue_calculator_backend_employee.common.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundHandler;
import jp.asatex.revenue_calculator_backend_employee.application.EmployeeApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    private EmployeeApplicationService employeeApplicationService;


    @Test
    void testGetEmployeesWithPagination() {
        // Prepare test data
        EmployeeDto employee1 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15), "tanaka@example.com", new BigDecimal("350000"), 2, true, true, new BigDecimal("5000"), new BigDecimal("150000"), new BigDecimal("50000"), new BigDecimal("30000"), new BigDecimal("20000"), new BigDecimal("5000"), new BigDecimal("3000"), new BigDecimal("5.00"), new BigDecimal("3.00"), null, null, null, null, null, null, null, null, null, null, null);
        EmployeeDto employee2 = new EmployeeDto(2L, "EMP002", "Sato Hanako", "sato hanako", LocalDate.of(1985, 12, 3), "sato@example.com", new BigDecimal("420000"), 1, true, true, new BigDecimal("6000"), new BigDecimal("180000"), new BigDecimal("60000"), new BigDecimal("35000"), new BigDecimal("25000"), new BigDecimal("6000"), new BigDecimal("3500"), new BigDecimal("6.00"), new BigDecimal("4.00"), null, null, null, null, null, null, null, null, null, null, null);
        List<EmployeeDto> employees = Arrays.asList(employee1, employee2);
        PageResponse<EmployeeDto> pageResponse = new PageResponse<>(employees, 0, 10, 2L, "name", "ASC");

        when(employeeApplicationService.getEmployeesWithPagination(any(PageRequest.class))).thenReturn(Mono.just(pageResponse));

        // Execute test
        webTestClient.get()
                .uri("/api/v1/employee?page=0&size=10&sortBy=name&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PageResponse.class)
                .value(response -> {
                    @SuppressWarnings("unchecked")
                    PageResponse<EmployeeDto> typedResponse = (PageResponse<EmployeeDto>) response;
                    assertThat(typedResponse.getContent()).hasSize(2);
                    assertThat(typedResponse.getPage()).isEqualTo(0);
                    assertThat(typedResponse.getSize()).isEqualTo(10);
                    assertThat(typedResponse.getTotalElements()).isEqualTo(2L);
                });
    }

    @Test
    void testGetEmployeeById() {
        // Prepare test data
        EmployeeDto employee = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15), "tanaka@example.com", new BigDecimal("350000"), 2, true, true, new BigDecimal("5000"), new BigDecimal("150000"), new BigDecimal("50000"), new BigDecimal("30000"), new BigDecimal("20000"), new BigDecimal("5000"), new BigDecimal("3000"), new BigDecimal("5.00"), new BigDecimal("3.00"), null, null, null, null, null, null, null, null, null, null, null);

        when(employeeApplicationService.getEmployeeById(1L)).thenReturn(Mono.just(employee));

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
        when(employeeApplicationService.getEmployeeById(999L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/employee/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateEmployee() {
        // Prepare test data
        EmployeeDto newEmployee = new EmployeeDto(null, "EMP006", "New Employee", "new employee", LocalDate.of(2000, 1, 1), "new@example.com", new BigDecimal("300000"), 0, false, false, new BigDecimal("4000"), new BigDecimal("120000"), new BigDecimal("40000"), new BigDecimal("25000"), new BigDecimal("15000"), new BigDecimal("4000"), new BigDecimal("2500"), new BigDecimal("4.00"), new BigDecimal("2.50"), null, null, null, null, null, null, null, null, null, null, null);
        EmployeeDto createdEmployee = new EmployeeDto(6L, "EMP006", "New Employee", "new employee", LocalDate.of(2000, 1, 1), "new@example.com", new BigDecimal("300000"), 0, false, false, new BigDecimal("4000"), new BigDecimal("120000"), new BigDecimal("40000"), new BigDecimal("25000"), new BigDecimal("15000"), new BigDecimal("4000"), new BigDecimal("2500"), new BigDecimal("4.00"), new BigDecimal("2.50"), null, null, null, null, null, null, null, null, null, null, null);

        when(employeeApplicationService.createEmployee(any(EmployeeDto.class))).thenReturn(Mono.just(createdEmployee));

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
        EmployeeDto updatedEmployee = new EmployeeDto(1L, "EMP001", "Tanaka Taro (Updated)", "tanaka taro (updated)", LocalDate.of(1990, 5, 15), "tanaka.updated@example.com", new BigDecimal("400000"), 3, true, true, new BigDecimal("5500"), new BigDecimal("165000"), new BigDecimal("55000"), new BigDecimal("35000"), new BigDecimal("25000"), new BigDecimal("5500"), new BigDecimal("3500"), new BigDecimal("5.50"), new BigDecimal("3.50"), null, null, null, null, null, null, null, null, null, null, null);

        when(employeeApplicationService.updateEmployee(anyLong(), any(EmployeeDto.class))).thenReturn(Mono.just(updatedEmployee));

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
    void testUpdateEmployeeAllFields() {
        // Prepare updated employee data with all fields changed
        EmployeeDto updatedEmployee = new EmployeeDto(1L, "EMP001", "Tanaka Taro Updated", "tanaka taro updated", LocalDate.of(1991, 6, 16), "tanaka.updated@example.com", new BigDecimal("450000"), 4, false, false, new BigDecimal("6000"), new BigDecimal("180000"), new BigDecimal("60000"), new BigDecimal("40000"), new BigDecimal("30000"), new BigDecimal("6000"), new BigDecimal("4000"), new BigDecimal("6.00"), new BigDecimal("4.00"), null, null, null, null, null, null, null, null, null, null, null);

        when(employeeApplicationService.updateEmployee(anyLong(), any(EmployeeDto.class))).thenReturn(Mono.just(updatedEmployee));

        // Execute test
        webTestClient.put()
                .uri("/api/v1/employee/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEmployee)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .value(response -> {
                    // Verify all fields are updated correctly
                    assertThat(response.getEmployeeId()).isEqualTo(1L);
                    assertThat(response.getEmployeeNumber()).isEqualTo("EMP001");
                    assertThat(response.getName()).isEqualTo("Tanaka Taro Updated");
                    assertThat(response.getFurigana()).isEqualTo("tanaka taro updated");
                    assertThat(response.getBirthday()).isEqualTo(LocalDate.of(1991, 6, 16));
                    assertThat(response.getEmail()).isEqualTo("tanaka.updated@example.com");
                    assertThat(response.getBasicSalary()).isEqualTo(new BigDecimal("450000"));
                    assertThat(response.getDependentCount()).isEqualTo(4);
                    assertThat(response.getHealthInsuranceEnrolled()).isFalse();
                    assertThat(response.getWelfarePensionEnrolled()).isFalse();
                    assertThat(response.getUnitPrice()).isEqualTo(new BigDecimal("6000"));
                    assertThat(response.getIndividualBusinessAmount()).isEqualTo(new BigDecimal("180000"));
                    assertThat(response.getPositionAllowance()).isEqualTo(new BigDecimal("60000"));
                    assertThat(response.getHousingAllowance()).isEqualTo(new BigDecimal("40000"));
                    assertThat(response.getFamilyAllowance()).isEqualTo(new BigDecimal("30000"));
                    assertThat(response.getCollectionFeeAmount()).isEqualTo(new BigDecimal("6000"));
                    assertThat(response.getPaymentFeeAmount()).isEqualTo(new BigDecimal("4000"));
                    assertThat(response.getThirdPartyManagementRate()).isEqualTo(new BigDecimal("6.00"));
                    assertThat(response.getThirdPartyProfitDistributionRate()).isEqualTo(new BigDecimal("4.00"));
                });
    }

    @Test
    void testGetEmployeeByNumber() {
        // Prepare test data
        EmployeeDto employee = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15), "tanaka@example.com", new BigDecimal("350000"), 2, true, true, new BigDecimal("5000"), new BigDecimal("150000"), new BigDecimal("50000"), new BigDecimal("30000"), new BigDecimal("20000"), new BigDecimal("5000"), new BigDecimal("3000"), new BigDecimal("5.00"), new BigDecimal("3.00"), null, null, null, null, null, null, null, null, null, null, null);

        when(employeeApplicationService.getEmployeeByNumber("EMP001")).thenReturn(Mono.just(employee));

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
        when(employeeApplicationService.deleteEmployee(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteEmployeeByNumber() {
        when(employeeApplicationService.deleteEmployeeByNumber("EMP001")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/employee/number/EMP001")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testSearchEmployeesByName() {
        // Prepare test data
        EmployeeDto employee1 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15), "tanaka@example.com", new BigDecimal("350000"), 2, true, true, new BigDecimal("5000"), new BigDecimal("150000"), new BigDecimal("50000"), new BigDecimal("30000"), new BigDecimal("20000"), new BigDecimal("5000"), new BigDecimal("3000"), new BigDecimal("5.00"), new BigDecimal("3.00"), null, null, null, null, null, null, null, null, null, null, null);
        EmployeeDto employee2 = new EmployeeDto(2L, "EMP002", "Tanaka Hanako", "tanaka hanako", LocalDate.of(1985, 12, 3), "hanako@example.com", new BigDecimal("380000"), 1, true, false, new BigDecimal("4500"), new BigDecimal("135000"), new BigDecimal("45000"), new BigDecimal("28000"), new BigDecimal("18000"), new BigDecimal("4500"), new BigDecimal("2800"), new BigDecimal("4.50"), new BigDecimal("2.80"), null, null, null, null, null, null, null, null, null, null, null);

        when(employeeApplicationService.searchEmployeesByName("Tanaka")).thenReturn(Flux.just(employee1, employee2));

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
        when(employeeApplicationService.searchEmployeesByName("Yamada")).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Yamada")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .hasSize(0);
    }


    @Test
    void testGetEmployeeByNumberNotFound() {
        when(employeeApplicationService.getEmployeeByNumber("NOTEXIST")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/employee/number/NOTEXIST")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployeeNotFound() {
        EmployeeDto updatedEmployee = new EmployeeDto(999L, "EMP999", "Test Employee", "test employee", LocalDate.of(1990, 5, 15), "test@example.com", new BigDecimal("300000"), 0, false, false, new BigDecimal("4000"), new BigDecimal("120000"), new BigDecimal("40000"), new BigDecimal("25000"), new BigDecimal("15000"), new BigDecimal("4000"), new BigDecimal("2500"), new BigDecimal("4.00"), new BigDecimal("2.50"), null, null, null, null, null, null, null, null, null, null, null);

        when(employeeApplicationService.updateEmployee(anyLong(), any(EmployeeDto.class))).thenReturn(
                Mono.error(new EmployeeNotFoundHandler("Employee not found, ID: 999"))
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
        when(employeeApplicationService.deleteEmployee(999L)).thenReturn(Mono.error(new EmployeeNotFoundHandler("Employee does not exist, ID: 999")));

        webTestClient.delete()
                .uri("/api/v1/employee/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployeeByNumberNotFound() {
        when(employeeApplicationService.deleteEmployeeByNumber("NOTEXIST")).thenReturn(Mono.error(new EmployeeNotFoundHandler("Employee does not exist, number: NOTEXIST")));

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

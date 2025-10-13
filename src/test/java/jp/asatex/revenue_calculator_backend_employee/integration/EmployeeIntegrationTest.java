package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import jp.asatex.revenue_calculator_backend_employee.common.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Employee integration test
 * Tests complete end-to-end flow
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import({jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class, 
         jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
class EmployeeIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;


    @BeforeEach
    void setUp() {
        // Clean up test data
        employeeRepository.deleteAll().block();
        
    }

    @Test
    void testCompleteEmployeeLifecycle() {
        // 1. Create employee
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("EMP001");
        newEmployee.setName("Tanaka Taro");
        newEmployee.setFurigana("tanaka taro");
        newEmployee.setBirthday(LocalDate.of(1990, 5, 15));

        EmployeeDto createdEmployee = webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newEmployee)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdEmployee).isNotNull();
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        EmployeeDto nonNullCreatedEmployee = Objects.requireNonNull(createdEmployee);
        assertThat(nonNullCreatedEmployee.getEmployeeId()).isNotNull();
        assertThat(nonNullCreatedEmployee.getEmployeeNumber()).isEqualTo("EMP001");
        assertThat(nonNullCreatedEmployee.getName()).isEqualTo("Tanaka Taro");

        // Get employee ID
        Long employeeId = nonNullCreatedEmployee.getEmployeeId();
        assertThat(employeeId).isNotNull();

        // 2. Get employee by ID
        EmployeeDto retrievedEmployee = webTestClient.get()
                .uri("/api/v1/employee/{id}", employeeId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(retrievedEmployee).isNotNull();
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        EmployeeDto nonNullRetrievedEmployee = Objects.requireNonNull(retrievedEmployee);
        assertThat(nonNullRetrievedEmployee.getEmployeeId()).isEqualTo(employeeId);
        assertThat(nonNullRetrievedEmployee.getEmployeeNumber()).isEqualTo("EMP001");

        // 3. Get employee by employee number
        EmployeeDto retrievedByNumber = webTestClient.get()
                .uri("/api/v1/employee/number/{employeeNumber}", "EMP001")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(retrievedByNumber).isNotNull();
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        EmployeeDto nonNullRetrievedByNumber = Objects.requireNonNull(retrievedByNumber);
        assertThat(nonNullRetrievedByNumber.getEmployeeId()).isEqualTo(employeeId);

        // 4. Update employee
        EmployeeDto updatedEmployee = new EmployeeDto();
        updatedEmployee.setEmployeeNumber("EMP001");
        updatedEmployee.setName("Tanaka Taro (Updated)");
        updatedEmployee.setFurigana("tanaka taro (updated)");
        updatedEmployee.setBirthday(LocalDate.of(1990, 5, 15));

        EmployeeDto resultEmployee = webTestClient.put()
                .uri("/api/v1/employee/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEmployee)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(resultEmployee).isNotNull();
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        EmployeeDto nonNullResultEmployee = Objects.requireNonNull(resultEmployee);
        assertThat(nonNullResultEmployee.getName()).isEqualTo("Tanaka Taro (Updated)");
        assertThat(nonNullResultEmployee.getFurigana()).isEqualTo("tanaka taro (updated)");

        // 5. Get all employees
        PageResponse<EmployeeDto> pageResponse = webTestClient.get()
                .uri("/api/v1/employee?size=100")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<PageResponse<EmployeeDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(pageResponse).isNotNull();
        assertThat(Objects.requireNonNull(pageResponse).getContent()).hasSize(1);
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        List<EmployeeDto> nonNullAllEmployees = Objects.requireNonNull(pageResponse.getContent());
        assertThat(nonNullAllEmployees.get(0).getName()).isEqualTo("Tanaka Taro (Updated)");

        // 6. Search employees by name
        List<EmployeeDto> searchResults = webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Tanaka")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(searchResults).hasSize(1);
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        List<EmployeeDto> nonNullSearchResults = Objects.requireNonNull(searchResults);
        assertThat(nonNullSearchResults.get(0).getName()).contains("Tanaka");

        // 7. Search employees by furigana
        List<EmployeeDto> furiganaResults = webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=tanaka")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(furiganaResults).hasSize(1);
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        List<EmployeeDto> nonNullFuriganaResults = Objects.requireNonNull(furiganaResults);
        assertThat(nonNullFuriganaResults.get(0).getFurigana()).contains("tanaka");

        // 8. Delete employee
        webTestClient.delete()
                .uri("/api/v1/employee/{id}", employeeId)
                .exchange()
                .expectStatus().isNoContent();

        // 9. Verify employee has been deleted
        webTestClient.get()
                .uri("/api/v1/employee/{id}", employeeId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateMultipleEmployees() {
        // Create multiple employees
        EmployeeDto employee1 = new EmployeeDto();
        employee1.setEmployeeNumber("EMP001");
        employee1.setName("Tanaka Taro");
        employee1.setFurigana("tanaka taro");
        employee1.setBirthday(LocalDate.of(1990, 5, 15));

        EmployeeDto employee2 = new EmployeeDto();
        employee2.setEmployeeNumber("EMP002");
        employee2.setName("Sato Hanako");
        employee2.setFurigana("sato hanako");
        employee2.setBirthday(LocalDate.of(1985, 8, 20));

        EmployeeDto employee3 = new EmployeeDto();
        employee3.setEmployeeNumber("EMP003");
        employee3.setName("Yamada Jiro");
        employee3.setFurigana("yamada jiro");
        employee3.setBirthday(LocalDate.of(1992, 3, 10));

        // Create employees
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee1)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee2)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee3)
                .exchange()
                .expectStatus().isCreated();

        // Get all employees with pagination
        PageResponse<EmployeeDto> pageResponse = webTestClient.get()
                .uri("/api/v1/employee?size=100") // Get a large page size to get all employees
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponse<EmployeeDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(Objects.requireNonNull(pageResponse).getContent()).hasSize(3);

        // Search test
        List<EmployeeDto> tanakaResults = webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Tanaka")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(tanakaResults).hasSize(1);
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        List<EmployeeDto> nonNullTanakaResults = Objects.requireNonNull(tanakaResults);
        assertThat(nonNullTanakaResults.get(0).getName()).isEqualTo("Tanaka Taro");

        List<EmployeeDto> yamadaResults = webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Yamada")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(yamadaResults).hasSize(1);
        // Use Objects.requireNonNull to ensure object is not null and resolve compiler warnings
        List<EmployeeDto> nonNullYamadaResults = Objects.requireNonNull(yamadaResults);
        assertThat(nonNullYamadaResults.get(0).getName()).isEqualTo("Yamada Jiro");
    }

    @Test
    void testValidationIntegration() {
        // Test invalid employee number format
        EmployeeDto invalidEmployee = new EmployeeDto();
        invalidEmployee.setEmployeeNumber("EMP@001");
        invalidEmployee.setName("Test");
        invalidEmployee.setFurigana("test");

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();

        // Test empty name
        EmployeeDto emptyNameEmployee = new EmployeeDto();
        emptyNameEmployee.setEmployeeNumber("EMP001");
        emptyNameEmployee.setName("");
        emptyNameEmployee.setFurigana("test");

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emptyNameEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();

        // Test future birthday
        EmployeeDto futureBirthdayEmployee = new EmployeeDto();
        futureBirthdayEmployee.setEmployeeNumber("EMP001");
        futureBirthdayEmployee.setName("Test");
        futureBirthdayEmployee.setFurigana("test");
        futureBirthdayEmployee.setBirthday(LocalDate.of(2030, 1, 1));

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(futureBirthdayEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    void testDuplicateEmployeeNumber() {
        // Create first employee
        EmployeeDto employee1 = new EmployeeDto();
        employee1.setEmployeeNumber("EMP001");
        employee1.setName("Tanaka Taro");
        employee1.setFurigana("tanaka taro");
        employee1.setBirthday(LocalDate.of(1990, 5, 15));

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee1)
                .exchange()
                .expectStatus().isCreated();

        // Try to create employee with same employee number
        EmployeeDto employee2 = new EmployeeDto();
        employee2.setEmployeeNumber("EMP001");
        employee2.setName("Sato Hanako");
        employee2.setFurigana("sato hanako");
        employee2.setBirthday(LocalDate.of(1985, 8, 20));

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee2)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Duplicate employee number")
                .jsonPath("$.message").exists();
    }

    @Test
    void testNotFoundScenarios() {
        // Test non-existent employee ID
        webTestClient.get()
                .uri("/api/v1/employee/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        // Test non-existent employee number
        webTestClient.get()
                .uri("/api/v1/employee/number/NOTEXIST")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        // Test updating non-existent employee
        EmployeeDto updateEmployee = new EmployeeDto();
        updateEmployee.setEmployeeNumber("EMP001");
        updateEmployee.setName("Test");
        updateEmployee.setFurigana("test");

        webTestClient.put()
                .uri("/api/v1/employee/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateEmployee)
                .exchange()
                .expectStatus().isNotFound();

        // Test deleting non-existent employee
        webTestClient.delete()
                .uri("/api/v1/employee/999")
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

    @Test
    void testDatabaseConstraints() {
        // Test database constraints
        Employee entity = new Employee();
        entity.setEmployeeNumber("EMP001");
        entity.setName("Tanaka Taro");
        entity.setFurigana("tanaka taro");
        entity.setBirthday(LocalDate.of(1990, 5, 15));

        // Save directly to database
        Mono<Employee> saveResult = employeeRepository.save(entity);
        
        StepVerifier.create(saveResult)
                .assertNext(savedEmployee -> {
                    assertThat(savedEmployee.getEmployeeId()).isNotNull();
                    assertThat(savedEmployee.getEmployeeNumber()).isEqualTo("EMP001");
                    assertThat(savedEmployee.getName()).isEqualTo("Tanaka Taro");
                })
                .verifyComplete();

        // Verify data is saved
        Mono<Employee> findResult = employeeRepository.findByEmployeeNumber("EMP001");
        
        StepVerifier.create(findResult)
                .assertNext(foundEmployee -> {
                    assertThat(foundEmployee.getEmployeeNumber()).isEqualTo("EMP001");
                    assertThat(foundEmployee.getName()).isEqualTo("Tanaka Taro");
                })
                .verifyComplete();
    }
}

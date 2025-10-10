package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.dto.PageResponse;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pagination and sorting functionality integration test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import({jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class, 
         jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
class PaginationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        // Clean up test data
        employeeRepository.deleteAll().block();
        
        // Create test data
        createTestEmployees();
    }

    private void createTestEmployees() {
        // Create multiple employees for pagination testing
        for (int i = 1; i <= 25; i++) {
            Employee employee = new Employee();
            employee.setEmployeeNumber("EMP" + String.format("%03d", i));
            employee.setName("Employee " + i);
            employee.setFurigana("employee " + i);
            employee.setBirthday(LocalDate.of(1990 + (i % 10), 1, 1));
            employee.setDeleted(false);
            
            employeeRepository.save(employee).block();
        }
    }

    @Test
    void testGetAllEmployeesWithPagination() {
        // Test first page
        PageResponse<EmployeeDto> firstPage = webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=10&sortBy=employeeNumber&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new org.springframework.core.ParameterizedTypeReference<PageResponse<EmployeeDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(firstPage).isNotNull();
        if (firstPage != null) {
            assertThat(firstPage.getContent()).isNotNull().hasSize(10);
            assertThat(firstPage.getPage()).isEqualTo(0);
            assertThat(firstPage.getSize()).isEqualTo(10);
            assertThat(firstPage.getTotalElements()).isEqualTo(25);
            assertThat(firstPage.getTotalPages()).isEqualTo(3);
            assertThat(firstPage.isFirst()).isTrue();
            assertThat(firstPage.isLast()).isFalse();
            assertThat(firstPage.getSortBy()).isEqualTo("employeeNumber");
            assertThat(firstPage.getSortDirection()).isEqualTo("ASC");
        }

        // Test second page
        PageResponse<EmployeeDto> secondPage = webTestClient.get()
                .uri("/api/v1/employee/paged?page=1&size=10&sortBy=employeeNumber&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new org.springframework.core.ParameterizedTypeReference<PageResponse<EmployeeDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(secondPage).isNotNull();
        if (secondPage != null) {
            assertThat(secondPage.getContent()).isNotNull().hasSize(10);
            assertThat(secondPage.getPage()).isEqualTo(1);
            assertThat(secondPage.isFirst()).isFalse();
            assertThat(secondPage.isLast()).isFalse();
        }

        // Test last page
        PageResponse<EmployeeDto> lastPage = webTestClient.get()
                .uri("/api/v1/employee/paged?page=2&size=10&sortBy=employeeNumber&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new org.springframework.core.ParameterizedTypeReference<PageResponse<EmployeeDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(lastPage).isNotNull();
        if (lastPage != null) {
            assertThat(lastPage.getContent()).isNotNull().hasSize(5);
            assertThat(lastPage.getPage()).isEqualTo(2);
            assertThat(lastPage.isFirst()).isFalse();
            assertThat(lastPage.isLast()).isTrue();
        }
    }

    @Test
    void testPaginationWithDifferentSorting() {
        // Test sorting by name in descending order
        PageResponse<EmployeeDto> response = webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=5&sortBy=name&sortDirection=DESC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new org.springframework.core.ParameterizedTypeReference<PageResponse<EmployeeDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        if (response != null) {
            assertThat(response.getContent()).isNotNull().hasSize(5);
            assertThat(response.getSortBy()).isEqualTo("name");
            assertThat(response.getSortDirection()).isEqualTo("DESC");
        }
    }

    @Test
    void testSearchByNameWithPagination() {
        // Create some employees with specific keywords
        Employee employee1 = new Employee();
        employee1.setEmployeeNumber("TANAKA001");
        employee1.setName("Tanaka Taro");
        employee1.setFurigana("tanaka taro");
        employee1.setBirthday(LocalDate.of(1990, 1, 1));
        employee1.setDeleted(false);
        employeeRepository.save(employee1).block();

        Employee employee2 = new Employee();
        employee2.setEmployeeNumber("TANAKA002");
        employee2.setName("Tanaka Hanako");
        employee2.setFurigana("tanaka hanako");
        employee2.setBirthday(LocalDate.of(1992, 1, 1));
        employee2.setDeleted(false);
        employeeRepository.save(employee2).block();

        // Test search by name (without pagination)
        java.util.List<EmployeeDto> response = webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Tanaka")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        if (response != null) {
            assertThat(response).hasSize(2);
            assertThat(response.get(0).getName()).contains("Tanaka");
            assertThat(response.get(1).getName()).contains("Tanaka");
        }
    }

    @Test
    void testSearchByFuriganaWithPagination() {
        // Create some employees with specific furigana
        Employee employee1 = new Employee();
        employee1.setEmployeeNumber("TANAKA001");
        employee1.setName("Tanaka Taro");
        employee1.setFurigana("tanaka taro");
        employee1.setBirthday(LocalDate.of(1990, 1, 1));
        employee1.setDeleted(false);
        employeeRepository.save(employee1).block();

        // Test search by furigana (without pagination)
        java.util.List<EmployeeDto> response = webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=tanaka")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        if (response != null) {
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getFurigana()).contains("tanaka");
        }
    }

    @Test
    void testPaginationWithInvalidParameters() {
        // Test invalid page number
        webTestClient.get()
                .uri("/api/v1/employee/paged?page=-1&size=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

        // Test invalid page size
        webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

        // Test oversized page size
        webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=101")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPaginationWithDefaultParameters() {
        // Test default parameters
        PageResponse<EmployeeDto> response = webTestClient.get()
                .uri("/api/v1/employee/paged")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new org.springframework.core.ParameterizedTypeReference<PageResponse<EmployeeDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        if (response != null) {
            assertThat(response.getPage()).isEqualTo(0);
            assertThat(response.getSize()).isEqualTo(10);
            assertThat(response.getSortBy()).isEqualTo("employeeId");
            assertThat(response.getSortDirection()).isEqualTo("ASC");
        }
    }
}

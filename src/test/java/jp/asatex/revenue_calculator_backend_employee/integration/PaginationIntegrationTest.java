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
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 分页和排序功能集成测试
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
        // 清理测试数据
        employeeRepository.deleteAll().block();
        
        // 创建测试数据
        createTestEmployees();
    }

    private void createTestEmployees() {
        // 创建多个员工用于测试分页
        for (int i = 1; i <= 25; i++) {
            Employee employee = new Employee();
            employee.setEmployeeNumber("EMP" + String.format("%03d", i));
            employee.setName("员工" + i);
            employee.setFurigana("ユウイン" + i);
            employee.setBirthday(LocalDate.of(1990 + (i % 10), 1, 1));
            employee.setDeleted(false);
            
            employeeRepository.save(employee).block();
        }
    }

    @Test
    void testGetAllEmployeesWithPagination() {
        // 测试第一页
        PageResponse<EmployeeDto> firstPage = webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=10&sortBy=employeeNumber&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(firstPage).isNotNull();
        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(firstPage.getPage()).isEqualTo(0);
        assertThat(firstPage.getSize()).isEqualTo(10);
        assertThat(firstPage.getTotalElements()).isEqualTo(25);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.isLast()).isFalse();
        assertThat(firstPage.getSortBy()).isEqualTo("employee_id");
        assertThat(firstPage.getSortDirection()).isEqualTo("ASC");

        // 测试第二页
        PageResponse<EmployeeDto> secondPage = webTestClient.get()
                .uri("/api/v1/employee/paged?page=1&size=10&sortBy=employeeNumber&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(secondPage).isNotNull();
        assertThat(secondPage.getContent()).hasSize(10);
        assertThat(secondPage.getPage()).isEqualTo(1);
        assertThat(secondPage.isFirst()).isFalse();
        assertThat(secondPage.isLast()).isFalse();

        // 测试最后一页
        PageResponse<EmployeeDto> lastPage = webTestClient.get()
                .uri("/api/v1/employee/paged?page=2&size=10&sortBy=employeeNumber&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(lastPage).isNotNull();
        assertThat(lastPage.getContent()).hasSize(5);
        assertThat(lastPage.getPage()).isEqualTo(2);
        assertThat(lastPage.isFirst()).isFalse();
        assertThat(lastPage.isLast()).isTrue();
    }

    @Test
    void testPaginationWithDifferentSorting() {
        // 测试按姓名降序排序
        PageResponse<EmployeeDto> response = webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=5&sortBy=name&sortDirection=DESC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(5);
        assertThat(response.getSortBy()).isEqualTo("name");
        assertThat(response.getSortDirection()).isEqualTo("DESC");
    }

    @Test
    void testSearchByNameWithPagination() {
        // 创建一些包含特定关键词的员工
        Employee employee1 = new Employee();
        employee1.setEmployeeNumber("TANAKA001");
        employee1.setName("田中太郎");
        employee1.setFurigana("タナカタロウ");
        employee1.setBirthday(LocalDate.of(1990, 1, 1));
        employee1.setDeleted(false);
        employeeRepository.save(employee1).block();

        Employee employee2 = new Employee();
        employee2.setEmployeeNumber("TANAKA002");
        employee2.setName("田中花子");
        employee2.setFurigana("タナカハナコ");
        employee2.setBirthday(LocalDate.of(1992, 1, 1));
        employee2.setDeleted(false);
        employeeRepository.save(employee2).block();

        // 测试按姓名搜索分页
        PageResponse<EmployeeDto> response = webTestClient.get()
                .uri("/api/v1/employee/search/name/paged?name=田中&page=0&size=10&sortBy=name&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
    }

    @Test
    void testSearchByFuriganaWithPagination() {
        // 创建一些包含特定ふりがな的员工
        Employee employee1 = new Employee();
        employee1.setEmployeeNumber("TANAKA001");
        employee1.setName("田中太郎");
        employee1.setFurigana("タナカタロウ");
        employee1.setBirthday(LocalDate.of(1990, 1, 1));
        employee1.setDeleted(false);
        employeeRepository.save(employee1).block();

        // 测试按ふりがな搜索分页
        PageResponse<EmployeeDto> response = webTestClient.get()
                .uri("/api/v1/employee/search/furigana/paged?furigana=タナカ&page=0&size=10&sortBy=name&sortDirection=ASC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testPaginationWithInvalidParameters() {
        // 测试无效的页码
        webTestClient.get()
                .uri("/api/v1/employee/paged?page=-1&size=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

        // 测试无效的每页大小
        webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();

        // 测试过大的每页大小
        webTestClient.get()
                .uri("/api/v1/employee/paged?page=0&size=101")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testPaginationWithDefaultParameters() {
        // 测试默认参数
        PageResponse<EmployeeDto> response = webTestClient.get()
                .uri("/api/v1/employee/paged")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getSortBy()).isEqualTo("employee_id");
        assertThat(response.getSortDirection()).isEqualTo("ASC");
    }
}

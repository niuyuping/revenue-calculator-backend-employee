package jp.asatex.revenue_calculator_backend_employee.integration;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
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
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Employee統合テスト
 * エンドツーエンドの完全なフローをテスト
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
        // テストデータのクリーンアップ
        employeeRepository.deleteAll().block();
    }

    @Test
    void testCompleteEmployeeLifecycle() {
        // 1. 従業員の作成
        EmployeeDto newEmployee = new EmployeeDto();
        newEmployee.setEmployeeNumber("EMP001");
        newEmployee.setName("田中太郎");
        newEmployee.setFurigana("タナカタロウ");
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
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        EmployeeDto nonNullCreatedEmployee = Objects.requireNonNull(createdEmployee);
        assertThat(nonNullCreatedEmployee.getEmployeeId()).isNotNull();
        assertThat(nonNullCreatedEmployee.getEmployeeNumber()).isEqualTo("EMP001");
        assertThat(nonNullCreatedEmployee.getName()).isEqualTo("田中太郎");

        // 従業員IDの取得
        Long employeeId = nonNullCreatedEmployee.getEmployeeId();
        assertThat(employeeId).isNotNull();

        // 2. IDによる従業員の取得
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
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        EmployeeDto nonNullRetrievedEmployee = Objects.requireNonNull(retrievedEmployee);
        assertThat(nonNullRetrievedEmployee.getEmployeeId()).isEqualTo(employeeId);
        assertThat(nonNullRetrievedEmployee.getEmployeeNumber()).isEqualTo("EMP001");

        // 3. 従業員番号による従業員の取得
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
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        EmployeeDto nonNullRetrievedByNumber = Objects.requireNonNull(retrievedByNumber);
        assertThat(nonNullRetrievedByNumber.getEmployeeId()).isEqualTo(employeeId);

        // 4. 従業員の更新
        EmployeeDto updatedEmployee = new EmployeeDto();
        updatedEmployee.setEmployeeNumber("EMP001");
        updatedEmployee.setName("田中太郎（更新）");
        updatedEmployee.setFurigana("タナカタロウ（コウシン）");
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
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        EmployeeDto nonNullResultEmployee = Objects.requireNonNull(resultEmployee);
        assertThat(nonNullResultEmployee.getName()).isEqualTo("田中太郎（更新）");
        assertThat(nonNullResultEmployee.getFurigana()).isEqualTo("タナカタロウ（コウシン）");

        // 5. 全従業員の取得
        List<EmployeeDto> allEmployees = webTestClient.get()
                .uri("/api/v1/employee")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(allEmployees).hasSize(1);
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        List<EmployeeDto> nonNullAllEmployees = Objects.requireNonNull(allEmployees);
        assertThat(nonNullAllEmployees.get(0).getName()).isEqualTo("田中太郎（更新）");

        // 6. 姓名による従業員検索
        List<EmployeeDto> searchResults = webTestClient.get()
                .uri("/api/v1/employee/search/name?name=田中")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(searchResults).hasSize(1);
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        List<EmployeeDto> nonNullSearchResults = Objects.requireNonNull(searchResults);
        assertThat(nonNullSearchResults.get(0).getName()).contains("田中");

        // 7. ふりがなによる従業員検索
        List<EmployeeDto> furiganaResults = webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=タナカ")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(furiganaResults).hasSize(1);
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        List<EmployeeDto> nonNullFuriganaResults = Objects.requireNonNull(furiganaResults);
        assertThat(nonNullFuriganaResults.get(0).getFurigana()).contains("タナカ");

        // 8. 従業員の削除
        webTestClient.delete()
                .uri("/api/v1/employee/{id}", employeeId)
                .exchange()
                .expectStatus().isNoContent();

        // 9. 従業員が削除されたことを確認
        webTestClient.get()
                .uri("/api/v1/employee/{id}", employeeId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateMultipleEmployees() {
        // 複数の従業員の作成
        EmployeeDto employee1 = new EmployeeDto();
        employee1.setEmployeeNumber("EMP001");
        employee1.setName("田中太郎");
        employee1.setFurigana("タナカタロウ");
        employee1.setBirthday(LocalDate.of(1990, 5, 15));

        EmployeeDto employee2 = new EmployeeDto();
        employee2.setEmployeeNumber("EMP002");
        employee2.setName("佐藤花子");
        employee2.setFurigana("サトウハナコ");
        employee2.setBirthday(LocalDate.of(1985, 8, 20));

        EmployeeDto employee3 = new EmployeeDto();
        employee3.setEmployeeNumber("EMP003");
        employee3.setName("山田次郎");
        employee3.setFurigana("ヤマダジロウ");
        employee3.setBirthday(LocalDate.of(1992, 3, 10));

        // 従業員の作成
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

        // 全従業員の取得
        List<EmployeeDto> allEmployees = webTestClient.get()
                .uri("/api/v1/employee")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(allEmployees).hasSize(3);

        // 検索テスト
        List<EmployeeDto> tanakaResults = webTestClient.get()
                .uri("/api/v1/employee/search/name?name=田中")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(tanakaResults).hasSize(1);
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        List<EmployeeDto> nonNullTanakaResults = Objects.requireNonNull(tanakaResults);
        assertThat(nonNullTanakaResults.get(0).getName()).isEqualTo("田中太郎");

        List<EmployeeDto> yamadaResults = webTestClient.get()
                .uri("/api/v1/employee/search/name?name=山田")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(yamadaResults).hasSize(1);
        // Objects.requireNonNullを使用してオブジェクトがnullでないことを保証し、コンパイラ警告を解消
        List<EmployeeDto> nonNullYamadaResults = Objects.requireNonNull(yamadaResults);
        assertThat(nonNullYamadaResults.get(0).getName()).isEqualTo("山田次郎");
    }

    @Test
    void testValidationIntegration() {
        // 無効な従業員番号フォーマットのテスト
        EmployeeDto invalidEmployee = new EmployeeDto();
        invalidEmployee.setEmployeeNumber("EMP@001");
        invalidEmployee.setName("テスト");
        invalidEmployee.setFurigana("テスト");

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();

        // 空の姓名のテスト
        EmployeeDto emptyNameEmployee = new EmployeeDto();
        emptyNameEmployee.setEmployeeNumber("EMP001");
        emptyNameEmployee.setName("");
        emptyNameEmployee.setFurigana("テスト");

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emptyNameEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();

        // 未来の生年月日のテスト
        EmployeeDto futureBirthdayEmployee = new EmployeeDto();
        futureBirthdayEmployee.setEmployeeNumber("EMP001");
        futureBirthdayEmployee.setName("テスト");
        futureBirthdayEmployee.setFurigana("テスト");
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
        // 最初の従業員の作成
        EmployeeDto employee1 = new EmployeeDto();
        employee1.setEmployeeNumber("EMP001");
        employee1.setName("田中太郎");
        employee1.setFurigana("タナカタロウ");
        employee1.setBirthday(LocalDate.of(1990, 5, 15));

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee1)
                .exchange()
                .expectStatus().isCreated();

        // 同じ従業員番号の従業員の作成を試行
        EmployeeDto employee2 = new EmployeeDto();
        employee2.setEmployeeNumber("EMP001");
        employee2.setName("佐藤花子");
        employee2.setFurigana("サトウハナコ");
        employee2.setBirthday(LocalDate.of(1985, 8, 20));

        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee2)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("従業員番号重複")
                .jsonPath("$.message").exists();
    }

    @Test
    void testNotFoundScenarios() {
        // 存在しない従業員IDのテスト
        webTestClient.get()
                .uri("/api/v1/employee/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        // 存在しない従業員番号のテスト
        webTestClient.get()
                .uri("/api/v1/employee/number/NOTEXIST")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        // 存在しない従業員の更新テスト
        EmployeeDto updateEmployee = new EmployeeDto();
        updateEmployee.setEmployeeNumber("EMP001");
        updateEmployee.setName("テスト");
        updateEmployee.setFurigana("テスト");

        webTestClient.put()
                .uri("/api/v1/employee/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateEmployee)
                .exchange()
                .expectStatus().isNotFound();

        // 存在しない従業員の削除テスト
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
        // データベース制約のテスト
        Employee entity = new Employee();
        entity.setEmployeeNumber("EMP001");
        entity.setName("田中太郎");
        entity.setFurigana("タナカタロウ");
        entity.setBirthday(LocalDate.of(1990, 5, 15));

        // データベースに直接保存
        Mono<Employee> saveResult = employeeRepository.save(entity);
        
        StepVerifier.create(saveResult)
                .assertNext(savedEmployee -> {
                    assertThat(savedEmployee.getEmployeeId()).isNotNull();
                    assertThat(savedEmployee.getEmployeeNumber()).isEqualTo("EMP001");
                    assertThat(savedEmployee.getName()).isEqualTo("田中太郎");
                })
                .verifyComplete();

        // データが保存されたことを確認
        Mono<Employee> findResult = employeeRepository.findByEmployeeNumber("EMP001");
        
        StepVerifier.create(findResult)
                .assertNext(foundEmployee -> {
                    assertThat(foundEmployee.getEmployeeNumber()).isEqualTo("EMP001");
                    assertThat(foundEmployee.getName()).isEqualTo("田中太郎");
                })
                .verifyComplete();
    }
}

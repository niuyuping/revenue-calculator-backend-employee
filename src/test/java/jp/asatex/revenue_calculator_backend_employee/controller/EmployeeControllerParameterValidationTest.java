package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * EmployeeController参数验证测试
 * 测试路径参数和查询参数的验证
 */
@WebFluxTest({EmployeeController.class, jp.asatex.revenue_calculator_backend_employee.exception.GlobalExceptionHandler.class, jp.asatex.revenue_calculator_backend_employee.config.ValidationConfig.class})
class EmployeeControllerParameterValidationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void testGetEmployeeById_WithInvalidId_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/0")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testGetEmployeeById_WithNegativeId_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/-1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testGetEmployeeByNumber_WithEmptyNumber_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/number/ ")
                .exchange()
                .expectStatus().isBadRequest() // 路径匹配但参数验证失败，返回400
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testGetEmployeeByNumber_WithInvalidFormat_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP@001")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testGetEmployeeByNumber_WithTooLongNumber_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP001234567890123456789")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testGetEmployeeByNumber_WithSpecialCharacters_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP 001")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testSearchEmployeesByName_WithEmptyName_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testSearchEmployeesByName_WithTooLongName_ShouldReturnBadRequest() {
        String longName = "a".repeat(101);
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=" + longName)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testSearchEmployeesByName_WithMissingParameter_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/search/name")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("パラメータ検証失敗")
                .jsonPath("$.message").isEqualTo("必須のクエリパラメータが不足しています");
    }

    @Test
    void testSearchEmployeesByFurigana_WithEmptyFurigana_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testSearchEmployeesByFurigana_WithTooLongFurigana_ShouldReturnBadRequest() {
        String longFurigana = "あ".repeat(201);
        webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=" + longFurigana)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testSearchEmployeesByFurigana_WithMissingParameter_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/search/furigana")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("パラメータ検証失敗")
                .jsonPath("$.message").isEqualTo("必須のクエリパラメータが不足しています");
    }

    @Test
    void testUpdateEmployee_WithInvalidId_ShouldReturnBadRequest() {
        String requestBody = "{\"employeeNumber\":\"EMP001\",\"name\":\"测试\",\"furigana\":\"テスト\"}";
        
        webTestClient.put()
                .uri("/api/v1/employee/0")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testDeleteEmployeeById_WithInvalidId_ShouldReturnBadRequest() {
        webTestClient.delete()
                .uri("/api/v1/employee/0")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testDeleteEmployeeByNumber_WithInvalidFormat_ShouldReturnBadRequest() {
        webTestClient.delete()
                .uri("/api/v1/employee/number/EMP@001")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testDeleteEmployeeByNumber_WithTooLongNumber_ShouldReturnBadRequest() {
        webTestClient.delete()
                .uri("/api/v1/employee/number/EMP001234567890123456789")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    void testValidEmployeeNumber_ShouldPassValidation() {
        // Mock service to return empty Mono (employee not found)
        when(employeeService.getEmployeeByNumber(any())).thenReturn(Mono.empty());
        
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP001")
                .exchange()
                .expectStatus().isNotFound(); // 员工不存在，但验证通过
    }

    @Test
    void testValidSearchParameters_ShouldPassValidation() {
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=田中")
                .exchange()
                .expectStatus().isOk(); // 验证通过，返回空结果
    }

    @Test
    void testValidFuriganaSearch_ShouldPassValidation() {
        webTestClient.get()
                .uri("/api/v1/employee/search/furigana?furigana=タナカ")
                .exchange()
                .expectStatus().isOk(); // 验证通过，返回空结果
    }

    @Test
    void testValidId_ShouldPassValidation() {
        // Mock service to return empty Mono (employee not found)
        when(employeeService.getEmployeeById(any())).thenReturn(Mono.empty());
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isNotFound(); // 员工不存在，但验证通过
    }
}

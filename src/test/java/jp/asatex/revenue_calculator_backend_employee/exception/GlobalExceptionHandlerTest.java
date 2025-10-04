package jp.asatex.revenue_calculator_backend_employee.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import jp.asatex.revenue_calculator_backend_employee.service.EmployeeService;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * グローバル例外ハンドラーテスト
 * 各種例外の処理をテスト
 */
@WebFluxTest
public class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    public void testHandleEmployeeNotFoundException() {
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new EmployeeNotFoundException("従業員が存在しません、ID: 999"))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("従業員未発見")
                .jsonPath("$.message").isEqualTo("従業員が存在しません、ID: 999");
    }

    @Test
    public void testHandleDuplicateEmployeeNumberException() {
        when(employeeService.createEmployee(any())).thenReturn(
                Mono.error(new DuplicateEmployeeNumberException("従業員番号が既に存在します: EMP001"))
        );
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue("{\"employeeNumber\":\"EMP001\",\"name\":\"テスト\",\"furigana\":\"テスト\"}")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("従業員番号重複")
                .jsonPath("$.message").isEqualTo("従業員番号が既に存在します: EMP001");
    }

    @Test
    public void testHandleValidationException_InvalidFurigana() {
        // 無効な文字を含むふりがなのテスト
        String invalidEmployee = "{\"employeeNumber\":\"EMP001\",\"name\":\"テスト\",\"furigana\":\"テスト@#$%\"}";
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleValidationException_EmptyName() {
        String invalidEmployee = "{\"employeeNumber\":\"EMP001\",\"name\":\"\",\"furigana\":\"テスト\"}";
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleValidationException_TooLongEmployeeNumber() {
        String tooLongNumber = "A".repeat(21);
        String invalidEmployee = String.format("{\"employeeNumber\":\"%s\",\"name\":\"テスト\",\"furigana\":\"テスト\"}", tooLongNumber);
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleValidationException_InvalidEmployeeNumberFormat() {
        String invalidEmployee = "{\"employeeNumber\":\"EMP@001\",\"name\":\"テスト\",\"furigana\":\"テスト\"}";
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleValidationException_EmptyEmployeeNumber() {
        String invalidEmployee = "{\"employeeNumber\":\"\",\"name\":\"テスト\",\"furigana\":\"テスト\"}";
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleValidationException_TooLongName() {
        String tooLongName = "A".repeat(101);
        String invalidEmployee = String.format("{\"employeeNumber\":\"EMP001\",\"name\":\"%s\",\"furigana\":\"テスト\"}", tooLongName);
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleValidationException_TooLongFurigana() {
        String tooLongFurigana = "あ".repeat(201);
        String invalidEmployee = String.format("{\"employeeNumber\":\"EMP001\",\"name\":\"テスト\",\"furigana\":\"%s\"}", tooLongFurigana);
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleValidationException_FutureBirthday() {
        String invalidEmployee = "{\"employeeNumber\":\"EMP001\",\"name\":\"テスト\",\"furigana\":\"テスト\",\"birthday\":\"2030-01-01\"}";
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleValidationException_MissingRequiredFields() {
        String invalidEmployee = "{\"employeeNumber\":\"EMP001\"}";
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.details").exists();
    }

    @Test
    public void testHandleConstraintViolationException() {
        // ConstraintViolationExceptionのシミュレート
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new jakarta.validation.ConstraintViolationException("パラメータ検証失敗", null))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("パラメータ検証失敗");
    }

    @Test
    public void testHandleGenericException() {
        // 汎用例外のシミュレート
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new RuntimeException("データベース接続失敗"))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.error").isEqualTo("サーバー内部エラー")
                .jsonPath("$.message").isEqualTo("データベース接続失敗");
    }

    @Test
    public void testHandleGenericException_WithValidationFailure() {
        // "Validation failure"を含む例外のシミュレート
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new RuntimeException("Validation failure: Invalid data"))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    public void testHandleGenericException_WithConstraintViolationCause() {
        // ConstraintViolationExceptionをcauseとして含む例外のシミュレート
        jakarta.validation.ConstraintViolationException cve = 
                new jakarta.validation.ConstraintViolationException("パラメータ検証失敗", null);
        RuntimeException exception = new RuntimeException("外層例外", cve);
        
        when(employeeService.getEmployeeById(any())).thenReturn(Mono.error(exception));
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("検証失敗")
                .jsonPath("$.message").isEqualTo("リクエストデータが検証ルールに適合しません");
    }

    @Test
    public void testHandleGenericException_WithNullMessage() {
        // メッセージのない例外のシミュレート
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new RuntimeException())
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.error").isEqualTo("サーバー内部エラー")
                .jsonPath("$.message").isEqualTo("不明なエラー");
    }

    @Test
    public void testHandleUpdateEmployee_WithDuplicateNumber() {
        when(employeeService.updateEmployee(any(), any())).thenReturn(
                Mono.error(new DuplicateEmployeeNumberException("従業員番号が既に存在します: EMP002"))
        );
        
        String updateEmployee = "{\"employeeNumber\":\"EMP002\",\"name\":\"テスト\",\"furigana\":\"テスト\"}";
        
        webTestClient.put()
                .uri("/api/v1/employee/1")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updateEmployee)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("従業員番号重複")
                .jsonPath("$.message").isEqualTo("従業員番号が既に存在します: EMP002");
    }

    @Test
    public void testHandleDeleteEmployee_WithNotFoundException() {
        when(employeeService.deleteEmployeeById(any())).thenReturn(
                Mono.error(new EmployeeNotFoundException("従業員が存在しません、ID: 999"))
        );
        
        webTestClient.delete()
                .uri("/api/v1/employee/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("従業員未発見")
                .jsonPath("$.message").isEqualTo("従業員が存在しません、ID: 999");
    }
}

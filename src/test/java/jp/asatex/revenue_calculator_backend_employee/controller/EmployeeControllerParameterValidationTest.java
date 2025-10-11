package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.application.EmployeeApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * EmployeeController parameter validation test
 * Tests path parameter and query parameter validation
 */
@WebFluxTest({EmployeeController.class, jp.asatex.revenue_calculator_backend_employee.exception.GlobalExceptionHandler.class, jp.asatex.revenue_calculator_backend_employee.config.ValidationConfig.class})
class EmployeeControllerParameterValidationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private EmployeeApplicationService employeeApplicationService;


    @Test
    void testGetEmployeeById_WithInvalidId_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/0")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testGetEmployeeById_WithNegativeId_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/-1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testGetEmployeeByNumber_WithEmptyNumber_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/number/ ")
                .exchange()
                .expectStatus().isBadRequest() // Path matches but parameter validation fails, returns 400
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testGetEmployeeByNumber_WithInvalidFormat_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP@001")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testGetEmployeeByNumber_WithTooLongNumber_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP001234567890123456789")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testGetEmployeeByNumber_WithSpecialCharacters_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP 001")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testSearchEmployeesByName_WithEmptyName_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testSearchEmployeesByName_WithTooLongName_ShouldReturnBadRequest() {
        String longName = "a".repeat(101);
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=" + longName)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testSearchEmployeesByName_WithMissingParameter_ShouldReturnBadRequest() {
        webTestClient.get()
                .uri("/api/v1/employee/search/name")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Parameter validation failed")
                .jsonPath("$.message").isEqualTo("Required query parameters are missing");
    }


    @Test
    void testUpdateEmployee_WithInvalidId_ShouldReturnBadRequest() {
        String requestBody = "{\"employeeNumber\":\"EMP001\",\"name\":\"Test\",\"furigana\":\"test\"}";
        
        webTestClient.put()
                .uri("/api/v1/employee/0")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testDeleteEmployeeById_WithInvalidId_ShouldReturnBadRequest() {
        webTestClient.delete()
                .uri("/api/v1/employee/0")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testDeleteEmployeeByNumber_WithInvalidFormat_ShouldReturnBadRequest() {
        webTestClient.delete()
                .uri("/api/v1/employee/number/EMP@001")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testDeleteEmployeeByNumber_WithTooLongNumber_ShouldReturnBadRequest() {
        webTestClient.delete()
                .uri("/api/v1/employee/number/EMP001234567890123456789")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    void testValidEmployeeNumber_ShouldPassValidation() {
        // Mock service to return empty Mono (employee not found)
        when(employeeApplicationService.getEmployeeByNumber(any())).thenReturn(Mono.empty());
        
        webTestClient.get()
                .uri("/api/v1/employee/number/EMP001")
                .exchange()
                .expectStatus().isNotFound(); // Employee does not exist, but validation passes
    }

    @Test
    void testValidSearchParameters_ShouldPassValidation() {
        webTestClient.get()
                .uri("/api/v1/employee/search/name?name=Tanaka")
                .exchange()
                .expectStatus().isOk(); // Validation passes, returns empty result
    }


    @Test
    void testValidId_ShouldPassValidation() {
        // Mock service to return empty Mono (employee not found)
        when(employeeApplicationService.getEmployeeById(any())).thenReturn(Mono.empty());
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isNotFound(); // Employee does not exist, but validation passes
    }
}

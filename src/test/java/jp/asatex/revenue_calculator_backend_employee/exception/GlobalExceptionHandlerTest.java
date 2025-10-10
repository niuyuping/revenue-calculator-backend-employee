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
 * Global exception handler test
 * Tests handling of various exceptions
 */
@WebFluxTest({jp.asatex.revenue_calculator_backend_employee.controller.EmployeeController.class, jp.asatex.revenue_calculator_backend_employee.exception.GlobalExceptionHandler.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private EmployeeService employeeService;


    @Test
    public void testHandleEmployeeNotFoundException() {
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new EmployeeNotFoundException("Employee does not exist, ID: 999"))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Employee not found")
                .jsonPath("$.message").isEqualTo("Employee does not exist, ID: 999");
    }

    @Test
    public void testHandleDuplicateEmployeeNumberException() {
        when(employeeService.createEmployee(any())).thenReturn(
                Mono.error(new DuplicateEmployeeNumberException("Employee number already exists: EMP001"))
        );
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue("{\"employeeNumber\":\"EMP001\",\"name\":\"Test\",\"furigana\":\"test\"}")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Duplicate employee number")
                .jsonPath("$.message").isEqualTo("Employee number already exists: EMP001");
    }

    @Test
    public void testHandleValidationException_InvalidFurigana() {
        // Test furigana with invalid characters
        String invalidEmployee = "{\"employeeNumber\":\"EMP001\",\"name\":\"Test\",\"furigana\":\"test@#$%\"}";
        
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
        String invalidEmployee = "{\"employeeNumber\":\"EMP001\",\"name\":\"\",\"furigana\":\"test\"}";
        
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
        String invalidEmployee = String.format("{\"employeeNumber\":\"%s\",\"name\":\"Test\",\"furigana\":\"test\"}", tooLongNumber);
        
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
        String invalidEmployee = "{\"employeeNumber\":\"EMP@001\",\"name\":\"Test\",\"furigana\":\"test\"}";
        
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
        String invalidEmployee = "{\"employeeNumber\":\"\",\"name\":\"Test\",\"furigana\":\"test\"}";
        
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
        String invalidEmployee = String.format("{\"employeeNumber\":\"EMP001\",\"name\":\"%s\",\"furigana\":\"test\"}", tooLongName);
        
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
        String tooLongFurigana = "a".repeat(201);
        String invalidEmployee = String.format("{\"employeeNumber\":\"EMP001\",\"name\":\"Test\",\"furigana\":\"%s\"}", tooLongFurigana);
        
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
        String invalidEmployee = "{\"employeeNumber\":\"EMP001\",\"name\":\"Test\",\"furigana\":\"test\",\"birthday\":\"2030-01-01\"}";
        
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
        // Simulate ConstraintViolationException
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new jakarta.validation.ConstraintViolationException("Parameter validation failed", null))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Parameter validation failed");
    }

    @Test
    public void testHandleGenericException() {
        // Simulate generic exception
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new RuntimeException("Database connection failed"))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Internal Server Error")
                .jsonPath("$.message").isEqualTo("Database connection failed");
    }

    @Test
    public void testHandleGenericException_WithValidationFailure() {
        // Simulate exception containing "Validation failure"
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new RuntimeException("Validation failure: Invalid data"))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    public void testHandleGenericException_WithConstraintViolationCause() {
        // Simulate exception containing ConstraintViolationException as cause
        jakarta.validation.ConstraintViolationException cve = 
                new jakarta.validation.ConstraintViolationException("Parameter validation failed", null);
        RuntimeException exception = new RuntimeException("Outer exception", cve);
        
        when(employeeService.getEmployeeById(any())).thenReturn(Mono.error(exception));
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request data does not conform to validation rules");
    }

    @Test
    public void testHandleGenericException_WithNullMessage() {
        // Simulate exception without message
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new RuntimeException())
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Internal Server Error")
                .jsonPath("$.message").isEqualTo("Unknown error");
    }

    @Test
    public void testHandleUpdateEmployee_WithDuplicateNumber() {
        when(employeeService.updateEmployee(any(), any())).thenReturn(
                Mono.error(new DuplicateEmployeeNumberException("Employee number already exists: EMP002"))
        );
        
        String updateEmployee = "{\"employeeNumber\":\"EMP002\",\"name\":\"Test\",\"furigana\":\"test\"}";
        
        webTestClient.put()
                .uri("/api/v1/employee/1")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updateEmployee)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Duplicate employee number")
                .jsonPath("$.message").isEqualTo("Employee number already exists: EMP002");
    }

    @Test
    public void testHandleDeleteEmployee_WithNotFoundException() {
        when(employeeService.deleteEmployeeById(any())).thenReturn(
                Mono.error(new EmployeeNotFoundException("Employee does not exist, ID: 999"))
        );
        
        webTestClient.delete()
                .uri("/api/v1/employee/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Employee not found")
                .jsonPath("$.message").isEqualTo("Employee does not exist, ID: 999");
    }

    // This test is skipped due to complex WebExchangeBindException simulation
    // @Test
    // public void testHandleWebExchangeBindException_WithNullDefaultMessage() {
    //     // WebExchangeBindException simulation (when defaultMessage is null)
    // }

    @Test
    public void testHandleServerWebInputException() {
        // Simulate ServerWebInputException
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new org.springframework.web.server.ServerWebInputException("Invalid input data"))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/invalid")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.message").exists();
    }

    @Test
    public void testHandleServerWebInputException_WithNullMessage() {
        // Simulate ServerWebInputException with empty message to test null message handling
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new org.springframework.web.server.ServerWebInputException(""))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/invalid")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.message").exists();
    }

    @Test
    public void testHandleConstraintViolationException_WithNullMessage() {
        // Simulate ConstraintViolationException with empty message to test null message handling
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new jakarta.validation.ConstraintViolationException("", null))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Parameter validation failed");
    }

    @Test
    public void testHandleGenericException_WithEmptyMessage() {
        // Simulate exception with empty message
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new RuntimeException(""))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.message").exists();
    }

    @Test
    public void testHandleGenericException_WithWhitespaceMessage() {
        // Simulate exception with whitespace-only message
        when(employeeService.getEmployeeById(any())).thenReturn(
                Mono.error(new RuntimeException("   "))
        );
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.error").exists()
                .jsonPath("$.message").exists();
    }

    @Test
    public void testHandleValidationException_WithMultipleErrors() {
        // Request containing multiple validation errors
        String invalidEmployee = "{\"employeeNumber\":\"\",\"name\":\"\",\"furigana\":\"test@#$%\",\"birthday\":\"2030-01-01\"}";
        
        webTestClient.post()
                .uri("/api/v1/employee")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.message").isEqualTo("Request parameter validation failed")
                .jsonPath("$.details").exists()
                .jsonPath("$.details").isMap();
    }

    // This test is skipped due to complex WebExchangeBindException simulation
    // @Test
    // public void testHandleValidationException_WithDuplicateFieldErrors() {
    //     // Test for multiple errors on the same field
    // }

    @Test
    public void testHandleGenericException_WithNestedCause() {
        // Simulate exception with nested causes
        RuntimeException rootCause = new RuntimeException("Root cause");
        RuntimeException middleCause = new RuntimeException("Middle cause", rootCause);
        RuntimeException topException = new RuntimeException("Top exception", middleCause);
        
        when(employeeService.getEmployeeById(any())).thenReturn(Mono.error(topException));
        
        webTestClient.get()
                .uri("/api/v1/employee/1")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Internal Server Error")
                .jsonPath("$.message").isEqualTo("Top exception");
    }

    // This test is skipped due to complex exception chain simulation
    // @Test
    // public void testHandleGenericException_WithValidationFailureInNestedCause() {
    //     // Simulate exception containing Validation failure in nested cause
    // }
}

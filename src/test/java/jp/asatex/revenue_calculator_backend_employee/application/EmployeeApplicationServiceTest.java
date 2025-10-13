package jp.asatex.revenue_calculator_backend_employee.application;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.exception.TransactionHandler;
import jp.asatex.revenue_calculator_backend_employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * EmployeeApplicationService test class
 */
@DisplayName("EmployeeApplicationService Test")
@ExtendWith(MockitoExtension.class)
class EmployeeApplicationServiceTest {

    @Mock
    private EmployeeService employeeService;

    private EmployeeApplicationService employeeApplicationService;
    private EmployeeDto testEmployeeDto;

    @BeforeEach
    void setUp() {
        employeeApplicationService = new EmployeeApplicationService();
        // Use reflection to inject the mock service
        try {
            java.lang.reflect.Field field = EmployeeApplicationService.class.getDeclaredField("employeeService");
            field.setAccessible(true);
            field.set(employeeApplicationService, employeeService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock service", e);
        }

        testEmployeeDto = new EmployeeDto();
        testEmployeeDto.setEmployeeId(1L);
        testEmployeeDto.setEmployeeNumber("EMP001");
        testEmployeeDto.setName("Tanaka Taro");
        testEmployeeDto.setFurigana("tanaka taro");
        testEmployeeDto.setBirthday(LocalDate.of(1990, 5, 15));
    }

    @Test
    @DisplayName("Create employee should wrap unexpected exceptions in TransactionHandler")
    void testCreateEmployeeWrapsUnexpectedExceptions() {
        // Given
        RuntimeException unexpectedException = new RuntimeException("Database connection failed");
        when(employeeService.createEmployee(any(EmployeeDto.class)))
                .thenReturn(Mono.error(unexpectedException));

        // When & Then
        StepVerifier.create(employeeApplicationService.createEmployee(testEmployeeDto))
                .expectError(TransactionHandler.class)
                .verify();
    }

    @Test
    @DisplayName("Update employee should wrap unexpected exceptions in TransactionHandler")
    void testUpdateEmployeeWrapsUnexpectedExceptions() {
        // Given
        RuntimeException unexpectedException = new RuntimeException("Database connection failed");
        when(employeeService.updateEmployee(any(Long.class), any(EmployeeDto.class)))
                .thenReturn(Mono.error(unexpectedException));

        // When & Then
        StepVerifier.create(employeeApplicationService.updateEmployee(1L, testEmployeeDto))
                .expectError(TransactionHandler.class)
                .verify();
    }

    @Test
    @DisplayName("Delete employee should wrap unexpected exceptions in TransactionHandler")
    void testDeleteEmployeeWrapsUnexpectedExceptions() {
        // Given
        RuntimeException unexpectedException = new RuntimeException("Database connection failed");
        when(employeeService.deleteEmployeeById(any(Long.class)))
                .thenReturn(Mono.error(unexpectedException));

        // When & Then
        StepVerifier.create(employeeApplicationService.deleteEmployee(1L))
                .expectError(TransactionHandler.class)
                .verify();
    }

    @Test
    @DisplayName("Delete employee by number should wrap unexpected exceptions in TransactionHandler")
    void testDeleteEmployeeByNumberWrapsUnexpectedExceptions() {
        // Given
        RuntimeException unexpectedException = new RuntimeException("Database connection failed");
        when(employeeService.deleteEmployeeByNumber(any(String.class)))
                .thenReturn(Mono.error(unexpectedException));

        // When & Then
        StepVerifier.create(employeeApplicationService.deleteEmployeeByNumber("EMP001"))
                .expectError(TransactionHandler.class)
                .verify();
    }

    @Test
    @DisplayName("Create employee should not wrap TransactionHandler")
    void testCreateEmployeeDoesNotWrapTransactionHandler() {
        // Given
        TransactionHandler transactionException = new TransactionHandler("Transaction failed");
        when(employeeService.createEmployee(any(EmployeeDto.class)))
                .thenReturn(Mono.error(transactionException));

        // When & Then
        StepVerifier.create(employeeApplicationService.createEmployee(testEmployeeDto))
                .expectError(TransactionHandler.class)
                .verify();
    }
}

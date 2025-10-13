package jp.asatex.revenue_calculator_backend_employee.service;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberHandler;
import jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundHandler;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import reactor.test.StepVerifier;


import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * EmployeeService unit test
 * Tests all business logic methods
 */
@ExtendWith(MockitoExtension.class)
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
class EmployeeServiceTest {
    
    /**
     * Utility method to create LocalDate objects
     */
    private LocalDate createDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }
    

    @Mock
    private EmployeeRepository employeeRepository;
    
    @Mock
    private io.micrometer.core.instrument.Counter employeeOperationCounter;
    
    @Mock
    private io.micrometer.core.instrument.Counter employeeCreateCounter;
    
    @Mock
    private io.micrometer.core.instrument.Counter employeeUpdateCounter;
    
    @Mock
    private io.micrometer.core.instrument.Counter employeeDeleteCounter;
    
    @Mock
    private io.micrometer.core.instrument.Counter employeeQueryCounter;
    
    @Mock
    private io.micrometer.core.instrument.Timer employeeOperationTimer;
    
    @Mock
    private io.micrometer.core.instrument.Timer databaseOperationTimer;

    @Mock
    private SystemMonitoringService transactionMonitoringService;


    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDto testEmployeeDto;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployeeDto = new EmployeeDto();
        testEmployeeDto.setEmployeeId(1L);
        testEmployeeDto.setEmployeeNumber("EMP001");
        testEmployeeDto.setName("Tanaka Taro");
        testEmployeeDto.setFurigana("tanaka taro");
        testEmployeeDto.setBirthday(createDate(1990, 5, 15));

        testEmployee = new Employee();
        testEmployee.setEmployeeId(1L);
        testEmployee.setEmployeeNumber("EMP001");
        testEmployee.setName("Tanaka Taro");
        testEmployee.setFurigana("tanaka taro");
        testEmployee.setBirthday(createDate(1990, 5, 15));

        // Configure SystemMonitoringService Mock - using lenient() to avoid unnecessary stubbing errors
        org.mockito.Mockito.lenient().when(transactionMonitoringService.monitorTransaction(any(String.class), any(String.class), any()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    Mono<Object> operation = (Mono<Object>) invocation.getArgument(2);
                    return operation;
                });

    }


    @Test
    void getEmployeeById_WhenEmployeeExists_ShouldReturnEmployee() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));

        // When & Then
        StepVerifier.create(employeeService.getEmployeeById(1L))
                .expectNextMatches(dto -> dto.getEmployeeNumber().equals("EMP001"))
                .verifyComplete();
    }

    @Test
    void getEmployeeById_WhenEmployeeNotExists_ShouldThrowException() {
        // Given
        when(employeeRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.getEmployeeById(999L))
                .expectError(EmployeeNotFoundHandler.class)
                .verify();
    }

    @Test
    void getEmployeeByNumber_WhenEmployeeExists_ShouldReturnEmployee() {
        // Given
        when(employeeRepository.findByEmployeeNumber("EMP001")).thenReturn(Mono.just(testEmployee));

        // When & Then
        StepVerifier.create(employeeService.getEmployeeByNumber("EMP001"))
                .expectNextMatches(dto -> dto.getEmployeeNumber().equals("EMP001"))
                .verifyComplete();
    }

    @Test
    void getEmployeeByNumber_WhenEmployeeNotExists_ShouldThrowException() {
        // Given
        when(employeeRepository.findByEmployeeNumber("NOTEXIST")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.getEmployeeByNumber("NOTEXIST"))
                .expectError(EmployeeNotFoundHandler.class)
                .verify();
    }

    @Test
    void searchEmployeesByName_ShouldReturnMatchingEmployees() {
        // Given
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByNameContaining("%Tanaka%")).thenReturn(Flux.fromIterable(employees));

        // When & Then
        StepVerifier.create(employeeService.searchEmployeesByName("Tanaka"))
                .expectNextMatches(dto -> dto.getName().contains("Tanaka"))
                .verifyComplete();
    }


    @Test
    void createEmployee_WhenEmployeeNumberNotExists_ShouldCreateEmployee() {
        // Given
        when(employeeRepository.existsByEmployeeNumber("EMP001")).thenReturn(Mono.just(false));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(testEmployee));

        // When & Then
        StepVerifier.create(employeeService.createEmployee(testEmployeeDto))
                .expectNextMatches(dto -> dto.getEmployeeNumber().equals("EMP001"))
                .verifyComplete();
    }

    @Test
    void createEmployee_WhenEmployeeNumberExists_ShouldThrowException() {
        // Given
        when(employeeRepository.existsByEmployeeNumber("EMP001")).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(employeeService.createEmployee(testEmployeeDto))
                .expectError(DuplicateEmployeeNumberHandler.class)
                .verify();
    }

    @Test
    void updateEmployee_WhenEmployeeExistsAndNumberNotChanged_ShouldUpdateEmployee() {
        // Given
        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setEmployeeNumber("EMP001");
        updateDto.setName("Tanaka Taro (Updated)");
        updateDto.setFurigana("tanaka taro (updated)");
        updateDto.setBirthday(createDate(1990, 5, 15));

        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(testEmployee));

        // When & Then
        StepVerifier.create(employeeService.updateEmployee(1L, updateDto))
                .expectNextMatches(dto -> dto.getEmployeeNumber().equals("EMP001"))
                .verifyComplete();
    }

    @Test
    void updateEmployee_WhenEmployeeExistsAndNumberChanged_ShouldUpdateEmployee() {
        // Given
        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setEmployeeNumber("EMP002");
        updateDto.setName("Tanaka Taro (Updated)");
        updateDto.setFurigana("tanaka taro (updated)");
        updateDto.setBirthday(createDate(1990, 5, 15));

        // Create updated employee entity
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmployeeId(1L);
        updatedEmployee.setEmployeeNumber("EMP002");
        updatedEmployee.setName("Tanaka Taro (Updated)");
        updatedEmployee.setFurigana("tanaka taro (updated)");
        updatedEmployee.setBirthday(createDate(1990, 5, 15));

        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(updatedEmployee));

        // When & Then
        StepVerifier.create(employeeService.updateEmployee(1L, updateDto))
                .expectNextMatches(dto -> dto.getEmployeeNumber().equals("EMP002"))
                .verifyComplete();
    }

    @Test
    void updateEmployee_WhenEmployeeNotExists_ShouldThrowException() {
        // Given
        when(employeeRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.updateEmployee(999L, testEmployeeDto))
                .expectError(EmployeeNotFoundHandler.class)
                .verify();
    }

    @Test
    void updateEmployee_WhenEmployeeExistsButNewNumberExists_ShouldUpdateEmployee() {
        // Given
        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setEmployeeNumber("EMP002");
        updateDto.setName("Tanaka Taro (Updated)");
        updateDto.setFurigana("tanaka taro (updated)");
        updateDto.setBirthday(createDate(1990, 5, 15));

        // Create updated employee entity
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmployeeId(1L);
        updatedEmployee.setEmployeeNumber("EMP002");
        updatedEmployee.setName("Tanaka Taro (Updated)");
        updatedEmployee.setFurigana("tanaka taro (updated)");
        updatedEmployee.setBirthday(createDate(1990, 5, 15));

        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(updatedEmployee));

        // When & Then
        StepVerifier.create(employeeService.updateEmployee(1L, updateDto))
                .expectNextMatches(dto -> dto.getEmployeeNumber().equals("EMP002"))
                .verifyComplete();
    }

    @Test
    void deleteEmployeeById_WhenEmployeeExists_ShouldDeleteEmployee() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));
        when(employeeRepository.delete(testEmployee)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.deleteEmployeeById(1L))
                .verifyComplete();
    }

    @Test
    void deleteEmployeeById_WhenEmployeeNotExists_ShouldThrowException() {
        // Given
        when(employeeRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.deleteEmployeeById(999L))
                .expectError(EmployeeNotFoundHandler.class)
                .verify();
    }

    @Test
    void deleteEmployeeByNumber_WhenEmployeeExists_ShouldDeleteEmployee() {
        // Given
        when(employeeRepository.findByEmployeeNumber("EMP001")).thenReturn(Mono.just(testEmployee));
        when(employeeRepository.delete(testEmployee)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.deleteEmployeeByNumber("EMP001"))
                .verifyComplete();
    }

    @Test
    void deleteEmployeeByNumber_WhenEmployeeNotExists_ShouldThrowException() {
        // Given
        when(employeeRepository.findByEmployeeNumber("NOTEXIST")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.deleteEmployeeByNumber("NOTEXIST"))
                .expectError(EmployeeNotFoundHandler.class)
                .verify();
    }

}

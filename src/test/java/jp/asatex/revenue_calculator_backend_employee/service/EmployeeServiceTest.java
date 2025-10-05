package jp.asatex.revenue_calculator_backend_employee.service;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import jp.asatex.revenue_calculator_backend_employee.exception.DuplicateEmployeeNumberException;
import jp.asatex.revenue_calculator_backend_employee.exception.EmployeeNotFoundException;
import jp.asatex.revenue_calculator_backend_employee.repository.EmployeeRepository;
import jp.asatex.revenue_calculator_backend_employee.service.DatabaseAuditService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * EmployeeService単体テスト
 * すべてのビジネスロジックメソッドをテスト
 */
@ExtendWith(MockitoExtension.class)
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
class EmployeeServiceTest {
    
    /**
     * 创建LocalDate对象的工具方法
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
    private TransactionMonitoringService transactionMonitoringService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private DatabaseAuditService databaseAuditService;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDto testEmployeeDto;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployeeDto = new EmployeeDto();
        testEmployeeDto.setEmployeeId(1L);
        testEmployeeDto.setEmployeeNumber("EMP001");
        testEmployeeDto.setName("田中太郎");
        testEmployeeDto.setFurigana("タナカタロウ");
        testEmployeeDto.setBirthday(createDate(1990, 5, 15));

        testEmployee = new Employee();
        testEmployee.setEmployeeId(1L);
        testEmployee.setEmployeeNumber("EMP001");
        testEmployee.setName("田中太郎");
        testEmployee.setFurigana("タナカタロウ");
        testEmployee.setBirthday(createDate(1990, 5, 15));

        // 配置 TransactionMonitoringService Mock - 使用 lenient() 避免不必要的 stubbing 错误
        org.mockito.Mockito.lenient().when(transactionMonitoringService.monitorTransaction(any(String.class), any(String.class), any(Mono.class)))
                .thenAnswer(invocation -> {
                    Mono<?> operation = invocation.getArgument(2);
                    return operation;
                });

        // AuditLogService 的方法调用是可选的，不需要Mock配置
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        // Given
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findAll()).thenReturn(Flux.fromIterable(employees));

        // When & Then
        StepVerifier.create(employeeService.getAllEmployees())
                .expectNextMatches(dto -> dto.getEmployeeNumber().equals("EMP001"))
                .verifyComplete();
    }

    @Test
    void getEmployeeById_WhenEmployeeExists_ShouldReturnEmployee() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));
        when(databaseAuditService.logSelectOperation(any(String.class), any(String.class), any(String.class), any(Long.class), any(Integer.class)))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.getEmployeeById(1L))
                .expectNextMatches(dto -> dto.getEmployeeNumber().equals("EMP001"))
                .verifyComplete();
    }

    @Test
    void getEmployeeById_WhenEmployeeNotExists_ShouldReturnEmpty() {
        // Given
        when(employeeRepository.findById(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.getEmployeeById(999L))
                .verifyComplete();
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
    void getEmployeeByNumber_WhenEmployeeNotExists_ShouldReturnEmpty() {
        // Given
        when(employeeRepository.findByEmployeeNumber("NOTEXIST")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.getEmployeeByNumber("NOTEXIST"))
                .verifyComplete();
    }

    @Test
    void searchEmployeesByName_ShouldReturnMatchingEmployees() {
        // Given
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByNameContaining("%田中%")).thenReturn(Flux.fromIterable(employees));

        // When & Then
        StepVerifier.create(employeeService.searchEmployeesByName("田中"))
                .expectNextMatches(dto -> dto.getName().contains("田中"))
                .verifyComplete();
    }

    @Test
    void searchEmployeesByFurigana_ShouldReturnMatchingEmployees() {
        // Given
        List<Employee> employees = Arrays.asList(testEmployee);
        when(employeeRepository.findByFuriganaContaining("%タナカ%")).thenReturn(Flux.fromIterable(employees));

        // When & Then
        StepVerifier.create(employeeService.searchEmployeesByFurigana("タナカ"))
                .expectNextMatches(dto -> dto.getFurigana().contains("タナカ"))
                .verifyComplete();
    }

    @Test
    void createEmployee_WhenEmployeeNumberNotExists_ShouldCreateEmployee() {
        // Given
        when(employeeRepository.existsByEmployeeNumber("EMP001")).thenReturn(Mono.just(false));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(testEmployee));
        when(databaseAuditService.logInsertOperation(any(String.class), any(String.class), any(Map.class), any(String.class), any(Long.class), any(Integer.class)))
                .thenReturn(Mono.empty());

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
                .expectError(DuplicateEmployeeNumberException.class)
                .verify();
    }

    @Test
    void updateEmployee_WhenEmployeeExistsAndNumberNotChanged_ShouldUpdateEmployee() {
        // Given
        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setEmployeeNumber("EMP001");
        updateDto.setName("田中太郎（更新）");
        updateDto.setFurigana("タナカタロウ（コウシン）");
        updateDto.setBirthday(createDate(1990, 5, 15));

        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(testEmployee));
        when(databaseAuditService.logUpdateOperation(any(String.class), any(String.class), any(Map.class), any(Map.class), any(String.class), any(Long.class), any(Integer.class)))
                .thenReturn(Mono.empty());

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
        updateDto.setName("田中太郎（更新）");
        updateDto.setFurigana("タナカタロウ（コウシン）");
        updateDto.setBirthday(createDate(1990, 5, 15));

        // 创建更新后的员工实体
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmployeeId(1L);
        updatedEmployee.setEmployeeNumber("EMP002");
        updatedEmployee.setName("田中太郎（更新）");
        updatedEmployee.setFurigana("タナカタロウ（コウシン）");
        updatedEmployee.setBirthday(createDate(1990, 5, 15));

        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));
        when(employeeRepository.existsByEmployeeNumber("EMP002")).thenReturn(Mono.just(false));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(updatedEmployee));
        when(databaseAuditService.logUpdateOperation(any(String.class), any(String.class), any(Map.class), any(Map.class), any(String.class), any(Long.class), any(Integer.class)))
                .thenReturn(Mono.empty());

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
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void updateEmployee_WhenEmployeeExistsButNewNumberExists_ShouldThrowException() {
        // Given
        EmployeeDto updateDto = new EmployeeDto();
        updateDto.setEmployeeNumber("EMP002");
        updateDto.setName("田中太郎（更新）");
        updateDto.setFurigana("タナカタロウ（コウシン）");
        updateDto.setBirthday(createDate(1990, 5, 15));

        when(employeeRepository.findById(1L)).thenReturn(Mono.just(testEmployee));
        when(employeeRepository.existsByEmployeeNumber("EMP002")).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(employeeService.updateEmployee(1L, updateDto))
                .expectError(DuplicateEmployeeNumberException.class)
                .verify();
    }

    @Test
    void deleteEmployeeById_WhenEmployeeExists_ShouldDeleteEmployee() {
        // Given
        when(employeeRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(employeeRepository.deleteById(1L)).thenReturn(Mono.empty());
        when(databaseAuditService.logDeleteOperation(any(String.class), any(String.class), any(Map.class), any(String.class), any(Long.class), any(Integer.class)))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.deleteEmployeeById(1L))
                .verifyComplete();
    }

    @Test
    void deleteEmployeeById_WhenEmployeeNotExists_ShouldThrowException() {
        // Given
        when(employeeRepository.existsById(999L)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(employeeService.deleteEmployeeById(999L))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void deleteEmployeeByNumber_WhenEmployeeExists_ShouldDeleteEmployee() {
        // Given
        when(employeeRepository.existsByEmployeeNumber("EMP001")).thenReturn(Mono.just(true));
        when(employeeRepository.deleteByEmployeeNumber("EMP001")).thenReturn(Mono.empty());
        when(databaseAuditService.logDeleteOperation(any(String.class), any(String.class), any(Map.class), any(String.class), any(Long.class), any(Integer.class)))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(employeeService.deleteEmployeeByNumber("EMP001"))
                .verifyComplete();
    }

    @Test
    void deleteEmployeeByNumber_WhenEmployeeNotExists_ShouldThrowException() {
        // Given
        when(employeeRepository.existsByEmployeeNumber("NOTEXIST")).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(employeeService.deleteEmployeeByNumber("NOTEXIST"))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void convertToDto_ShouldConvertEntityToDto() {
        // Given
        Employee entity = testEmployee;

        // When
        EmployeeDto result = employeeService.convertToDto(entity);

        // Then
        assert result.getEmployeeId().equals(entity.getEmployeeId());
        assert result.getEmployeeNumber().equals(entity.getEmployeeNumber());
        assert result.getName().equals(entity.getName());
        assert result.getFurigana().equals(entity.getFurigana());
        assert result.getBirthday().equals(entity.getBirthday());
    }

    @Test
    void convertToEntity_ShouldConvertDtoToEntity() {
        // Given
        EmployeeDto dto = testEmployeeDto;

        // When
        Employee result = employeeService.convertToEntity(dto);

        // Then
        assert result.getEmployeeId().equals(dto.getEmployeeId());
        assert result.getEmployeeNumber().equals(dto.getEmployeeNumber());
        assert result.getName().equals(dto.getName());
        assert result.getFurigana().equals(dto.getFurigana());
        assert result.getBirthday().equals(dto.getBirthday());
    }
}

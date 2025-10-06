package jp.asatex.revenue_calculator_backend_employee.repository;

import jp.asatex.revenue_calculator_backend_employee.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EmployeeRepository test
 * Tests database operations and custom queries
 */
@DataR2dbcTest
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee testEmployee1;
    private Employee testEmployee2;

    @BeforeEach
    void setUp() {
        // Clean up test data
        employeeRepository.deleteAll().block();

        // Create test data
        testEmployee1 = new Employee();
        testEmployee1.setEmployeeNumber("EMP001");
        testEmployee1.setName("Tanaka Taro");
        testEmployee1.setFurigana("tanaka taro");
        testEmployee1.setBirthday(LocalDate.of(1990, 5, 15));

        testEmployee2 = new Employee();
        testEmployee2.setEmployeeNumber("EMP002");
        testEmployee2.setName("Sato Hanako");
        testEmployee2.setFurigana("sato hanako");
        testEmployee2.setBirthday(LocalDate.of(1985, 8, 20));
    }

    @Test
    void save_ShouldSaveEmployee() {
        // When
        Mono<Employee> result = employeeRepository.save(testEmployee1);

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> {
                    assertThat(employee.getEmployeeId()).isNotNull();
                    assertThat(employee.getEmployeeNumber()).isEqualTo("EMP001");
                    assertThat(employee.getName()).isEqualTo("Tanaka Taro");
                    assertThat(employee.getFurigana()).isEqualTo("tanaka taro");
                    assertThat(employee.getBirthday()).isEqualTo(LocalDate.of(1990, 5, 15));
                })
                .verifyComplete();
    }

    @Test
    void findById_WhenEmployeeExists_ShouldReturnEmployee() {
        // Given
        Employee savedEmployee = employeeRepository.save(testEmployee1).block();

        // When
        Mono<Employee> result = employeeRepository.findById(savedEmployee.getEmployeeId());

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> {
                    assertThat(employee.getEmployeeNumber()).isEqualTo("EMP001");
                    assertThat(employee.getName()).isEqualTo("Tanaka Taro");
                })
                .verifyComplete();
    }

    @Test
    void findById_WhenEmployeeNotExists_ShouldReturnEmpty() {
        // When
        Mono<Employee> result = employeeRepository.findById(999L);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void findByEmployeeNumber_WhenEmployeeExists_ShouldReturnEmployee() {
        // Given
        employeeRepository.save(testEmployee1).block();

        // When
        Mono<Employee> result = employeeRepository.findByEmployeeNumber("EMP001");

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> {
                    assertThat(employee.getEmployeeNumber()).isEqualTo("EMP001");
                    assertThat(employee.getName()).isEqualTo("Tanaka Taro");
                })
                .verifyComplete();
    }

    @Test
    void findByEmployeeNumber_WhenEmployeeNotExists_ShouldReturnEmpty() {
        // When
        Mono<Employee> result = employeeRepository.findByEmployeeNumber("NOTEXIST");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void findByNameContaining_ShouldReturnMatchingEmployees() {
        // Given
        employeeRepository.save(testEmployee1).block();
        employeeRepository.save(testEmployee2).block();

        // When
        Flux<Employee> result = employeeRepository.findByNameContaining("%Tanaka%");

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> {
                    assertThat(employee.getName()).contains("Tanaka");
                    assertThat(employee.getEmployeeNumber()).isEqualTo("EMP001");
                })
                .verifyComplete();
    }

    @Test
    void findByNameContaining_WhenNoMatch_ShouldReturnEmpty() {
        // Given
        employeeRepository.save(testEmployee1).block();

        // When
        Flux<Employee> result = employeeRepository.findByNameContaining("Yamada");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void findByFuriganaContaining_ShouldReturnMatchingEmployees() {
        // Given
        employeeRepository.save(testEmployee1).block();
        employeeRepository.save(testEmployee2).block();

        // When
        Flux<Employee> result = employeeRepository.findByFuriganaContaining("%tanaka%");

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> {
                    assertThat(employee.getFurigana()).contains("tanaka");
                    assertThat(employee.getEmployeeNumber()).isEqualTo("EMP001");
                })
                .verifyComplete();
    }

    @Test
    void findByFuriganaContaining_WhenNoMatch_ShouldReturnEmpty() {
        // Given
        employeeRepository.save(testEmployee1).block();

        // When
        Flux<Employee> result = employeeRepository.findByFuriganaContaining("%yamada%");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void existsByEmployeeNumber_WhenEmployeeExists_ShouldReturnTrue() {
        // Given
        employeeRepository.save(testEmployee1).block();

        // When
        Mono<Boolean> result = employeeRepository.existsByEmployeeNumber("EMP001");

        // Then
        StepVerifier.create(result)
                .assertNext(exists -> assertThat(exists).isTrue())
                .verifyComplete();
    }

    @Test
    void existsByEmployeeNumber_WhenEmployeeNotExists_ShouldReturnFalse() {
        // When
        Mono<Boolean> result = employeeRepository.existsByEmployeeNumber("NOTEXIST");

        // Then
        StepVerifier.create(result)
                .assertNext(exists -> assertThat(exists).isFalse())
                .verifyComplete();
    }

    @Test
    void existsById_WhenEmployeeExists_ShouldReturnTrue() {
        // Given
        Employee savedEmployee = employeeRepository.save(testEmployee1).block();

        // When
        Mono<Boolean> result = employeeRepository.existsById(savedEmployee.getEmployeeId());

        // Then
        StepVerifier.create(result)
                .assertNext(exists -> assertThat(exists).isTrue())
                .verifyComplete();
    }

    @Test
    void existsById_WhenEmployeeNotExists_ShouldReturnFalse() {
        // When
        Mono<Boolean> result = employeeRepository.existsById(999L);

        // Then
        StepVerifier.create(result)
                .assertNext(exists -> assertThat(exists).isFalse())
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnAllEmployees() {
        // Given
        employeeRepository.save(testEmployee1).block();
        employeeRepository.save(testEmployee2).block();

        // When
        Flux<Employee> result = employeeRepository.findAll();

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> assertThat(employee.getEmployeeNumber()).isIn("EMP001", "EMP002"))
                .assertNext(employee -> assertThat(employee.getEmployeeNumber()).isIn("EMP001", "EMP002"))
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldDeleteEmployee() {
        // Given
        Employee savedEmployee = employeeRepository.save(testEmployee1).block();

        // When
        Mono<Void> result = employeeRepository.deleteById(savedEmployee.getEmployeeId());

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        // Verify deletion
        StepVerifier.create(employeeRepository.findById(savedEmployee.getEmployeeId()))
                .verifyComplete();
    }

    @Test
    void deleteByEmployeeNumber_ShouldDeleteEmployee() {
        // Given
        employeeRepository.save(testEmployee1).block();

        // When
        Mono<Void> result = employeeRepository.deleteByEmployeeNumber("EMP001");

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        // Verify deletion
        StepVerifier.create(employeeRepository.findByEmployeeNumber("EMP001"))
                .verifyComplete();
    }

    @Test
    void deleteAll_ShouldDeleteAllEmployees() {
        // Given
        employeeRepository.save(testEmployee1).block();
        employeeRepository.save(testEmployee2).block();

        // When
        Mono<Void> result = employeeRepository.deleteAll();

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        // Verify all deleted
        StepVerifier.create(employeeRepository.findAll())
                .verifyComplete();
    }

    @Test
    void updateEmployee_ShouldUpdateEmployee() {
        // Given
        Employee savedEmployee = employeeRepository.save(testEmployee1).block();
        savedEmployee.setName("Tanaka Taro (Updated)");
        savedEmployee.setFurigana("tanaka taro (updated)");

        // When
        Mono<Employee> result = employeeRepository.save(savedEmployee);

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> {
                    assertThat(employee.getName()).isEqualTo("Tanaka Taro (Updated)");
                    assertThat(employee.getFurigana()).isEqualTo("tanaka taro (updated)");
                    assertThat(employee.getEmployeeId()).isEqualTo(savedEmployee.getEmployeeId());
                })
                .verifyComplete();
    }

    @Test
    void saveEmployeeWithNullFurigana_ShouldSaveSuccessfully() {
        // Given
        testEmployee1.setFurigana(null);

        // When
        Mono<Employee> result = employeeRepository.save(testEmployee1);

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> {
                    assertThat(employee.getFurigana()).isNull();
                    assertThat(employee.getEmployeeNumber()).isEqualTo("EMP001");
                })
                .verifyComplete();
    }

    @Test
    void saveEmployeeWithNullBirthday_ShouldSaveSuccessfully() {
        // Given
        testEmployee1.setBirthday(null);

        // When
        Mono<Employee> result = employeeRepository.save(testEmployee1);

        // Then
        StepVerifier.create(result)
                .assertNext(employee -> {
                    assertThat(employee.getBirthday()).isNull();
                    assertThat(employee.getEmployeeNumber()).isEqualTo("EMP001");
                })
                .verifyComplete();
    }
}

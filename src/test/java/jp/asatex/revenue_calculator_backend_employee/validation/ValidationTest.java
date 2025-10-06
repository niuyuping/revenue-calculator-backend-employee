package jp.asatex.revenue_calculator_backend_employee.validation;

import jp.asatex.revenue_calculator_backend_employee.dto.EmployeeDto;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation constraint test
 */
public class ValidationTest {

    private final Validator validator;

    public ValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidEmployeeDto() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("Tanaka Taro");
        employee.setFurigana("tanaka taro");
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertTrue(violations.isEmpty(), "Valid data should not have validation errors");
    }

    @Test
    public void testInvalidEmployeeNumber() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber(""); // Empty string
        employee.setName("Tanaka Taro");
        employee.setFurigana("tanaka taro");
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertFalse(violations.isEmpty(), "Invalid employee number should have validation errors");
        
        boolean hasEmployeeNumberError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber"));
        assertTrue(hasEmployeeNumberError, "Employee number should have validation errors");
    }

    @Test
    public void testInvalidName() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName(""); // Empty string
        employee.setFurigana("tanaka taro");
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertFalse(violations.isEmpty(), "Invalid name should have validation errors");
        
        boolean hasNameError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertTrue(hasNameError, "Name should have validation errors");
    }

    @Test
    public void testInvalidFurigana() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("Tanaka Taro");
        employee.setFurigana("tanaka123"); // Contains numbers, does not match furigana format
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertFalse(violations.isEmpty(), "Invalid furigana should have validation errors");
        
        boolean hasFuriganaError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("furigana"));
        assertTrue(hasFuriganaError, "Furigana should have validation errors");
    }

    @Test
    public void testInvalidBirthday() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("Tanaka Taro");
        employee.setFurigana("tanaka taro");
        employee.setBirthday(LocalDate.now().plusDays(1)); // Future date

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertFalse(violations.isEmpty(), "Invalid birthday should have validation errors");
        
        boolean hasBirthdayError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday"));
        assertTrue(hasBirthdayError, "Birthday should have validation errors");
    }

    @Test
    public void testValidFurigana() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("Tanaka Taro");
        employee.setFurigana("tanaka taro"); // Valid hiragana
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertTrue(violations.isEmpty(), "Valid furigana should not have validation errors");
    }

    @Test
    public void testValidKatakanaFurigana() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("Tanaka Taro");
        employee.setFurigana("tanaka taro"); // Valid katakana
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertTrue(violations.isEmpty(), "Valid katakana should not have validation errors");
    }
}


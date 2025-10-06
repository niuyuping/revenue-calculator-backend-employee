package jp.asatex.revenue_calculator_backend_employee.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for EmployeeDto
 * Tests all getter/setter, constructor, equals/hashCode/toString methods
 */
@DisplayName("EmployeeDto Test")
class EmployeeDtoTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Nested
    @DisplayName("Constructor Test")
    class ConstructorTests {

        @Test
        @DisplayName("Object is created with default constructor")
        void testDefaultConstructor() {
            // When
            EmployeeDto dto = new EmployeeDto();

            // Then
            assertNotNull(dto);
            assertNull(dto.getEmployeeId());
            assertNull(dto.getEmployeeNumber());
            assertNull(dto.getName());
            assertNull(dto.getFurigana());
            assertNull(dto.getBirthday());
        }

        @Test
        @DisplayName("Object is created with all parameters constructor")
        void testAllArgsConstructor() {
            // Given
            Long employeeId = 1L;
            String employeeNumber = "EMP001";
            String name = "Tanaka Taro";
            String furigana = "tanaka taro";
            LocalDate birthday = LocalDate.of(1990, 5, 15);

            // When
            EmployeeDto dto = new EmployeeDto(employeeId, employeeNumber, name, furigana, birthday);

            // Then
            assertNotNull(dto);
            assertEquals(employeeId, dto.getEmployeeId());
            assertEquals(employeeNumber, dto.getEmployeeNumber());
            assertEquals(name, dto.getName());
            assertEquals(furigana, dto.getFurigana());
            assertEquals(birthday, dto.getBirthday());
        }

        @Test
        @DisplayName("Constructor works with null parameters")
        void testAllArgsConstructorWithNulls() {
            // When
            EmployeeDto dto = new EmployeeDto(null, null, null, null, null);

            // Then
            assertNotNull(dto);
            assertNull(dto.getEmployeeId());
            assertNull(dto.getEmployeeNumber());
            assertNull(dto.getName());
            assertNull(dto.getFurigana());
            assertNull(dto.getBirthday());
        }
    }

    @Nested
    @DisplayName("Getter/Setter Test")
    class GetterSetterTests {

        @Test
        @DisplayName("employeeId getter/setter works correctly")
        void testEmployeeIdGetterSetter() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            Long employeeId = 123L;

            // When
            dto.setEmployeeId(employeeId);

            // Then
            assertEquals(employeeId, dto.getEmployeeId());
        }

        @Test
        @DisplayName("employeeNumber getter/setter works correctly")
        void testEmployeeNumberGetterSetter() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            String employeeNumber = "EMP123";

            // When
            dto.setEmployeeNumber(employeeNumber);

            // Then
            assertEquals(employeeNumber, dto.getEmployeeNumber());
        }

        @Test
        @DisplayName("name getter/setter works correctly")
        void testNameGetterSetter() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            String name = "Yamada Taro";

            // When
            dto.setName(name);

            // Then
            assertEquals(name, dto.getName());
        }

        @Test
        @DisplayName("furigana getter/setter works correctly")
        void testFuriganaGetterSetter() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            String furigana = "yamada taro";

            // When
            dto.setFurigana(furigana);

            // Then
            assertEquals(furigana, dto.getFurigana());
        }

        @Test
        @DisplayName("birthday getter/setter works correctly")
        void testBirthdayGetterSetter() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            LocalDate birthday = LocalDate.of(1985, 12, 25);

            // When
            dto.setBirthday(birthday);

            // Then
            assertEquals(birthday, dto.getBirthday());
        }

        @Test
        @DisplayName("null value setter works correctly")
        void testNullValueSetters() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.now());

            // When
            dto.setEmployeeId(null);
            dto.setEmployeeNumber(null);
            dto.setName(null);
            dto.setFurigana(null);
            dto.setBirthday(null);

            // Then
            assertNull(dto.getEmployeeId());
            assertNull(dto.getEmployeeNumber());
            assertNull(dto.getName());
            assertNull(dto.getFurigana());
            assertNull(dto.getBirthday());
        }
    }

    @Nested
    @DisplayName("equals/hashCode Test")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Objects with same content are equal")
        void testEqualsWithSameContent() {
            // Given
            EmployeeDto dto1 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));
            EmployeeDto dto2 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Objects with different content are not equal")
        void testEqualsWithDifferentContent() {
            // Given
            EmployeeDto dto1 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));
            EmployeeDto dto2 = new EmployeeDto(2L, "EMP002", "Sato Hanako", "sato hanako", LocalDate.of(1985, 12, 3));

            // When & Then
            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Comparison of objects containing null values works correctly")
        void testEqualsWithNullValues() {
            // Given
            EmployeeDto dto1 = new EmployeeDto(null, null, null, null, null);
            EmployeeDto dto2 = new EmployeeDto(null, null, null, null, null);

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Comparison with self is equal")
        void testEqualsWithSelf() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));

            // When & Then
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("Comparison with null is not equal")
        void testEqualsWithNull() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));

            // When & Then
            assertNotEquals(dto, null);
        }

        @Test
        @DisplayName("Comparison with different class is not equal")
        void testEqualsWithDifferentClass() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));
            String otherObject = "not an EmployeeDto";

            // When & Then
            assertNotEquals(dto, otherObject);
        }

        @Test
        @DisplayName("Comparison when some fields are different")
        void testEqualsWithPartialDifferences() {
            // Given
            EmployeeDto dto1 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));
            EmployeeDto dto2 = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 16)); // birthday is different

            // When & Then
            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("toString Test")
    class ToStringTests {

        @Test
        @DisplayName("toString outputs in correct format")
        void testToString() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "Tanaka Taro", "tanaka taro", LocalDate.of(1990, 5, 15));

            // When
            String result = dto.toString();

            // Then
            assertNotNull(result);
            assertTrue(result.contains("EmployeeDto"));
            assertTrue(result.contains("employeeId=1"));
            assertTrue(result.contains("employeeNumber='EMP001'"));
            assertTrue(result.contains("name='Tanaka Taro'"));
            assertTrue(result.contains("furigana='tanaka taro'"));
            assertTrue(result.contains("birthday=1990-05-15"));
        }

        @Test
        @DisplayName("toString with null values works correctly")
        void testToStringWithNullValues() {
            // Given
            EmployeeDto dto = new EmployeeDto(null, null, null, null, null);

            // When
            String result = dto.toString();

            // Then
            assertNotNull(result);
            assertTrue(result.contains("EmployeeDto"));
            assertTrue(result.contains("employeeId=null"));
            assertTrue(result.contains("employeeNumber='null'"));
            assertTrue(result.contains("name='null'"));
            assertTrue(result.contains("furigana='null'"));
            assertTrue(result.contains("birthday=null"));
        }
    }

    @Nested
    @DisplayName("Validation Test")
    class ValidationTests {

        @Test
        @DisplayName("Validation passes with valid data")
        void testValidData() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("Tanaka Taro");
            dto.setFurigana("tanaka taro");
            dto.setBirthday(LocalDate.of(1990, 5, 15));

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Validation error when employeeNumber is empty")
        void testEmptyEmployeeNumber() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("");
            dto.setName("Tanaka Taro");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber")));
        }

        @Test
        @DisplayName("Validation error when employeeNumber is null")
        void testNullEmployeeNumber() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setName("Tanaka Taro");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber")));
        }

        @Test
        @DisplayName("Validation error when employeeNumber is too long")
        void testTooLongEmployeeNumber() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("A".repeat(21)); // 21 characters
            dto.setName("Tanaka Taro");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber")));
        }

        @Test
        @DisplayName("Validation error when employeeNumber contains invalid characters")
        void testInvalidEmployeeNumber() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP@001"); // @ is invalid
            dto.setName("Tanaka Taro");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber")));
        }

        @Test
        @DisplayName("Validation error when name is empty")
        void testEmptyName() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Validation error when name is too long")
        void testTooLongName() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("A".repeat(101)); // 101 characters

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("Validation error when furigana is too long")
        void testTooLongFurigana() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("Tanaka Taro");
            dto.setFurigana("a".repeat(201)); // 201 characters

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("furigana")));
        }

        @Test
        @DisplayName("Validation error when furigana contains invalid characters")
        void testInvalidFurigana() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("Tanaka Taro");
            dto.setFurigana("tanaka@taro"); // @ is invalid

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("furigana")));
        }

        @Test
        @DisplayName("Validation error when birthday is future date")
        void testFutureBirthday() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("Tanaka Taro");
            dto.setBirthday(LocalDate.now().plusDays(1)); // Future date

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
        }

        @Test
        @DisplayName("Null furigana should not cause validation error")
        void testNullFuriganaIsValid() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("Tanaka Taro");
            dto.setFurigana(null);

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Null birthday should not cause validation error")
        void testNullBirthdayIsValid() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("Tanaka Taro");
            dto.setBirthday(null);

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
        }
    }
}


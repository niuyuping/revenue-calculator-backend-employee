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
 * EmployeeDtoの単体テスト
 * すべてのgetter/setter、コンストラクタ、equals/hashCode/toStringメソッドをテスト
 */
@DisplayName("EmployeeDto テスト")
class EmployeeDtoTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Nested
    @DisplayName("コンストラクタテスト")
    class ConstructorTests {

        @Test
        @DisplayName("デフォルトコンストラクタでオブジェクトが作成される")
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
        @DisplayName("全パラメータコンストラクタでオブジェクトが作成される")
        void testAllArgsConstructor() {
            // Given
            Long employeeId = 1L;
            String employeeNumber = "EMP001";
            String name = "田中太郎";
            String furigana = "たなかたろう";
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
        @DisplayName("nullパラメータでコンストラクタが動作する")
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
    @DisplayName("Getter/Setterテスト")
    class GetterSetterTests {

        @Test
        @DisplayName("employeeIdのgetter/setterが正常に動作する")
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
        @DisplayName("employeeNumberのgetter/setterが正常に動作する")
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
        @DisplayName("nameのgetter/setterが正常に動作する")
        void testNameGetterSetter() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            String name = "山田太郎";

            // When
            dto.setName(name);

            // Then
            assertEquals(name, dto.getName());
        }

        @Test
        @DisplayName("furiganaのgetter/setterが正常に動作する")
        void testFuriganaGetterSetter() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            String furigana = "やまだたろう";

            // When
            dto.setFurigana(furigana);

            // Then
            assertEquals(furigana, dto.getFurigana());
        }

        @Test
        @DisplayName("birthdayのgetter/setterが正常に動作する")
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
        @DisplayName("null値のsetterが正常に動作する")
        void testNullValueSetters() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.now());

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
    @DisplayName("equals/hashCodeテスト")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("同じ内容のオブジェクトは等しい")
        void testEqualsWithSameContent() {
            // Given
            EmployeeDto dto1 = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 15));
            EmployeeDto dto2 = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 15));

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("異なる内容のオブジェクトは等しくない")
        void testEqualsWithDifferentContent() {
            // Given
            EmployeeDto dto1 = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 15));
            EmployeeDto dto2 = new EmployeeDto(2L, "EMP002", "佐藤花子", "さとうはなこ", LocalDate.of(1985, 12, 3));

            // When & Then
            assertNotEquals(dto1, dto2);
            assertNotEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("null値を含むオブジェクトの比較が正常に動作する")
        void testEqualsWithNullValues() {
            // Given
            EmployeeDto dto1 = new EmployeeDto(null, null, null, null, null);
            EmployeeDto dto2 = new EmployeeDto(null, null, null, null, null);

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("自分自身との比較は等しい")
        void testEqualsWithSelf() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 15));

            // When & Then
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("nullとの比較は等しくない")
        void testEqualsWithNull() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 15));

            // When & Then
            assertNotEquals(dto, null);
        }

        @Test
        @DisplayName("異なるクラスとの比較は等しくない")
        void testEqualsWithDifferentClass() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 15));
            String otherObject = "not an EmployeeDto";

            // When & Then
            assertNotEquals(dto, otherObject);
        }

        @Test
        @DisplayName("一部のフィールドが異なる場合の比較")
        void testEqualsWithPartialDifferences() {
            // Given
            EmployeeDto dto1 = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 15));
            EmployeeDto dto2 = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 16)); // birthdayが異なる

            // When & Then
            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("toStringテスト")
    class ToStringTests {

        @Test
        @DisplayName("toStringが正しい形式で出力される")
        void testToString() {
            // Given
            EmployeeDto dto = new EmployeeDto(1L, "EMP001", "田中太郎", "たなかたろう", LocalDate.of(1990, 5, 15));

            // When
            String result = dto.toString();

            // Then
            assertNotNull(result);
            assertTrue(result.contains("EmployeeDto"));
            assertTrue(result.contains("employeeId=1"));
            assertTrue(result.contains("employeeNumber='EMP001'"));
            assertTrue(result.contains("name='田中太郎'"));
            assertTrue(result.contains("furigana='たなかたろう'"));
            assertTrue(result.contains("birthday=1990-05-15"));
        }

        @Test
        @DisplayName("null値を含むtoStringが正常に動作する")
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
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("有効なデータでバリデーションが通る")
        void testValidData() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("田中太郎");
            dto.setFurigana("たなかたろう");
            dto.setBirthday(LocalDate.of(1990, 5, 15));

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("employeeNumberが空の場合バリデーションエラー")
        void testEmptyEmployeeNumber() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("");
            dto.setName("田中太郎");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber")));
        }

        @Test
        @DisplayName("employeeNumberがnullの場合バリデーションエラー")
        void testNullEmployeeNumber() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setName("田中太郎");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber")));
        }

        @Test
        @DisplayName("employeeNumberが長すぎる場合バリデーションエラー")
        void testTooLongEmployeeNumber() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("A".repeat(21)); // 21文字
            dto.setName("田中太郎");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber")));
        }

        @Test
        @DisplayName("employeeNumberに無効な文字が含まれる場合バリデーションエラー")
        void testInvalidEmployeeNumber() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP@001"); // @は無効
            dto.setName("田中太郎");

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber")));
        }

        @Test
        @DisplayName("nameが空の場合バリデーションエラー")
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
        @DisplayName("nameが長すぎる場合バリデーションエラー")
        void testTooLongName() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("A".repeat(101)); // 101文字

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @Test
        @DisplayName("furiganaが長すぎる場合バリデーションエラー")
        void testTooLongFurigana() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("田中太郎");
            dto.setFurigana("あ".repeat(201)); // 201文字

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("furigana")));
        }

        @Test
        @DisplayName("furiganaに無効な文字が含まれる場合バリデーションエラー")
        void testInvalidFurigana() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("田中太郎");
            dto.setFurigana("たなか@たろう"); // @は無効

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("furigana")));
        }

        @Test
        @DisplayName("birthdayが未来の日付の場合バリデーションエラー")
        void testFutureBirthday() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("田中太郎");
            dto.setBirthday(LocalDate.now().plusDays(1)); // 未来の日付

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
        }

        @Test
        @DisplayName("furiganaがnullの場合はバリデーションエラーにならない")
        void testNullFuriganaIsValid() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("田中太郎");
            dto.setFurigana(null);

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("birthdayがnullの場合はバリデーションエラーにならない")
        void testNullBirthdayIsValid() {
            // Given
            EmployeeDto dto = new EmployeeDto();
            dto.setEmployeeNumber("EMP001");
            dto.setName("田中太郎");
            dto.setBirthday(null);

            // When
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto);

            // Then
            assertTrue(violations.isEmpty());
        }
    }
}


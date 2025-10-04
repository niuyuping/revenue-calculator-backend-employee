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
 * 検証制約テスト
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
        employee.setName("田中太郎");
        employee.setFurigana("たなかたろう");
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertTrue(violations.isEmpty(), "有効なデータには検証エラーがあってはいけません");
    }

    @Test
    public void testInvalidEmployeeNumber() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber(""); // 空文字列
        employee.setName("田中太郎");
        employee.setFurigana("たなかたろう");
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertFalse(violations.isEmpty(), "無効な従業員番号には検証エラーがあるべきです");
        
        boolean hasEmployeeNumberError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("employeeNumber"));
        assertTrue(hasEmployeeNumberError, "従業員番号の検証エラーがあるべきです");
    }

    @Test
    public void testInvalidName() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName(""); // 空文字列
        employee.setFurigana("たなかたろう");
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertFalse(violations.isEmpty(), "無効な姓名には検証エラーがあるべきです");
        
        boolean hasNameError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertTrue(hasNameError, "姓名の検証エラーがあるべきです");
    }

    @Test
    public void testInvalidFurigana() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("田中太郎");
        employee.setFurigana("田中123"); // 数字を含み、ふりがなフォーマットに適合しない
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertFalse(violations.isEmpty(), "無効なふりがなには検証エラーがあるべきです");
        
        boolean hasFuriganaError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("furigana"));
        assertTrue(hasFuriganaError, "ふりがなの検証エラーがあるべきです");
    }

    @Test
    public void testInvalidBirthday() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("田中太郎");
        employee.setFurigana("たなかたろう");
        employee.setBirthday(LocalDate.now().plusDays(1)); // 未来の日付

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertFalse(violations.isEmpty(), "無効な生年月日には検証エラーがあるべきです");
        
        boolean hasBirthdayError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday"));
        assertTrue(hasBirthdayError, "生年月日の検証エラーがあるべきです");
    }

    @Test
    public void testValidFurigana() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("田中太郎");
        employee.setFurigana("たなかたろう"); // 有効なひらがな
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertTrue(violations.isEmpty(), "有効なふりがなには検証エラーがあってはいけません");
    }

    @Test
    public void testValidKatakanaFurigana() {
        EmployeeDto employee = new EmployeeDto();
        employee.setEmployeeNumber("EMP001");
        employee.setName("田中太郎");
        employee.setFurigana("タナカタロウ"); // 有効なカタカナ
        employee.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employee);
        assertTrue(violations.isEmpty(), "有効なカタカナには検証エラーがあってはいけません");
    }
}


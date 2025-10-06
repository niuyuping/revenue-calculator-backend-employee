package jp.asatex.revenue_calculator_backend_employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Employee data transfer object
 * Used for API requests and responses
 */
@Schema(description = "Employee information")
public class EmployeeDto {
    
    @Schema(description = "Employee ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long employeeId;
    
    @Schema(description = "Employee number", example = "EMP001", required = true)
    @NotBlank(message = "Employee number cannot be empty")
    @Size(min = 1, max = 20, message = "Employee number length must be between 1-20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Employee number can only contain letters, numbers, underscores, and hyphens")
    private String employeeNumber;
    
    @Schema(description = "Name", example = "Tanaka Taro", required = true)
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 100, message = "Name length must be between 1-100 characters")
    private String name;
    
    @Schema(description = "Furigana", example = "tanaka taro")
    @Size(max = 200, message = "Furigana length cannot exceed 200 characters")
    @Pattern(regexp = "^[\\p{IsHiragana}\\p{IsKatakana}ー\\p{IsLatin}\\s（）()]*$", message = "Furigana can only contain hiragana, katakana, Latin characters, spaces, and parentheses")
    private String furigana;
    
    @Schema(description = "Birthday", example = "1990-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Birthday must be a past date")
    private LocalDate birthday;
    
    // Default constructor
    public EmployeeDto() {}
    
    // All parameters constructor
    public EmployeeDto(Long employeeId, String employeeNumber, String name, String furigana, LocalDate birthday) {
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.furigana = furigana;
        this.birthday = birthday;
    }
    
    // Getter and Setter methods
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeNumber() {
        return employeeNumber;
    }
    
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFurigana() {
        return furigana;
    }
    
    public void setFurigana(String furigana) {
        this.furigana = furigana;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeDto that = (EmployeeDto) o;
        return Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(employeeNumber, that.employeeNumber) &&
                Objects.equals(name, that.name) &&
                Objects.equals(furigana, that.furigana) &&
                Objects.equals(birthday, that.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, employeeNumber, name, furigana, birthday);
    }

    @Override
    public String toString() {
        return "EmployeeDto{" +
                "employeeId=" + employeeId +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", name='" + name + '\'' +
                ", furigana='" + furigana + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}

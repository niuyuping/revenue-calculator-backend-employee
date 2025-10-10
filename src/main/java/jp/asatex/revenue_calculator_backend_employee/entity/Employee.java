package jp.asatex.revenue_calculator_backend_employee.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Employee entity class
 * Contains basic employee information: employee number, name, furigana, birthday
 */
@Table("employeeInfo")
public class Employee {
    
    @Id
    @Column("employee_id")
    private Long employeeId;
    
    @NotBlank(message = "Employee number cannot be empty")
    @Size(min = 1, max = 20, message = "Employee number length must be between 1-20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Employee number can only contain letters, numbers, underscores, and hyphens")
    @Column("employee_number")
    private String employeeNumber;
    
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 100, message = "Name length must be between 1-100 characters")
    @Column("name")
    private String name;
    
    @Size(max = 200, message = "Furigana length cannot exceed 200 characters")
    @Pattern(regexp = "^[\\p{IsHiragana}\\p{IsKatakana}\\p{IsLatin}\\s（）]*$", message = "Furigana can only contain hiragana, katakana, Latin characters, spaces, and parentheses")
    @Column("furigana")
    private String furigana;
    
    @Past(message = "Birthday must be a past date")
    @Column("birthday")
    private LocalDate birthday;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
    
    @Column("deleted_at")
    private LocalDateTime deletedAt;
    
    @Column("deleted_by")
    private String deletedBy;
    
    @Column("is_deleted")
    private Boolean deleted = false;
    
    // Default constructor
    public Employee() {}
    
    // All parameters constructor
    public Employee(Long employeeId, String employeeNumber, String name, String furigana, LocalDate birthday) {
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public String getDeletedBy() {
        return deletedBy;
    }
    
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    
    public boolean isDeleted() {
        return deleted != null && deleted;
    }
    
    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", name='" + name + '\'' +
                ", furigana='" + furigana + '\'' +
                ", birthday=" + birthday +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deletedBy='" + deletedBy + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}

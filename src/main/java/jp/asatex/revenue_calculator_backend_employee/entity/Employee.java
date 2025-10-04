package jp.asatex.revenue_calculator_backend_employee.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Employeeエンティティクラス
 * 従業員の基本情報を含む：従業員番号、姓名、ふりがな、生年月日
 */
@Table("employees")
public class Employee {
    
    @Id
    @Column("employee_id")
    private Long employeeId;
    
    @NotBlank(message = "従業員番号は空にできません")
    @Size(min = 1, max = 20, message = "従業員番号の長さは1-20文字の間である必要があります")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "従業員番号は英字、数字、アンダースコア、ハイフンのみを含むことができます")
    @Column("employee_number")
    private String employeeNumber;
    
    @NotBlank(message = "姓名は空にできません")
    @Size(min = 1, max = 100, message = "姓名の長さは1-100文字の間である必要があります")
    @Column("name")
    private String name;
    
    @Size(max = 200, message = "ふりがなの長さは200文字を超えることはできません")
    @Pattern(regexp = "^[\\p{IsHiragana}\\p{IsKatakana}\\p{IsLatin}\\s（）]*$", message = "ふりがなはひらがな、カタカナ、ラテン文字、スペース、括弧のみを含むことができます")
    @Column("furigana")
    private String furigana;
    
    @Past(message = "生年月日は過去の日付である必要があります")
    @Column("birthday")
    private LocalDate birthday;
    
    // デフォルトコンストラクタ
    public Employee() {}
    
    // 全パラメータコンストラクタ
    public Employee(Long employeeId, String employeeNumber, String name, String furigana, LocalDate birthday) {
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.furigana = furigana;
        this.birthday = birthday;
    }
    
    // GetterとSetterメソッド
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
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", name='" + name + '\'' +
                ", furigana='" + furigana + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}

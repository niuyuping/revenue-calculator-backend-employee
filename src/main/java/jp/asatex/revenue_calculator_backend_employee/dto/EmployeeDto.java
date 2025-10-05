package jp.asatex.revenue_calculator_backend_employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Employeeデータ転送オブジェクト
 * APIリクエストとレスポンスに使用
 */
@Schema(description = "従業員情報")
public class EmployeeDto {
    
    @Schema(description = "従業員ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long employeeId;
    
    @Schema(description = "従業員番号", example = "EMP001", required = true)
    @NotBlank(message = "従業員番号は空にできません")
    @Size(min = 1, max = 20, message = "従業員番号の長さは1-20文字の間である必要があります")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "従業員番号は英字、数字、アンダースコア、ハイフンのみを含むことができます")
    private String employeeNumber;
    
    @Schema(description = "姓名", example = "田中太郎", required = true)
    @NotBlank(message = "姓名は空にできません")
    @Size(min = 1, max = 100, message = "姓名の長さは1-100文字の間である必要があります")
    private String name;
    
    @Schema(description = "ふりがな", example = "たなかたろう")
    @Size(max = 200, message = "ふりがなの長さは200文字を超えることはできません")
    @Pattern(regexp = "^[\\p{IsHiragana}\\p{IsKatakana}ー\\p{IsLatin}\\s（）]*$", message = "ふりがなはひらがな、カタカナ、ラテン文字、スペース、括弧のみを含むことができます")
    private String furigana;
    
    @Schema(description = "生年月日", example = "1990-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "生年月日は過去の日付である必要があります")
    private LocalDate birthday;
    
    // デフォルトコンストラクタ
    public EmployeeDto() {}
    
    // 全パラメータコンストラクタ
    public EmployeeDto(Long employeeId, String employeeNumber, String name, String furigana, LocalDate birthday) {
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

package jp.asatex.revenue_calculator_backend_employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
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
    
    @Schema(description = "Employee number", example = "EMP001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Employee number cannot be empty")
    @Size(min = 1, max = 20, message = "Employee number length must be between 1-20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Employee number can only contain letters, numbers, underscores, and hyphens")
    private String employeeNumber;
    
    @Schema(description = "Name", example = "Tanaka Taro", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 100, message = "Name length must be between 1-100 characters")
    private String name;
    
    @Schema(description = "Furigana", example = "tanaka taro")
    @Size(max = 200, message = "Furigana length cannot exceed 200 characters")
    @Pattern(regexp = "^[\\p{IsHiragana}\\p{IsKatakana}ー\\p{IsLatin}\\s\u3000（）()]*$", message = "Furigana can only contain hiragana, katakana, Latin characters, spaces, and parentheses")
    private String furigana;
    
    @Schema(description = "Birthday", example = "1990-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Birthday must be a past date")
    private LocalDate birthday;
    
    @Schema(description = "Email address", example = "tanaka.taro@example.com")
    @Email(message = "Email format is invalid")
    @Size(max = 255, message = "Email length cannot exceed 255 characters")
    private String email;
    
    @Schema(description = "Basic salary in JPY", example = "300000.00")
    @DecimalMin(value = "0.0", message = "Basic salary must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Basic salary must have at most 10 integer digits and 2 decimal places")
    private BigDecimal basicSalary;
    
    @Schema(description = "Number of dependents for tax calculation", example = "2")
    @Min(value = 0, message = "Dependent count must be non-negative")
    private Integer dependentCount;
    
    @Schema(description = "Whether enrolled in health insurance", example = "true")
    private Boolean healthInsuranceEnrolled;
    
    @Schema(description = "Whether enrolled in welfare pension", example = "true")
    private Boolean welfarePensionEnrolled;
    
    @Schema(description = "Unit price per hour/day in JPY", example = "5000.00")
    @DecimalMin(value = "0.01", message = "Unit price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Unit price must have at most 10 integer digits and 2 decimal places")
    private BigDecimal unitPrice;
    
    @Schema(description = "Individual business owner request amount in JPY", example = "150000.00")
    @DecimalMin(value = "0.0", message = "Individual business amount must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Individual business amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal individualBusinessAmount;
    
    @Schema(description = "Position allowance amount in JPY", example = "50000.00")
    @DecimalMin(value = "0.0", message = "Position allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Position allowance must have at most 10 integer digits and 2 decimal places")
    private BigDecimal positionAllowance;
    
    @Schema(description = "Housing allowance amount in JPY", example = "30000.00")
    @DecimalMin(value = "0.0", message = "Housing allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Housing allowance must have at most 10 integer digits and 2 decimal places")
    private BigDecimal housingAllowance;
    
    @Schema(description = "Family allowance amount in JPY", example = "20000.00")
    @DecimalMin(value = "0.0", message = "Family allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Family allowance must have at most 10 integer digits and 2 decimal places")
    private BigDecimal familyAllowance;
    
    @Schema(description = "Collection fee amount in JPY", example = "5000.00")
    @DecimalMin(value = "0.0", message = "Collection fee amount must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Collection fee amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal collectionFeeAmount;
    
    @Schema(description = "Payment fee amount in JPY", example = "3000.00")
    @DecimalMin(value = "0.0", message = "Payment fee amount must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Payment fee amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal paymentFeeAmount;
    
    @Schema(description = "Third party management rate (0.00-100.00)", example = "5.00")
    @DecimalMin(value = "0.0", message = "Third party management rate must be non-negative")
    @DecimalMax(value = "100.0", message = "Third party management rate must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Third party management rate must have at most 3 integer digits and 2 decimal places")
    private BigDecimal thirdPartyManagementRate;
    
    @Schema(description = "Third party profit distribution rate (0.00-100.00)", example = "3.00")
    @DecimalMin(value = "0.0", message = "Third party profit distribution rate must be non-negative")
    @DecimalMax(value = "100.0", message = "Third party profit distribution rate must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Third party profit distribution rate must have at most 3 integer digits and 2 decimal places")
    private BigDecimal thirdPartyProfitDistributionRate;
    
    @Schema(description = "Phone number with country code", example = "+81-3-1234-5678")
    @Size(max = 20, message = "Phone number length cannot exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Phone number can only contain numbers, spaces, hyphens, parentheses, and optional plus sign")
    private String phoneNumber;
    
    @Schema(description = "Consumption tax rate percentage (0.00-100.00)", example = "10.00")
    @DecimalMin(value = "0.0", message = "Consumption tax rate must be non-negative")
    @DecimalMax(value = "100.0", message = "Consumption tax rate must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Consumption tax rate must have at most 3 integer digits and 2 decimal places")
    private BigDecimal consumptionTaxRate;
    
    @Schema(description = "Non-working deduction amount in JPY", example = "50000.00")
    @DecimalMin(value = "0.0", message = "Non-working deduction must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Non-working deduction must have at most 10 integer digits and 2 decimal places")
    private BigDecimal nonWorkingDeduction;
    
    @Schema(description = "Overtime allowance amount in JPY", example = "25000.00")
    @DecimalMin(value = "0.0", message = "Overtime allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Overtime allowance must have at most 10 integer digits and 2 decimal places")
    private BigDecimal overtimeAllowance;
    
    @Schema(description = "Commuting allowance amount in JPY", example = "15000.00")
    @DecimalMin(value = "0.0", message = "Commuting allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Commuting allowance must have at most 10 integer digits and 2 decimal places")
    private BigDecimal commutingAllowance;
    
    @Schema(description = "Additional remarks or notes about the employee", example = "Special skills: Java, Spring Boot")
    @Size(max = 1000, message = "Remarks length cannot exceed 1000 characters")
    private String remarks;
    
    @Schema(description = "Whether the employee is disabled (for tax deduction)", example = "false")
    private Boolean isDisabled;
    
    @Schema(description = "Whether the employee is a single parent (for tax deduction)", example = "false")
    private Boolean isSingleParent;
    
    @Schema(description = "Whether the employee is a widow (for tax deduction)", example = "false")
    private Boolean isWidow;
    
    @Schema(description = "Whether the employee is a working student (for tax deduction)", example = "false")
    private Boolean isWorkingStudent;
    
    @Schema(description = "Number of disabled dependents (for tax deduction)", example = "0")
    @Min(value = 0, message = "Disabled dependent count must be non-negative")
    private Integer disabledDependentCount;
    
    // Default constructor
    public EmployeeDto() {}
    
    // All parameters constructor
    public EmployeeDto(Long employeeId, String employeeNumber, String name, String furigana, LocalDate birthday, String email, BigDecimal basicSalary, Integer dependentCount, Boolean healthInsuranceEnrolled, Boolean welfarePensionEnrolled, BigDecimal unitPrice, BigDecimal individualBusinessAmount, BigDecimal positionAllowance, BigDecimal housingAllowance, BigDecimal familyAllowance, BigDecimal collectionFeeAmount, BigDecimal paymentFeeAmount, BigDecimal thirdPartyManagementRate, BigDecimal thirdPartyProfitDistributionRate, String phoneNumber, BigDecimal consumptionTaxRate, BigDecimal nonWorkingDeduction, BigDecimal overtimeAllowance, BigDecimal commutingAllowance, String remarks, Boolean isDisabled, Boolean isSingleParent, Boolean isWidow, Boolean isWorkingStudent, Integer disabledDependentCount) {
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.furigana = furigana;
        this.birthday = birthday;
        this.email = email;
        this.basicSalary = basicSalary;
        this.dependentCount = dependentCount;
        this.healthInsuranceEnrolled = healthInsuranceEnrolled;
        this.welfarePensionEnrolled = welfarePensionEnrolled;
        this.unitPrice = unitPrice;
        this.individualBusinessAmount = individualBusinessAmount;
        this.positionAllowance = positionAllowance;
        this.housingAllowance = housingAllowance;
        this.familyAllowance = familyAllowance;
        this.collectionFeeAmount = collectionFeeAmount;
        this.paymentFeeAmount = paymentFeeAmount;
        this.thirdPartyManagementRate = thirdPartyManagementRate;
        this.thirdPartyProfitDistributionRate = thirdPartyProfitDistributionRate;
        this.phoneNumber = phoneNumber;
        this.consumptionTaxRate = consumptionTaxRate;
        this.nonWorkingDeduction = nonWorkingDeduction;
        this.overtimeAllowance = overtimeAllowance;
        this.commutingAllowance = commutingAllowance;
        this.remarks = remarks;
        this.isDisabled = isDisabled;
        this.isSingleParent = isSingleParent;
        this.isWidow = isWidow;
        this.isWorkingStudent = isWorkingStudent;
        this.disabledDependentCount = disabledDependentCount;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public BigDecimal getBasicSalary() {
        return basicSalary;
    }
    
    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }
    
    public Integer getDependentCount() {
        return dependentCount;
    }
    
    public void setDependentCount(Integer dependentCount) {
        this.dependentCount = dependentCount;
    }
    
    public Boolean getHealthInsuranceEnrolled() {
        return healthInsuranceEnrolled;
    }
    
    public void setHealthInsuranceEnrolled(Boolean healthInsuranceEnrolled) {
        this.healthInsuranceEnrolled = healthInsuranceEnrolled;
    }
    
    public Boolean getWelfarePensionEnrolled() {
        return welfarePensionEnrolled;
    }
    
    public void setWelfarePensionEnrolled(Boolean welfarePensionEnrolled) {
        this.welfarePensionEnrolled = welfarePensionEnrolled;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public BigDecimal getIndividualBusinessAmount() {
        return individualBusinessAmount;
    }
    
    public void setIndividualBusinessAmount(BigDecimal individualBusinessAmount) {
        this.individualBusinessAmount = individualBusinessAmount;
    }
    
    public BigDecimal getPositionAllowance() {
        return positionAllowance;
    }
    
    public void setPositionAllowance(BigDecimal positionAllowance) {
        this.positionAllowance = positionAllowance;
    }
    
    public BigDecimal getHousingAllowance() {
        return housingAllowance;
    }
    
    public void setHousingAllowance(BigDecimal housingAllowance) {
        this.housingAllowance = housingAllowance;
    }
    
    public BigDecimal getFamilyAllowance() {
        return familyAllowance;
    }
    
    public void setFamilyAllowance(BigDecimal familyAllowance) {
        this.familyAllowance = familyAllowance;
    }
    
    public BigDecimal getCollectionFeeAmount() {
        return collectionFeeAmount;
    }
    
    public void setCollectionFeeAmount(BigDecimal collectionFeeAmount) {
        this.collectionFeeAmount = collectionFeeAmount;
    }
    
    public BigDecimal getPaymentFeeAmount() {
        return paymentFeeAmount;
    }
    
    public void setPaymentFeeAmount(BigDecimal paymentFeeAmount) {
        this.paymentFeeAmount = paymentFeeAmount;
    }
    
    public BigDecimal getThirdPartyManagementRate() {
        return thirdPartyManagementRate;
    }
    
    public void setThirdPartyManagementRate(BigDecimal thirdPartyManagementRate) {
        this.thirdPartyManagementRate = thirdPartyManagementRate;
    }
    
    public BigDecimal getThirdPartyProfitDistributionRate() {
        return thirdPartyProfitDistributionRate;
    }
    
    public void setThirdPartyProfitDistributionRate(BigDecimal thirdPartyProfitDistributionRate) {
        this.thirdPartyProfitDistributionRate = thirdPartyProfitDistributionRate;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public BigDecimal getConsumptionTaxRate() {
        return consumptionTaxRate;
    }
    
    public void setConsumptionTaxRate(BigDecimal consumptionTaxRate) {
        this.consumptionTaxRate = consumptionTaxRate;
    }
    
    public BigDecimal getNonWorkingDeduction() {
        return nonWorkingDeduction;
    }
    
    public void setNonWorkingDeduction(BigDecimal nonWorkingDeduction) {
        this.nonWorkingDeduction = nonWorkingDeduction;
    }
    
    public BigDecimal getOvertimeAllowance() {
        return overtimeAllowance;
    }
    
    public void setOvertimeAllowance(BigDecimal overtimeAllowance) {
        this.overtimeAllowance = overtimeAllowance;
    }
    
    public BigDecimal getCommutingAllowance() {
        return commutingAllowance;
    }
    
    public void setCommutingAllowance(BigDecimal commutingAllowance) {
        this.commutingAllowance = commutingAllowance;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Boolean getIsDisabled() {
        return isDisabled;
    }
    
    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
    
    public Boolean getIsSingleParent() {
        return isSingleParent;
    }
    
    public void setIsSingleParent(Boolean isSingleParent) {
        this.isSingleParent = isSingleParent;
    }
    
    public Boolean getIsWidow() {
        return isWidow;
    }
    
    public void setIsWidow(Boolean isWidow) {
        this.isWidow = isWidow;
    }
    
    public Boolean getIsWorkingStudent() {
        return isWorkingStudent;
    }
    
    public void setIsWorkingStudent(Boolean isWorkingStudent) {
        this.isWorkingStudent = isWorkingStudent;
    }
    
    public Integer getDisabledDependentCount() {
        return disabledDependentCount;
    }
    
    public void setDisabledDependentCount(Integer disabledDependentCount) {
        this.disabledDependentCount = disabledDependentCount;
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
                Objects.equals(birthday, that.birthday) &&
                Objects.equals(email, that.email) &&
                Objects.equals(basicSalary, that.basicSalary) &&
                Objects.equals(dependentCount, that.dependentCount) &&
                Objects.equals(healthInsuranceEnrolled, that.healthInsuranceEnrolled) &&
                Objects.equals(welfarePensionEnrolled, that.welfarePensionEnrolled) &&
                Objects.equals(unitPrice, that.unitPrice) &&
                Objects.equals(individualBusinessAmount, that.individualBusinessAmount) &&
                Objects.equals(positionAllowance, that.positionAllowance) &&
                Objects.equals(housingAllowance, that.housingAllowance) &&
                Objects.equals(familyAllowance, that.familyAllowance) &&
                Objects.equals(collectionFeeAmount, that.collectionFeeAmount) &&
                Objects.equals(paymentFeeAmount, that.paymentFeeAmount) &&
                Objects.equals(thirdPartyManagementRate, that.thirdPartyManagementRate) &&
                Objects.equals(thirdPartyProfitDistributionRate, that.thirdPartyProfitDistributionRate) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(consumptionTaxRate, that.consumptionTaxRate) &&
                Objects.equals(nonWorkingDeduction, that.nonWorkingDeduction) &&
                Objects.equals(overtimeAllowance, that.overtimeAllowance) &&
                Objects.equals(commutingAllowance, that.commutingAllowance) &&
                Objects.equals(remarks, that.remarks) &&
                Objects.equals(isDisabled, that.isDisabled) &&
                Objects.equals(isSingleParent, that.isSingleParent) &&
                Objects.equals(isWidow, that.isWidow) &&
                Objects.equals(isWorkingStudent, that.isWorkingStudent) &&
                Objects.equals(disabledDependentCount, that.disabledDependentCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, employeeNumber, name, furigana, birthday, email, basicSalary, dependentCount, healthInsuranceEnrolled, welfarePensionEnrolled, unitPrice, individualBusinessAmount, positionAllowance, housingAllowance, familyAllowance, collectionFeeAmount, paymentFeeAmount, thirdPartyManagementRate, thirdPartyProfitDistributionRate, phoneNumber, consumptionTaxRate, nonWorkingDeduction, overtimeAllowance, commutingAllowance, remarks, isDisabled, isSingleParent, isWidow, isWorkingStudent, disabledDependentCount);
    }

    @Override
    public String toString() {
        return "EmployeeDto{" +
                "employeeId=" + employeeId +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", name='" + name + '\'' +
                ", furigana='" + furigana + '\'' +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", basicSalary=" + basicSalary +
                ", dependentCount=" + dependentCount +
                ", healthInsuranceEnrolled=" + healthInsuranceEnrolled +
                ", welfarePensionEnrolled=" + welfarePensionEnrolled +
                ", unitPrice=" + unitPrice +
                ", individualBusinessAmount=" + individualBusinessAmount +
                ", positionAllowance=" + positionAllowance +
                ", housingAllowance=" + housingAllowance +
                ", familyAllowance=" + familyAllowance +
                ", collectionFeeAmount=" + collectionFeeAmount +
                ", paymentFeeAmount=" + paymentFeeAmount +
                ", thirdPartyManagementRate=" + thirdPartyManagementRate +
                ", thirdPartyProfitDistributionRate=" + thirdPartyProfitDistributionRate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", consumptionTaxRate=" + consumptionTaxRate +
                ", nonWorkingDeduction=" + nonWorkingDeduction +
                ", overtimeAllowance=" + overtimeAllowance +
                ", commutingAllowance=" + commutingAllowance +
                ", remarks='" + remarks + '\'' +
                ", isDisabled=" + isDisabled +
                ", isSingleParent=" + isSingleParent +
                ", isWidow=" + isWidow +
                ", isWorkingStudent=" + isWorkingStudent +
                ", disabledDependentCount=" + disabledDependentCount +
                '}';
    }
}

package jp.asatex.revenue_calculator_backend_employee.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Employee entity class
 * Contains employee information: basic info, contact, salary, insurance, payment details, allowances and fees
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
    @Pattern(regexp = "^[\\p{IsHiragana}\\p{IsKatakana}ー\\p{IsLatin}\\s\u3000（）()]*$", message = "Furigana can only contain hiragana, katakana, Latin characters, spaces, and parentheses")
    @Column("furigana")
    private String furigana;
    
    @Past(message = "Birthday must be a past date")
    @Column("birthday")
    private LocalDate birthday;
    
    @Email(message = "Email format is invalid")
    @Size(max = 255, message = "Email length cannot exceed 255 characters")
    @Column("email")
    private String email;
    
    @DecimalMin(value = "0.0", message = "Basic salary must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Basic salary must have at most 10 integer digits and 2 decimal places")
    @Column("basic_salary")
    private BigDecimal basicSalary;
    
    @Min(value = 0, message = "Dependent count must be non-negative")
    @Column("dependent_count")
    private Integer dependentCount;
    
    @Column("no_health_insurance")
    private Boolean noHealthInsurance;
    
    @Column("no_pension_insurance")
    private Boolean noPensionInsurance;
    
    @DecimalMin(value = "0.01", message = "Unit price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Unit price must have at most 10 integer digits and 2 decimal places")
    @Column("unit_price")
    private BigDecimal unitPrice;
    
    @DecimalMin(value = "0.0", message = "Individual business amount must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Individual business amount must have at most 10 integer digits and 2 decimal places")
    @Column("individual_business_amount")
    private BigDecimal individualBusinessAmount;
    
    @DecimalMin(value = "0.0", message = "Position allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Position allowance must have at most 10 integer digits and 2 decimal places")
    @Column("position_allowance")
    private BigDecimal positionAllowance;
    
    @DecimalMin(value = "0.0", message = "Housing allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Housing allowance must have at most 10 integer digits and 2 decimal places")
    @Column("housing_allowance")
    private BigDecimal housingAllowance;
    
    @DecimalMin(value = "0.0", message = "Family allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Family allowance must have at most 10 integer digits and 2 decimal places")
    @Column("family_allowance")
    private BigDecimal familyAllowance;
    
    @DecimalMin(value = "0.0", message = "Collection fee amount must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Collection fee amount must have at most 10 integer digits and 2 decimal places")
    @Column("collection_fee_amount")
    private BigDecimal collectionFeeAmount;
    
    @DecimalMin(value = "0.0", message = "Payment fee amount must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Payment fee amount must have at most 10 integer digits and 2 decimal places")
    @Column("payment_fee_amount")
    private BigDecimal paymentFeeAmount;
    
    @DecimalMin(value = "0.0", message = "Third party management rate must be non-negative")
    @DecimalMax(value = "100.0", message = "Third party management rate must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Third party management rate must have at most 3 integer digits and 2 decimal places")
    @Column("third_party_management_rate")
    private BigDecimal thirdPartyManagementRate;
    
    @DecimalMin(value = "0.0", message = "Third party profit distribution rate must be non-negative")
    @DecimalMax(value = "100.0", message = "Third party profit distribution rate must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Third party profit distribution rate must have at most 3 integer digits and 2 decimal places")
    @Column("third_party_profit_distribution_rate")
    private BigDecimal thirdPartyProfitDistributionRate;
    
    @Size(max = 20, message = "Phone number length cannot exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Phone number can only contain numbers, spaces, hyphens, parentheses, and optional plus sign")
    @Column("phone_number")
    private String phoneNumber;
    
    @DecimalMin(value = "0.0", message = "Consumption tax rate must be non-negative")
    @DecimalMax(value = "100.0", message = "Consumption tax rate must not exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Consumption tax rate must have at most 3 integer digits and 2 decimal places")
    @Column("consumption_tax_rate")
    private BigDecimal consumptionTaxRate;
    
    @DecimalMin(value = "0.0", message = "Non-working deduction must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Non-working deduction must have at most 10 integer digits and 2 decimal places")
    @Column("non_working_deduction")
    private BigDecimal nonWorkingDeduction;
    
    @DecimalMin(value = "0.0", message = "Overtime allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Overtime allowance must have at most 10 integer digits and 2 decimal places")
    @Column("overtime_allowance")
    private BigDecimal overtimeAllowance;
    
    @DecimalMin(value = "0.0", message = "Commuting allowance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Commuting allowance must have at most 10 integer digits and 2 decimal places")
    @Column("commuting_allowance")
    private BigDecimal commutingAllowance;
    
    @Size(max = 1000, message = "Remarks length cannot exceed 1000 characters")
    @Column("remarks")
    private String remarks;
    
    @Column("is_disabled")
    private Boolean isDisabled;
    
    @Column("is_single_parent")
    private Boolean isSingleParent;
    
    @Column("is_widow")
    private Boolean isWidow;
    
    @Column("is_working_student")
    private Boolean isWorkingStudent;
    
    @Min(value = 0, message = "Disabled dependent count must be non-negative")
    @Column("disabled_dependent_count")
    private Integer disabledDependentCount;
    
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
    public Employee(Long employeeId, String employeeNumber, String name, String furigana, LocalDate birthday, String email, BigDecimal basicSalary, Integer dependentCount, Boolean noHealthInsurance, Boolean noPensionInsurance, BigDecimal unitPrice, BigDecimal individualBusinessAmount, BigDecimal positionAllowance, BigDecimal housingAllowance, BigDecimal familyAllowance, BigDecimal collectionFeeAmount, BigDecimal paymentFeeAmount, BigDecimal thirdPartyManagementRate, BigDecimal thirdPartyProfitDistributionRate, String phoneNumber, BigDecimal consumptionTaxRate, BigDecimal nonWorkingDeduction, BigDecimal overtimeAllowance, BigDecimal commutingAllowance, String remarks, Boolean isDisabled, Boolean isSingleParent, Boolean isWidow, Boolean isWorkingStudent, Integer disabledDependentCount) {
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.furigana = furigana;
        this.birthday = birthday;
        this.email = email;
        this.basicSalary = basicSalary;
        this.dependentCount = dependentCount;
        this.noHealthInsurance = noHealthInsurance;
        this.noPensionInsurance = noPensionInsurance;
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
    
    public Boolean getNoHealthInsurance() {
        return noHealthInsurance;
    }
    
    public void setNoHealthInsurance(Boolean noHealthInsurance) {
        this.noHealthInsurance = noHealthInsurance;
    }
    
    public Boolean getNoPensionInsurance() {
        return noPensionInsurance;
    }
    
    public void setNoPensionInsurance(Boolean noPensionInsurance) {
        this.noPensionInsurance = noPensionInsurance;
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
                ", email='" + email + '\'' +
                ", basicSalary=" + basicSalary +
                ", dependentCount=" + dependentCount +
                ", noHealthInsurance=" + noHealthInsurance +
                ", noPensionInsurance=" + noPensionInsurance +
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
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ", deletedBy='" + deletedBy + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}

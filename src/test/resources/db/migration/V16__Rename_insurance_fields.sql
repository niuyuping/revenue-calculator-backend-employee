-- Rename insurance enrollment fields to reflect "not enrolled" logic
-- healthInsuranceEnrolled -> noHealthInsurance (default false = enrolled)
-- welfarePensionEnrolled -> noPensionInsurance (default false = enrolled)

-- Rename columns
ALTER TABLE employeeInfo RENAME COLUMN health_insurance_enrolled TO no_health_insurance;
ALTER TABLE employeeInfo RENAME COLUMN welfare_pension_enrolled TO no_pension_insurance;

-- Update indexes
DROP INDEX IF EXISTS idx_employeeInfo_health_insurance_enrolled;
DROP INDEX IF EXISTS idx_employeeInfo_welfare_pension_enrolled;

CREATE INDEX IF NOT EXISTS idx_employeeInfo_no_health_insurance ON employeeInfo(no_health_insurance);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_no_pension_insurance ON employeeInfo(no_pension_insurance);

-- Update comments
COMMENT ON COLUMN employeeInfo.no_health_insurance IS 'Whether the employee is NOT enrolled in health insurance (false = enrolled, true = not enrolled)';
COMMENT ON COLUMN employeeInfo.no_pension_insurance IS 'Whether the employee is NOT enrolled in pension insurance (false = enrolled, true = not enrolled)';

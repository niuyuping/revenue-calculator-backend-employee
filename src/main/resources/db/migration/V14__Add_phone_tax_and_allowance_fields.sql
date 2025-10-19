-- Add phone number, consumption tax rate, non-working deduction, overtime allowance, commuting allowance, and remarks fields
-- This migration adds 6 new fields to the employeeInfo table

-- Add phone number field (VARCHAR for phone numbers with country code)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20);

-- Add consumption tax rate field (percentage, using DECIMAL for precise values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS consumption_tax_rate DECIMAL(5,2);

-- Add non-working deduction field (amount in JPY, using DECIMAL for precise monetary values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS non_working_deduction DECIMAL(12,2);

-- Add overtime allowance field (amount in JPY, using DECIMAL for precise monetary values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS overtime_allowance DECIMAL(12,2);

-- Add commuting allowance field (amount in JPY, using DECIMAL for precise monetary values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS commuting_allowance DECIMAL(12,2);

-- Add remarks field (TEXT for longer text content)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS remarks TEXT;

-- Create indexes for new fields that might be searched frequently
CREATE INDEX IF NOT EXISTS idx_employeeInfo_phone_number ON employeeInfo(phone_number);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_consumption_tax_rate ON employeeInfo(consumption_tax_rate);

-- Add constraints for monetary fields (non-negative)
ALTER TABLE employeeInfo ADD CONSTRAINT chk_non_working_deduction_non_negative 
CHECK (non_working_deduction IS NULL OR non_working_deduction >= 0);

ALTER TABLE employeeInfo ADD CONSTRAINT chk_overtime_allowance_non_negative 
CHECK (overtime_allowance IS NULL OR overtime_allowance >= 0);

ALTER TABLE employeeInfo ADD CONSTRAINT chk_commuting_allowance_non_negative 
CHECK (commuting_allowance IS NULL OR commuting_allowance >= 0);

-- Add constraint for consumption tax rate (0-100% range)
ALTER TABLE employeeInfo ADD CONSTRAINT chk_consumption_tax_rate_range 
CHECK (consumption_tax_rate IS NULL OR (consumption_tax_rate >= 0 AND consumption_tax_rate <= 100));

-- Add constraint for phone number format (basic validation)
ALTER TABLE employeeInfo ADD CONSTRAINT chk_phone_number_format 
CHECK (phone_number IS NULL OR phone_number ~ '^[+]?[0-9\s\-\(\)]+$');

-- Add comments for documentation
COMMENT ON COLUMN employeeInfo.phone_number IS 'Phone number with country code (e.g., +81-3-1234-5678)';
COMMENT ON COLUMN employeeInfo.consumption_tax_rate IS 'Consumption tax rate percentage (0.00-100.00)';
COMMENT ON COLUMN employeeInfo.non_working_deduction IS 'Non-working deduction amount in JPY';
COMMENT ON COLUMN employeeInfo.overtime_allowance IS 'Overtime allowance amount in JPY';
COMMENT ON COLUMN employeeInfo.commuting_allowance IS 'Commuting allowance amount in JPY';
COMMENT ON COLUMN employeeInfo.remarks IS 'Additional remarks or notes about the employee';

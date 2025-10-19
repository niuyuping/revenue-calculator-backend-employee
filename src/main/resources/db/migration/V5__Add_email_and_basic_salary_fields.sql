-- Add email and basic salary fields to employeeInfo table
-- Add email field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS email VARCHAR(255);

-- Add basic salary field (using DECIMAL for precise monetary values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS basic_salary DECIMAL(12,2);

-- Create indexes for new fields
CREATE INDEX IF NOT EXISTS idx_employeeInfo_email ON employeeInfo(email);

-- Add constraints for email field
ALTER TABLE employeeInfo ADD CONSTRAINT chk_email_format 
CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Add constraints for basic salary field
ALTER TABLE employeeInfo ADD CONSTRAINT chk_basic_salary_positive 
CHECK (basic_salary IS NULL OR basic_salary >= 0);

-- Add comment for documentation
COMMENT ON COLUMN employeeInfo.email IS 'Employee email address';
COMMENT ON COLUMN employeeInfo.basic_salary IS 'Employee basic salary in JPY';

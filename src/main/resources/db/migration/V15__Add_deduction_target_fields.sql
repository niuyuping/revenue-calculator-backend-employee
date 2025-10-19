-- Add deduction target related fields for tax calculation
-- These fields are used for Japanese tax deduction calculations

-- Add boolean fields for deduction target status
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS is_disabled BOOLEAN DEFAULT FALSE;
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS is_single_parent BOOLEAN DEFAULT FALSE;
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS is_widow BOOLEAN DEFAULT FALSE;
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS is_working_student BOOLEAN DEFAULT FALSE;

-- Add count field for disabled dependents
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS disabled_dependent_count INTEGER DEFAULT 0;

-- Add indexes for new fields
CREATE INDEX IF NOT EXISTS idx_employeeInfo_is_disabled ON employeeInfo(is_disabled);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_is_single_parent ON employeeInfo(is_single_parent);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_is_widow ON employeeInfo(is_widow);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_is_working_student ON employeeInfo(is_working_student);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_disabled_dependent_count ON employeeInfo(disabled_dependent_count);

-- Add constraints for new fields
ALTER TABLE employeeInfo ADD CONSTRAINT chk_disabled_dependent_count_non_negative
CHECK (disabled_dependent_count IS NULL OR disabled_dependent_count >= 0);

-- Add comments for documentation
COMMENT ON COLUMN employeeInfo.is_disabled IS 'Whether the employee is disabled (for tax deduction)';
COMMENT ON COLUMN employeeInfo.is_single_parent IS 'Whether the employee is a single parent (for tax deduction)';
COMMENT ON COLUMN employeeInfo.is_widow IS 'Whether the employee is a widow (for tax deduction)';
COMMENT ON COLUMN employeeInfo.is_working_student IS 'Whether the employee is a working student (for tax deduction)';
COMMENT ON COLUMN employeeInfo.disabled_dependent_count IS 'Number of disabled dependents (for tax deduction)';

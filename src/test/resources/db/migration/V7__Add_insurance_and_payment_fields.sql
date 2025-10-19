-- Add insurance and payment related fields to employeeInfo table
-- Add dependent count field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS dependent_count INTEGER DEFAULT 0;

-- Add health insurance enrollment field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS health_insurance_enrolled BOOLEAN DEFAULT FALSE;

-- Add welfare pension enrollment field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS welfare_pension_enrolled BOOLEAN DEFAULT FALSE;

-- Add unit price field (using DECIMAL for precise monetary values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS unit_price DECIMAL(12,2);

-- Add individual business owner request amount field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS individual_business_amount DECIMAL(12,2);

-- Create indexes for new fields
CREATE INDEX IF NOT EXISTS idx_employeeInfo_dependent_count ON employeeInfo(dependent_count);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_health_insurance ON employeeInfo(health_insurance_enrolled);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_welfare_pension ON employeeInfo(welfare_pension_enrolled);

-- Add constraints for dependent count field
ALTER TABLE employeeInfo ADD CONSTRAINT chk_dependent_count_non_negative 
CHECK (dependent_count IS NULL OR dependent_count >= 0);

-- Add constraints for unit price field
ALTER TABLE employeeInfo ADD CONSTRAINT chk_unit_price_positive 
CHECK (unit_price IS NULL OR unit_price > 0);

-- Add constraints for individual business amount field
ALTER TABLE employeeInfo ADD CONSTRAINT chk_individual_business_amount_non_negative 
CHECK (individual_business_amount IS NULL OR individual_business_amount >= 0);

-- Add comments for documentation
COMMENT ON COLUMN employeeInfo.dependent_count IS 'Number of dependents for tax calculation';
COMMENT ON COLUMN employeeInfo.health_insurance_enrolled IS 'Whether enrolled in health insurance';
COMMENT ON COLUMN employeeInfo.welfare_pension_enrolled IS 'Whether enrolled in welfare pension';
COMMENT ON COLUMN employeeInfo.unit_price IS 'Unit price per hour/day in JPY';
COMMENT ON COLUMN employeeInfo.individual_business_amount IS 'Individual business owner request amount in JPY';

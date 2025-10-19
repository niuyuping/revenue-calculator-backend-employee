-- Add allowance and fee related fields to employeeInfo table
-- Add position allowance field (using DECIMAL for precise monetary values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS position_allowance DECIMAL(12,2);

-- Add housing allowance field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS housing_allowance DECIMAL(12,2);

-- Add family allowance field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS family_allowance DECIMAL(12,2);

-- Add collection fee amount field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS collection_fee_amount DECIMAL(12,2);

-- Add payment fee amount field
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS payment_fee_amount DECIMAL(12,2);

-- Add third party management rate field (percentage, using DECIMAL for precise values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS third_party_management_rate DECIMAL(5,4);

-- Add third party profit_distribution_rate field (percentage, using DECIMAL for precise values)
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS third_party_profit_distribution_rate DECIMAL(5,4);

-- Create indexes for new fields
CREATE INDEX IF NOT EXISTS idx_employeeInfo_position_allowance ON employeeInfo(position_allowance);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_housing_allowance ON employeeInfo(housing_allowance);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_family_allowance ON employeeInfo(family_allowance);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_collection_fee ON employeeInfo(collection_fee_amount);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_payment_fee ON employeeInfo(payment_fee_amount);

-- Add constraints for allowance fields (non-negative)
ALTER TABLE employeeInfo ADD CONSTRAINT chk_position_allowance_non_negative 
CHECK (position_allowance IS NULL OR position_allowance >= 0);

ALTER TABLE employeeInfo ADD CONSTRAINT chk_housing_allowance_non_negative 
CHECK (housing_allowance IS NULL OR housing_allowance >= 0);

ALTER TABLE employeeInfo ADD CONSTRAINT chk_family_allowance_non_negative 
CHECK (family_allowance IS NULL OR family_allowance >= 0);

-- Add constraints for fee amount fields (non-negative)
ALTER TABLE employeeInfo ADD CONSTRAINT chk_collection_fee_non_negative 
CHECK (collection_fee_amount IS NULL OR collection_fee_amount >= 0);

ALTER TABLE employeeInfo ADD CONSTRAINT chk_payment_fee_non_negative 
CHECK (payment_fee_amount IS NULL OR payment_fee_amount >= 0);

-- Add constraints for rate fields (0-100% range)
ALTER TABLE employeeInfo ADD CONSTRAINT chk_third_party_management_rate_range 
CHECK (third_party_management_rate IS NULL OR (third_party_management_rate >= 0 AND third_party_management_rate <= 1));

ALTER TABLE employeeInfo ADD CONSTRAINT chk_third_party_profit_distribution_rate_range 
CHECK (third_party_profit_distribution_rate IS NULL OR (third_party_profit_distribution_rate >= 0 AND third_party_profit_distribution_rate <= 1));

-- Add comments for documentation
COMMENT ON COLUMN employeeInfo.position_allowance IS 'Position allowance amount in JPY';
COMMENT ON COLUMN employeeInfo.housing_allowance IS 'Housing allowance amount in JPY';
COMMENT ON COLUMN employeeInfo.family_allowance IS 'Family allowance amount in JPY';
COMMENT ON COLUMN employeeInfo.collection_fee_amount IS 'Collection fee amount in JPY';
COMMENT ON COLUMN employeeInfo.payment_fee_amount IS 'Payment fee amount in JPY';
COMMENT ON COLUMN employeeInfo.third_party_management_rate IS 'Third party management rate (0.0000-1.0000)';
COMMENT ON COLUMN employeeInfo.third_party_profit_distribution_rate IS 'Third party profit distribution rate (0.0000-1.0000)';

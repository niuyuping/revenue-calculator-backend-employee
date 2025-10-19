-- Fix third party rate constraints to use 0-100 range instead of 0-1
-- This migration removes the old constraints and adds new ones with correct range

-- Drop existing constraints for third party rates
ALTER TABLE employeeInfo DROP CONSTRAINT IF EXISTS chk_third_party_management_rate_range;
ALTER TABLE employeeInfo DROP CONSTRAINT IF EXISTS chk_third_party_profit_distribution_rate_range;

-- Add new constraints for rate fields (0-100% range)
ALTER TABLE employeeInfo ADD CONSTRAINT chk_third_party_management_rate_range 
CHECK (third_party_management_rate IS NULL OR (third_party_management_rate >= 0 AND third_party_management_rate <= 100));

ALTER TABLE employeeInfo ADD CONSTRAINT chk_third_party_profit_distribution_rate_range 
CHECK (third_party_profit_distribution_rate IS NULL OR (third_party_profit_distribution_rate >= 0 AND third_party_profit_distribution_rate <= 100));

-- Update column comments to reflect the correct range
COMMENT ON COLUMN employeeInfo.third_party_management_rate IS 'Third party management rate (0.00-100.00)';
COMMENT ON COLUMN employeeInfo.third_party_profit_distribution_rate IS 'Third party profit distribution rate (0.00-100.00)';

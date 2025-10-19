-- Fix third party rate column precision to support percentage format (0-100.00)
-- Change from DECIMAL(5,4) to DECIMAL(5,2) to support values like 10.00, 50.00

-- Update third party management rate column precision
ALTER TABLE employeeInfo ALTER COLUMN third_party_management_rate TYPE DECIMAL(5,2);

-- Update third party profit distribution rate column precision  
ALTER TABLE employeeInfo ALTER COLUMN third_party_profit_distribution_rate TYPE DECIMAL(5,2);

-- Update column comments to reflect the correct precision
COMMENT ON COLUMN employeeInfo.third_party_management_rate IS 'Third party management rate (0.00-100.00)';
COMMENT ON COLUMN employeeInfo.third_party_profit_distribution_rate IS 'Third party profit distribution rate (0.00-100.00)';

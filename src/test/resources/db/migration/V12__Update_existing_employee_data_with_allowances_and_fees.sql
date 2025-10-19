-- Update existing employee data with allowance and fee information
-- This script updates the sample data that was inserted in V2
-- Note: Rate values are now in percentage format (0-100) instead of decimal (0-1)

-- Update employee data with allowance and fee information
UPDATE employeeInfo SET 
    position_allowance = 50000.00,
    housing_allowance = 30000.00,
    family_allowance = 20000.00,
    collection_fee_amount = 5000.00,
    payment_fee_amount = 3000.00,
    third_party_management_rate = 5.00,
    third_party_profit_distribution_rate = 3.00
WHERE employee_number = 'EMP001';

UPDATE employeeInfo SET 
    position_allowance = 60000.00,
    housing_allowance = 35000.00,
    family_allowance = 25000.00,
    collection_fee_amount = 6000.00,
    payment_fee_amount = 3500.00,
    third_party_management_rate = 6.00,
    third_party_profit_distribution_rate = 4.00
WHERE employee_number = 'EMP002';

UPDATE employeeInfo SET 
    position_allowance = 40000.00,
    housing_allowance = 25000.00,
    family_allowance = 15000.00,
    collection_fee_amount = 4000.00,
    payment_fee_amount = 2500.00,
    third_party_management_rate = 4.00,
    third_party_profit_distribution_rate = 2.50
WHERE employee_number = 'EMP003';

UPDATE employeeInfo SET 
    position_allowance = 55000.00,
    housing_allowance = 32000.00,
    family_allowance = 22000.00,
    collection_fee_amount = 5500.00,
    payment_fee_amount = 3200.00,
    third_party_management_rate = 5.50,
    third_party_profit_distribution_rate = 3.50
WHERE employee_number = 'EMP004';

UPDATE employeeInfo SET 
    position_allowance = 45000.00,
    housing_allowance = 28000.00,
    family_allowance = 18000.00,
    collection_fee_amount = 4500.00,
    payment_fee_amount = 2800.00,
    third_party_management_rate = 4.50,
    third_party_profit_distribution_rate = 2.80
WHERE employee_number = 'EMP005';

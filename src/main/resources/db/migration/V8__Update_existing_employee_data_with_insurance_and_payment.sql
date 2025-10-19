-- Update existing employee data with insurance and payment information
-- This script updates the sample data that was inserted in V2

-- Update employee data with insurance and payment information
UPDATE employeeInfo SET 
    dependent_count = 2,
    health_insurance_enrolled = true,
    welfare_pension_enrolled = true,
    unit_price = 5000.00,
    individual_business_amount = 150000.00
WHERE employee_number = 'EMP001';

UPDATE employeeInfo SET 
    dependent_count = 1,
    health_insurance_enrolled = true,
    welfare_pension_enrolled = true,
    unit_price = 6000.00,
    individual_business_amount = 180000.00
WHERE employee_number = 'EMP002';

UPDATE employeeInfo SET 
    dependent_count = 0,
    health_insurance_enrolled = false,
    welfare_pension_enrolled = false,
    unit_price = 4000.00,
    individual_business_amount = 120000.00
WHERE employee_number = 'EMP003';

UPDATE employeeInfo SET 
    dependent_count = 3,
    health_insurance_enrolled = true,
    welfare_pension_enrolled = true,
    unit_price = 5500.00,
    individual_business_amount = 165000.00
WHERE employee_number = 'EMP004';

UPDATE employeeInfo SET 
    dependent_count = 1,
    health_insurance_enrolled = true,
    welfare_pension_enrolled = false,
    unit_price = 4500.00,
    individual_business_amount = 135000.00
WHERE employee_number = 'EMP005';

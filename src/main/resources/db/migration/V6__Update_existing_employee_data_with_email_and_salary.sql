-- Update existing employee data with email and basic salary
-- This script updates the sample data that was inserted in V2

-- Update employee data with email and basic salary
UPDATE employeeInfo SET 
    email = 'tanaka.taro@example.com',
    basic_salary = 350000.00
WHERE employee_number = 'EMP001';

UPDATE employeeInfo SET 
    email = 'sato.hanako@example.com',
    basic_salary = 420000.00
WHERE employee_number = 'EMP002';

UPDATE employeeInfo SET 
    email = 'suzuki.ichiro@example.com',
    basic_salary = 280000.00
WHERE employee_number = 'EMP003';

UPDATE employeeInfo SET 
    email = 'takahashi.misaki@example.com',
    basic_salary = 380000.00
WHERE employee_number = 'EMP004';

UPDATE employeeInfo SET 
    email = 'yamada.jiro@example.com',
    basic_salary = 320000.00
WHERE employee_number = 'EMP005';

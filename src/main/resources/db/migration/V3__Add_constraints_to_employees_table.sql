-- Add constraints to employeeInfo table
-- Add check constraints

-- Employee number format constraint
ALTER TABLE employeeInfo ADD CONSTRAINT chk_employee_number_format 
CHECK (employee_number ~ '^[A-Za-z0-9_-]+$');

-- Employee number length constraint
ALTER TABLE employeeInfo ADD CONSTRAINT chk_employee_number_length 
CHECK (LENGTH(employee_number) >= 1 AND LENGTH(employee_number) <= 20);

-- Name length constraint
ALTER TABLE employeeInfo ADD CONSTRAINT chk_name_length 
CHECK (LENGTH(name) >= 1 AND LENGTH(name) <= 100);

-- Furigana length constraint
ALTER TABLE employeeInfo ADD CONSTRAINT chk_furigana_length 
CHECK (furigana IS NULL OR LENGTH(furigana) <= 200);

-- Birthday constraint (must be a past date)
ALTER TABLE employeeInfo ADD CONSTRAINT chk_birthday_past 
CHECK (birthday IS NULL OR birthday < CURRENT_DATE);

-- Add update time trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create update time trigger
CREATE TRIGGER update_employeeInfo_updated_at 
    BEFORE UPDATE ON employeeInfo 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

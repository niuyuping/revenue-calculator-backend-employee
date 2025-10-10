-- Create employeeInfo table
CREATE TABLE IF NOT EXISTS employeeInfo (
    employee_id BIGSERIAL PRIMARY KEY,
    employee_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    furigana VARCHAR(200),
    birthday DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_employeeInfo_employee_number ON employeeInfo(employee_number);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_name ON employeeInfo(name);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_furigana ON employeeInfo(furigana);

-- 为员工表添加约束
-- 添加检查约束

-- 员工编号格式约束
ALTER TABLE employees ADD CONSTRAINT chk_employee_number_format 
CHECK (employee_number ~ '^[A-Za-z0-9_-]+$');

-- 员工编号长度约束
ALTER TABLE employees ADD CONSTRAINT chk_employee_number_length 
CHECK (LENGTH(employee_number) >= 1 AND LENGTH(employee_number) <= 20);

-- 姓名长度约束
ALTER TABLE employees ADD CONSTRAINT chk_name_length 
CHECK (LENGTH(name) >= 1 AND LENGTH(name) <= 100);

-- 日文注音长度约束
ALTER TABLE employees ADD CONSTRAINT chk_furigana_length 
CHECK (furigana IS NULL OR LENGTH(furigana) <= 200);

-- 生日约束（必须是过去的日期）
ALTER TABLE employees ADD CONSTRAINT chk_birthday_past 
CHECK (birthday IS NULL OR birthday < CURRENT_DATE);

-- 添加更新时间触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 创建更新时间触发器
CREATE TRIGGER update_employees_updated_at 
    BEFORE UPDATE ON employees 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

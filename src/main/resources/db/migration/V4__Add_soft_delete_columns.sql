-- 为员工表添加软删除相关字段
-- 添加软删除字段
ALTER TABLE employees ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE employees ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE employees ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);

-- 创建软删除相关索引
CREATE INDEX IF NOT EXISTS idx_employees_is_deleted ON employees(is_deleted);
CREATE INDEX IF NOT EXISTS idx_employees_deleted_at ON employees(deleted_at);

-- 添加软删除约束
ALTER TABLE employees ADD CONSTRAINT chk_deleted_by_length 
CHECK (deleted_by IS NULL OR LENGTH(deleted_by) <= 100);

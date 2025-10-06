-- Add soft delete related fields to employees table
-- Add soft delete fields
ALTER TABLE employees ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE employees ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE employees ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);

-- Create soft delete related indexes
CREATE INDEX IF NOT EXISTS idx_employees_is_deleted ON employees(is_deleted);
CREATE INDEX IF NOT EXISTS idx_employees_deleted_at ON employees(deleted_at);

-- Add soft delete constraints
ALTER TABLE employees ADD CONSTRAINT chk_deleted_by_length 
CHECK (deleted_by IS NULL OR LENGTH(deleted_by) <= 100);

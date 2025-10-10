-- Add soft delete related fields to employeeInfo table
-- Add soft delete fields
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE employeeInfo ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);

-- Create soft delete related indexes
CREATE INDEX IF NOT EXISTS idx_employeeInfo_is_deleted ON employeeInfo(is_deleted);
CREATE INDEX IF NOT EXISTS idx_employeeInfo_deleted_at ON employeeInfo(deleted_at);

-- Add soft delete constraints
ALTER TABLE employeeInfo ADD CONSTRAINT chk_deleted_by_length 
CHECK (deleted_by IS NULL OR LENGTH(deleted_by) <= 100);

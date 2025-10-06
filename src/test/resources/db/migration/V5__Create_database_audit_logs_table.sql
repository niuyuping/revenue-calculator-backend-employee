-- Create database audit logs table
CREATE TABLE IF NOT EXISTS database_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(20) NOT NULL,
    table_name VARCHAR(100) NOT NULL,
    record_id VARCHAR(100),
    user_id VARCHAR(100),
    session_id VARCHAR(100),
    request_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    old_values TEXT,
    new_values TEXT,
    sql_statement TEXT,
    execution_time_ms BIGINT,
    affected_rows INTEGER,
    error_message TEXT,
    operation_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
);

-- Create indexes to improve query performance
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_operation_type ON database_audit_logs(operation_type);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_table_name ON database_audit_logs(table_name);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_user_id ON database_audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_session_id ON database_audit_logs(session_id);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_request_id ON database_audit_logs(request_id);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_record_id ON database_audit_logs(record_id);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_operation_status ON database_audit_logs(operation_status);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_created_at ON database_audit_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_table_record ON database_audit_logs(table_name, record_id);
CREATE INDEX IF NOT EXISTS idx_database_audit_logs_user_created ON database_audit_logs(user_id, created_at);

-- Add table comments
COMMENT ON TABLE database_audit_logs IS 'Database audit logs table, records detailed information of all database operations';
COMMENT ON COLUMN database_audit_logs.id IS 'Primary key ID';
COMMENT ON COLUMN database_audit_logs.operation_type IS 'Operation type: INSERT, UPDATE, DELETE, SELECT';
COMMENT ON COLUMN database_audit_logs.table_name IS 'Table name';
COMMENT ON COLUMN database_audit_logs.record_id IS 'Record ID';
COMMENT ON COLUMN database_audit_logs.user_id IS 'User ID';
COMMENT ON COLUMN database_audit_logs.session_id IS 'Session ID';
COMMENT ON COLUMN database_audit_logs.request_id IS 'Request ID';
COMMENT ON COLUMN database_audit_logs.ip_address IS 'IP address';
COMMENT ON COLUMN database_audit_logs.user_agent IS 'User agent';
COMMENT ON COLUMN database_audit_logs.old_values IS 'Old values (JSON format)';
COMMENT ON COLUMN database_audit_logs.new_values IS 'New values (JSON format)';
COMMENT ON COLUMN database_audit_logs.sql_statement IS 'SQL statement';
COMMENT ON COLUMN database_audit_logs.execution_time_ms IS 'Execution time (milliseconds)';
COMMENT ON COLUMN database_audit_logs.affected_rows IS 'Affected rows';
COMMENT ON COLUMN database_audit_logs.error_message IS 'Error message';
COMMENT ON COLUMN database_audit_logs.operation_status IS 'Operation status: SUCCESS, FAILURE, ROLLBACK';
COMMENT ON COLUMN database_audit_logs.created_at IS 'Created time';
COMMENT ON COLUMN database_audit_logs.created_by IS 'Created by';

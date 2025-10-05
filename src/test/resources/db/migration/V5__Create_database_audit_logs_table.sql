-- 创建数据库审计日志表
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

-- 创建索引以提高查询性能
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

-- 添加表注释
COMMENT ON TABLE database_audit_logs IS '数据库审计日志表，记录所有数据库操作的详细信息';
COMMENT ON COLUMN database_audit_logs.id IS '主键ID';
COMMENT ON COLUMN database_audit_logs.operation_type IS '操作类型：INSERT, UPDATE, DELETE, SELECT';
COMMENT ON COLUMN database_audit_logs.table_name IS '表名';
COMMENT ON COLUMN database_audit_logs.record_id IS '记录ID';
COMMENT ON COLUMN database_audit_logs.user_id IS '用户ID';
COMMENT ON COLUMN database_audit_logs.session_id IS '会话ID';
COMMENT ON COLUMN database_audit_logs.request_id IS '请求ID';
COMMENT ON COLUMN database_audit_logs.ip_address IS 'IP地址';
COMMENT ON COLUMN database_audit_logs.user_agent IS '用户代理';
COMMENT ON COLUMN database_audit_logs.old_values IS '旧值（JSON格式）';
COMMENT ON COLUMN database_audit_logs.new_values IS '新值（JSON格式）';
COMMENT ON COLUMN database_audit_logs.sql_statement IS 'SQL语句';
COMMENT ON COLUMN database_audit_logs.execution_time_ms IS '执行时间（毫秒）';
COMMENT ON COLUMN database_audit_logs.affected_rows IS '影响行数';
COMMENT ON COLUMN database_audit_logs.error_message IS '错误消息';
COMMENT ON COLUMN database_audit_logs.operation_status IS '操作状态：SUCCESS, FAILURE, ROLLBACK';
COMMENT ON COLUMN database_audit_logs.created_at IS '创建时间';
COMMENT ON COLUMN database_audit_logs.created_by IS '创建者';

package jp.asatex.revenue_calculator_backend_employee.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 数据库审计日志实体
 * 记录所有数据库操作的详细信息
 */
@Table("database_audit_logs")
public class DatabaseAuditLog {

    @Id
    @Column("id")
    private Long id;

    @Column("operation_type")
    private String operationType; // INSERT, UPDATE, DELETE, SELECT

    @Column("table_name")
    private String tableName;

    @Column("record_id")
    private String recordId;

    @Column("user_id")
    private String userId;

    @Column("session_id")
    private String sessionId;

    @Column("request_id")
    private String requestId;

    @Column("ip_address")
    private String ipAddress;

    @Column("user_agent")
    private String userAgent;

    @Column("old_values")
    private String oldValues; // JSON格式的旧值

    @Column("new_values")
    private String newValues; // JSON格式的新值

    @Column("sql_statement")
    private String sqlStatement;

    @Column("execution_time_ms")
    private Long executionTimeMs;

    @Column("affected_rows")
    private Integer affectedRows;

    @Column("error_message")
    private String errorMessage;

    @Column("operation_status")
    private String operationStatus; // SUCCESS, FAILURE, ROLLBACK

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("created_by")
    private String createdBy;

    // 构造函数
    public DatabaseAuditLog() {}

    public DatabaseAuditLog(String operationType, String tableName, String recordId, 
                           String userId, String sessionId, String requestId, 
                           String ipAddress, String userAgent) {
        this.operationType = operationType;
        this.tableName = tableName;
        this.recordId = recordId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = LocalDateTime.now();
        this.createdBy = userId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public Integer getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(Integer affectedRows) {
        this.affectedRows = affectedRows;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "DatabaseAuditLog{" +
                "id=" + id +
                ", operationType='" + operationType + '\'' +
                ", tableName='" + tableName + '\'' +
                ", recordId='" + recordId + '\'' +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", oldValues='" + oldValues + '\'' +
                ", newValues='" + newValues + '\'' +
                ", sqlStatement='" + sqlStatement + '\'' +
                ", executionTimeMs=" + executionTimeMs +
                ", affectedRows=" + affectedRows +
                ", errorMessage='" + errorMessage + '\'' +
                ", operationStatus='" + operationStatus + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}

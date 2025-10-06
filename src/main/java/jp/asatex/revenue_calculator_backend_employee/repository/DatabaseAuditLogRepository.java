package jp.asatex.revenue_calculator_backend_employee.repository;

import jp.asatex.revenue_calculator_backend_employee.entity.DatabaseAuditLog;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Database audit log repository
 * Provides CRUD operations for database audit logs
 */
@Repository
public interface DatabaseAuditLogRepository extends ReactiveCrudRepository<DatabaseAuditLog, Long> {

    /**
     * Query audit logs by operation type
     * @param operationType Operation type
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_type = :operationType ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByOperationType(String operationType);

    /**
     * Query audit logs by table name
     * @param tableName Table name
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE table_name = :tableName ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByTableName(String tableName);

    /**
     * Query audit logs by user ID
     * @param userId User ID
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE user_id = :userId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByUserId(String userId);

    /**
     * Query audit logs by session ID
     * @param sessionId Session ID
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE session_id = :sessionId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findBySessionId(String sessionId);

    /**
     * Query audit logs by request ID
     * @param requestId Request ID
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE request_id = :requestId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByRequestId(String requestId);

    /**
     * Query audit logs by record ID
     * @param recordId Record ID
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE record_id = :recordId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByRecordId(String recordId);

    /**
     * Query audit logs by operation status
     * @param operationStatus Operation status
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_status = :operationStatus ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByOperationStatus(String operationStatus);

    /**
     * Query audit logs by time range
     * @param startTime Start time
     * @param endTime End time
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Query audit logs by table name and record ID
     * @param tableName Table name
     * @param recordId Record ID
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE table_name = :tableName AND record_id = :recordId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByTableNameAndRecordId(String tableName, String recordId);

    /**
     * Query audit logs by user ID and time range
     * @param userId User ID
     * @param startTime Start time
     * @param endTime End time
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE user_id = :userId AND created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Query audit logs by operation type and table name
     * @param operationType Operation type
     * @param tableName Table name
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_type = :operationType AND table_name = :tableName ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByOperationTypeAndTableName(String operationType, String tableName);

    /**
     * Count audit logs in specified time range
     * @param startTime Start time
     * @param endTime End time
     * @return Audit log count
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE created_at BETWEEN :startTime AND :endTime")
    Mono<Long> countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Count audit logs by operation type
     * @param operationType Operation type
     * @return Audit log count
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE operation_type = :operationType")
    Mono<Long> countByOperationType(String operationType);

    /**
     * Count audit logs by table name
     * @param tableName Table name
     * @return Audit log count
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE table_name = :tableName")
    Mono<Long> countByTableName(String tableName);

    /**
     * Count audit logs by user
     * @param userId User ID
     * @return Audit log count
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE user_id = :userId")
    Mono<Long> countByUserId(String userId);

    /**
     * Count audit logs by operation status
     * @param operationStatus Operation status
     * @return Audit log count
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE operation_status = :operationStatus")
    Mono<Long> countByOperationStatus(String operationStatus);

    /**
     * Query recent audit logs
     * @param limit Limit count
     * @return Audit log list
     */
    @Query("SELECT * FROM database_audit_logs ORDER BY created_at DESC LIMIT :limit")
    Flux<DatabaseAuditLog> findRecentLogs(int limit);

    /**
     * Query error audit logs in specified time range
     * @param startTime Start time
     * @param endTime End time
     * @return Error audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_status = 'FAILURE' AND created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findErrorLogsByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Query error audit logs by specified user
     * @param userId User ID
     * @return Error audit log list
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_status = 'FAILURE' AND user_id = :userId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findErrorLogsByUserId(String userId);

    /**
     * Delete audit logs before specified time
     * @param beforeTime Specified time
     * @return Number of deleted records
     */
    @Modifying
    @Query("DELETE FROM database_audit_logs WHERE created_at < :beforeTime")
    Mono<Integer> deleteByCreatedAtBefore(LocalDateTime beforeTime);
}

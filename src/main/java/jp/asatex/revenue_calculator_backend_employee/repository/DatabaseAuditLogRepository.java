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
 * 数据库审计日志Repository
 * 提供数据库审计日志的CRUD操作
 */
@Repository
public interface DatabaseAuditLogRepository extends ReactiveCrudRepository<DatabaseAuditLog, Long> {

    /**
     * 根据操作类型查询审计日志
     * @param operationType 操作类型
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_type = :operationType ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByOperationType(String operationType);

    /**
     * 根据表名查询审计日志
     * @param tableName 表名
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE table_name = :tableName ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByTableName(String tableName);

    /**
     * 根据用户ID查询审计日志
     * @param userId 用户ID
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE user_id = :userId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByUserId(String userId);

    /**
     * 根据会话ID查询审计日志
     * @param sessionId 会话ID
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE session_id = :sessionId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findBySessionId(String sessionId);

    /**
     * 根据请求ID查询审计日志
     * @param requestId 请求ID
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE request_id = :requestId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByRequestId(String requestId);

    /**
     * 根据记录ID查询审计日志
     * @param recordId 记录ID
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE record_id = :recordId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByRecordId(String recordId);

    /**
     * 根据操作状态查询审计日志
     * @param operationStatus 操作状态
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_status = :operationStatus ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByOperationStatus(String operationStatus);

    /**
     * 根据时间范围查询审计日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据表名和记录ID查询审计日志
     * @param tableName 表名
     * @param recordId 记录ID
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE table_name = :tableName AND record_id = :recordId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByTableNameAndRecordId(String tableName, String recordId);

    /**
     * 根据用户ID和时间范围查询审计日志
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE user_id = :userId AND created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据操作类型和表名查询审计日志
     * @param operationType 操作类型
     * @param tableName 表名
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_type = :operationType AND table_name = :tableName ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findByOperationTypeAndTableName(String operationType, String tableName);

    /**
     * 统计指定时间范围内的审计日志数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志数量
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE created_at BETWEEN :startTime AND :endTime")
    Mono<Long> countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定操作类型的审计日志数量
     * @param operationType 操作类型
     * @return 审计日志数量
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE operation_type = :operationType")
    Mono<Long> countByOperationType(String operationType);

    /**
     * 统计指定表名的审计日志数量
     * @param tableName 表名
     * @return 审计日志数量
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE table_name = :tableName")
    Mono<Long> countByTableName(String tableName);

    /**
     * 统计指定用户的审计日志数量
     * @param userId 用户ID
     * @return 审计日志数量
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE user_id = :userId")
    Mono<Long> countByUserId(String userId);

    /**
     * 统计指定操作状态的审计日志数量
     * @param operationStatus 操作状态
     * @return 审计日志数量
     */
    @Query("SELECT COUNT(*) FROM database_audit_logs WHERE operation_status = :operationStatus")
    Mono<Long> countByOperationStatus(String operationStatus);

    /**
     * 查询最近的审计日志
     * @param limit 限制数量
     * @return 审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs ORDER BY created_at DESC LIMIT :limit")
    Flux<DatabaseAuditLog> findRecentLogs(int limit);

    /**
     * 查询指定时间范围内的错误审计日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_status = 'FAILURE' AND created_at BETWEEN :startTime AND :endTime ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findErrorLogsByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询指定用户的错误审计日志
     * @param userId 用户ID
     * @return 错误审计日志列表
     */
    @Query("SELECT * FROM database_audit_logs WHERE operation_status = 'FAILURE' AND user_id = :userId ORDER BY created_at DESC")
    Flux<DatabaseAuditLog> findErrorLogsByUserId(String userId);

    /**
     * 删除指定时间之前的审计日志
     * @param beforeTime 指定时间
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM database_audit_logs WHERE created_at < :beforeTime")
    Mono<Integer> deleteByCreatedAtBefore(LocalDateTime beforeTime);
}

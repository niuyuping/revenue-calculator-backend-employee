package jp.asatex.revenue_calculator_backend_employee.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.asatex.revenue_calculator_backend_employee.entity.DatabaseAuditLog;
import jp.asatex.revenue_calculator_backend_employee.repository.DatabaseAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库审计服务
 * 提供数据库操作的审计日志记录功能
 */
@Service
public class DatabaseAuditService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuditService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Autowired
    private DatabaseAuditLogRepository databaseAuditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 记录数据库操作审计日志
     * @param operationType 操作类型 (INSERT, UPDATE, DELETE, SELECT)
     * @param tableName 表名
     * @param recordId 记录ID
     * @param oldValues 旧值
     * @param newValues 新值
     * @param sqlStatement SQL语句
     * @param executionTimeMs 执行时间（毫秒）
     * @param affectedRows 影响行数
     * @param operationStatus 操作状态 (SUCCESS, FAILURE, ROLLBACK)
     * @param errorMessage 错误消息
     * @return Mono<Void>
     */
    public Mono<Void> logDatabaseOperation(String operationType, String tableName, String recordId,
                                          Map<String, Object> oldValues, Map<String, Object> newValues,
                                          String sqlStatement, Long executionTimeMs, Integer affectedRows,
                                          String operationStatus, String errorMessage) {
        try {
            // 从MDC获取上下文信息
            String userId = MDC.get("userId") != null ? MDC.get("userId") : "system";
            String sessionId = MDC.get("sessionId");
            String requestId = MDC.get("requestId");
            String ipAddress = MDC.get("ipAddress");
            String userAgent = MDC.get("userAgent");

            // 创建审计日志实体
            DatabaseAuditLog auditLog = new DatabaseAuditLog(
                    operationType, tableName, recordId, userId, sessionId, requestId, ipAddress, userAgent
            );

            // 设置其他属性
            auditLog.setOldValues(convertToJson(oldValues));
            auditLog.setNewValues(convertToJson(newValues));
            auditLog.setSqlStatement(sqlStatement);
            auditLog.setExecutionTimeMs(executionTimeMs);
            auditLog.setAffectedRows(affectedRows);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setOperationStatus(operationStatus);

            // 保存审计日志
            return databaseAuditLogRepository.save(auditLog)
                    .doOnSuccess(savedLog -> {
                        logger.debug("数据库审计日志已保存: {}", savedLog.getId());
                        auditLogger.info("数据库操作审计: 操作类型={}, 表名={}, 记录ID={}, 状态={}, 耗时={}ms",
                                operationType, tableName, recordId, operationStatus, executionTimeMs);
                    })
                    .doOnError(error -> {
                        logger.error("保存数据库审计日志失败", error);
                        auditLogger.error("数据库审计日志保存失败: 操作类型={}, 表名={}, 记录ID={}, 错误={}",
                                operationType, tableName, recordId, error.getMessage());
                    })
                    .then();
        } catch (Exception e) {
            logger.error("记录数据库审计日志时发生异常", e);
            return Mono.empty();
        }
    }

    /**
     * 记录成功的数据库操作
     * @param operationType 操作类型
     * @param tableName 表名
     * @param recordId 记录ID
     * @param oldValues 旧值
     * @param newValues 新值
     * @param sqlStatement SQL语句
     * @param executionTimeMs 执行时间
     * @param affectedRows 影响行数
     * @return Mono<Void>
     */
    public Mono<Void> logSuccessfulOperation(String operationType, String tableName, String recordId,
                                           Map<String, Object> oldValues, Map<String, Object> newValues,
                                           String sqlStatement, Long executionTimeMs, Integer affectedRows) {
        return logDatabaseOperation(operationType, tableName, recordId, oldValues, newValues,
                sqlStatement, executionTimeMs, affectedRows, "SUCCESS", null);
    }

    /**
     * 记录失败的数据库操作
     * @param operationType 操作类型
     * @param tableName 表名
     * @param recordId 记录ID
     * @param sqlStatement SQL语句
     * @param executionTimeMs 执行时间
     * @param errorMessage 错误消息
     * @return Mono<Void>
     */
    public Mono<Void> logFailedOperation(String operationType, String tableName, String recordId,
                                       String sqlStatement, Long executionTimeMs, String errorMessage) {
        return logDatabaseOperation(operationType, tableName, recordId, null, null,
                sqlStatement, executionTimeMs, 0, "FAILURE", errorMessage);
    }

    /**
     * 记录回滚的数据库操作
     * @param operationType 操作类型
     * @param tableName 表名
     * @param recordId 记录ID
     * @param sqlStatement SQL语句
     * @param executionTimeMs 执行时间
     * @param errorMessage 错误消息
     * @return Mono<Void>
     */
    public Mono<Void> logRollbackOperation(String operationType, String tableName, String recordId,
                                         String sqlStatement, Long executionTimeMs, String errorMessage) {
        return logDatabaseOperation(operationType, tableName, recordId, null, null,
                sqlStatement, executionTimeMs, 0, "ROLLBACK", errorMessage);
    }

    /**
     * 记录INSERT操作
     * @param tableName 表名
     * @param recordId 记录ID
     * @param newValues 新值
     * @param sqlStatement SQL语句
     * @param executionTimeMs 执行时间
     * @param affectedRows 影响行数
     * @return Mono<Void>
     */
    public Mono<Void> logInsertOperation(String tableName, String recordId, Map<String, Object> newValues,
                                       String sqlStatement, Long executionTimeMs, Integer affectedRows) {
        return logSuccessfulOperation("INSERT", tableName, recordId, null, newValues,
                sqlStatement, executionTimeMs, affectedRows);
    }

    /**
     * 记录UPDATE操作
     * @param tableName 表名
     * @param recordId 记录ID
     * @param oldValues 旧值
     * @param newValues 新值
     * @param sqlStatement SQL语句
     * @param executionTimeMs 执行时间
     * @param affectedRows 影响行数
     * @return Mono<Void>
     */
    public Mono<Void> logUpdateOperation(String tableName, String recordId, Map<String, Object> oldValues,
                                       Map<String, Object> newValues, String sqlStatement, Long executionTimeMs,
                                       Integer affectedRows) {
        return logSuccessfulOperation("UPDATE", tableName, recordId, oldValues, newValues,
                sqlStatement, executionTimeMs, affectedRows);
    }

    /**
     * 记录DELETE操作
     * @param tableName 表名
     * @param recordId 记录ID
     * @param oldValues 旧值
     * @param sqlStatement SQL语句
     * @param executionTimeMs 执行时间
     * @param affectedRows 影响行数
     * @return Mono<Void>
     */
    public Mono<Void> logDeleteOperation(String tableName, String recordId, Map<String, Object> oldValues,
                                       String sqlStatement, Long executionTimeMs, Integer affectedRows) {
        return logSuccessfulOperation("DELETE", tableName, recordId, oldValues, null,
                sqlStatement, executionTimeMs, affectedRows);
    }

    /**
     * 记录SELECT操作
     * @param tableName 表名
     * @param recordId 记录ID
     * @param sqlStatement SQL语句
     * @param executionTimeMs 执行时间
     * @param affectedRows 影响行数
     * @return Mono<Void>
     */
    public Mono<Void> logSelectOperation(String tableName, String recordId, String sqlStatement,
                                       Long executionTimeMs, Integer affectedRows) {
        return logSuccessfulOperation("SELECT", tableName, recordId, null, null,
                sqlStatement, executionTimeMs, affectedRows);
    }

    /**
     * 将Map转换为JSON字符串
     * @param data 数据Map
     * @return JSON字符串
     */
    private String convertToJson(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.warn("JSON序列化失败: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * 获取审计日志统计信息
     * @return 统计信息Map
     */
    public Mono<Map<String, Object>> getAuditStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        return Mono.zip(
                databaseAuditLogRepository.countByCreatedAtBetween(oneDayAgo, now),
                databaseAuditLogRepository.countByCreatedAtBetween(oneWeekAgo, now),
                databaseAuditLogRepository.countByCreatedAtBetween(oneMonthAgo, now),
                databaseAuditLogRepository.countByOperationType("INSERT"),
                databaseAuditLogRepository.countByOperationType("UPDATE"),
                databaseAuditLogRepository.countByOperationType("DELETE"),
                databaseAuditLogRepository.countByOperationType("SELECT"),
                databaseAuditLogRepository.countByOperationStatus("SUCCESS")
        ).flatMap(tuple -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("last24Hours", tuple.getT1());
            stats.put("last7Days", tuple.getT2());
            stats.put("last30Days", tuple.getT3());
            stats.put("totalInserts", tuple.getT4());
            stats.put("totalUpdates", tuple.getT5());
            stats.put("totalDeletes", tuple.getT6());
            stats.put("totalSelects", tuple.getT7());
            stats.put("totalSuccess", tuple.getT8());
            stats.put("timestamp", now.toString());
            
            return Mono.zip(
                    databaseAuditLogRepository.countByOperationStatus("FAILURE"),
                    databaseAuditLogRepository.countByOperationStatus("ROLLBACK")
            ).map(tuple2 -> {
                stats.put("totalFailures", tuple2.getT1());
                stats.put("totalRollbacks", tuple2.getT2());
                return stats;
            });
        });
    }

    /**
     * 清理过期的审计日志
     * @param retentionDays 保留天数
     * @return 删除的记录数
     */
    public Mono<Long> cleanupOldAuditLogs(int retentionDays) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
        return databaseAuditLogRepository.deleteByCreatedAtBefore(cutoffTime)
                .map(deletedCount -> deletedCount != null ? deletedCount.longValue() : 0L)
                .doOnSuccess(deletedCount -> {
                    logger.info("清理了 {} 条过期的审计日志", deletedCount);
                    auditLogger.info("审计日志清理: 删除了 {} 条 {} 天前的记录", deletedCount, retentionDays);
                })
                .doOnError(error -> {
                    logger.error("清理过期审计日志失败", error);
                    auditLogger.error("审计日志清理失败: {}", error.getMessage());
                });
    }

    // 以下方法用于控制器查询
    public Flux<DatabaseAuditLog> findByOperationType(String operationType) {
        return databaseAuditLogRepository.findByOperationType(operationType);
    }

    public Flux<DatabaseAuditLog> findByTableName(String tableName) {
        return databaseAuditLogRepository.findByTableName(tableName);
    }

    public Flux<DatabaseAuditLog> findByUserId(String userId) {
        return databaseAuditLogRepository.findByUserId(userId);
    }

    public Flux<DatabaseAuditLog> findBySessionId(String sessionId) {
        return databaseAuditLogRepository.findBySessionId(sessionId);
    }

    public Flux<DatabaseAuditLog> findByRequestId(String requestId) {
        return databaseAuditLogRepository.findByRequestId(requestId);
    }

    public Flux<DatabaseAuditLog> findByRecordId(String recordId) {
        return databaseAuditLogRepository.findByRecordId(recordId);
    }

    public Flux<DatabaseAuditLog> findByOperationStatus(String operationStatus) {
        return databaseAuditLogRepository.findByOperationStatus(operationStatus);
    }

    public Flux<DatabaseAuditLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return databaseAuditLogRepository.findByCreatedAtBetween(startTime, endTime);
    }

    public Flux<DatabaseAuditLog> findByTableNameAndRecordId(String tableName, String recordId) {
        return databaseAuditLogRepository.findByTableNameAndRecordId(tableName, recordId);
    }

    public Flux<DatabaseAuditLog> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        return databaseAuditLogRepository.findByUserIdAndCreatedAtBetween(userId, startTime, endTime);
    }

    public Flux<DatabaseAuditLog> findByOperationTypeAndTableName(String operationType, String tableName) {
        return databaseAuditLogRepository.findByOperationTypeAndTableName(operationType, tableName);
    }

    public Flux<DatabaseAuditLog> findRecentLogs(int limit) {
        return databaseAuditLogRepository.findRecentLogs(limit);
    }

    public Flux<DatabaseAuditLog> findErrorLogsByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return databaseAuditLogRepository.findErrorLogsByCreatedAtBetween(startTime, endTime);
    }

    public Flux<DatabaseAuditLog> findErrorLogsByUserId(String userId) {
        return databaseAuditLogRepository.findErrorLogsByUserId(userId);
    }
}

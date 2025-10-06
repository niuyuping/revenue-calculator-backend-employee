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
 * Database audit service
 * Provides database operation audit logging functionality
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
     * Log database operation audit
     * @param operationType Operation type (INSERT, UPDATE, DELETE, SELECT)
     * @param tableName Table name
     * @param recordId Record ID
     * @param oldValues Old values
     * @param newValues New values
     * @param sqlStatement SQL statement
     * @param executionTimeMs Execution time (milliseconds)
     * @param affectedRows Affected rows
     * @param operationStatus Operation status (SUCCESS, FAILURE, ROLLBACK)
     * @param errorMessage Error message
     * @return Mono<Void>
     */
    public Mono<Void> logDatabaseOperation(String operationType, String tableName, String recordId,
                                          Map<String, Object> oldValues, Map<String, Object> newValues,
                                          String sqlStatement, Long executionTimeMs, Integer affectedRows,
                                          String operationStatus, String errorMessage) {
        try {
            // Get context information from MDC
            String userId = MDC.get("userId") != null ? MDC.get("userId") : "system";
            String sessionId = MDC.get("sessionId");
            String requestId = MDC.get("requestId");
            String ipAddress = MDC.get("ipAddress");
            String userAgent = MDC.get("userAgent");

            // Create audit log entity
            DatabaseAuditLog auditLog = new DatabaseAuditLog(
                    operationType, tableName, recordId, userId, sessionId, requestId, ipAddress, userAgent
            );

            // Set other properties
            auditLog.setOldValues(convertToJson(oldValues));
            auditLog.setNewValues(convertToJson(newValues));
            auditLog.setSqlStatement(sqlStatement);
            auditLog.setExecutionTimeMs(executionTimeMs);
            auditLog.setAffectedRows(affectedRows);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setOperationStatus(operationStatus);

            // Save audit log
            return databaseAuditLogRepository.save(auditLog)
                    .doOnSuccess(savedLog -> {
                        logger.debug("Database audit log saved: {}", savedLog.getId());
                        auditLogger.info("Database operation audit: operationType={}, tableName={}, recordId={}, status={}, executionTime={}ms",
                                operationType, tableName, recordId, operationStatus, executionTimeMs);
                    })
                    .doOnError(error -> {
                        logger.error("Failed to save database audit log", error);
                        auditLogger.error("Database audit log save failed: operationType={}, tableName={}, recordId={}, error={}",
                                operationType, tableName, recordId, error.getMessage());
                    })
                    .then();
        } catch (Exception e) {
            logger.error("Exception occurred while logging database audit", e);
            return Mono.empty();
        }
    }

    /**
     * Log successful database operation
     * @param operationType Operation type
     * @param tableName Table name
     * @param recordId Record ID
     * @param oldValues Old values
     * @param newValues New values
     * @param sqlStatement SQL statement
     * @param executionTimeMs Execution time
     * @param affectedRows Affected rows
     * @return Mono<Void>
     */
    public Mono<Void> logSuccessfulOperation(String operationType, String tableName, String recordId,
                                           Map<String, Object> oldValues, Map<String, Object> newValues,
                                           String sqlStatement, Long executionTimeMs, Integer affectedRows) {
        return logDatabaseOperation(operationType, tableName, recordId, oldValues, newValues,
                sqlStatement, executionTimeMs, affectedRows, "SUCCESS", null);
    }

    /**
     * Log failed database operation
     * @param operationType Operation type
     * @param tableName Table name
     * @param recordId Record ID
     * @param sqlStatement SQL statement
     * @param executionTimeMs Execution time
     * @param errorMessage Error message
     * @return Mono<Void>
     */
    public Mono<Void> logFailedOperation(String operationType, String tableName, String recordId,
                                       String sqlStatement, Long executionTimeMs, String errorMessage) {
        return logDatabaseOperation(operationType, tableName, recordId, null, null,
                sqlStatement, executionTimeMs, 0, "FAILURE", errorMessage);
    }

    /**
     * Log rolled back database operation
     * @param operationType Operation type
     * @param tableName Table name
     * @param recordId Record ID
     * @param sqlStatement SQL statement
     * @param executionTimeMs Execution time
     * @param errorMessage Error message
     * @return Mono<Void>
     */
    public Mono<Void> logRollbackOperation(String operationType, String tableName, String recordId,
                                         String sqlStatement, Long executionTimeMs, String errorMessage) {
        return logDatabaseOperation(operationType, tableName, recordId, null, null,
                sqlStatement, executionTimeMs, 0, "ROLLBACK", errorMessage);
    }

    /**
     * Log INSERT operation
     * @param tableName Table name
     * @param recordId Record ID
     * @param newValues New values
     * @param sqlStatement SQL statement
     * @param executionTimeMs Execution time
     * @param affectedRows Affected rows
     * @return Mono<Void>
     */
    public Mono<Void> logInsertOperation(String tableName, String recordId, Map<String, Object> newValues,
                                       String sqlStatement, Long executionTimeMs, Integer affectedRows) {
        return logSuccessfulOperation("INSERT", tableName, recordId, null, newValues,
                sqlStatement, executionTimeMs, affectedRows);
    }

    /**
     * Log UPDATE operation
     * @param tableName Table name
     * @param recordId Record ID
     * @param oldValues Old values
     * @param newValues New values
     * @param sqlStatement SQL statement
     * @param executionTimeMs Execution time
     * @param affectedRows Affected rows
     * @return Mono<Void>
     */
    public Mono<Void> logUpdateOperation(String tableName, String recordId, Map<String, Object> oldValues,
                                       Map<String, Object> newValues, String sqlStatement, Long executionTimeMs,
                                       Integer affectedRows) {
        return logSuccessfulOperation("UPDATE", tableName, recordId, oldValues, newValues,
                sqlStatement, executionTimeMs, affectedRows);
    }

    /**
     * Log DELETE operation
     * @param tableName Table name
     * @param recordId Record ID
     * @param oldValues Old values
     * @param sqlStatement SQL statement
     * @param executionTimeMs Execution time
     * @param affectedRows Affected rows
     * @return Mono<Void>
     */
    public Mono<Void> logDeleteOperation(String tableName, String recordId, Map<String, Object> oldValues,
                                       String sqlStatement, Long executionTimeMs, Integer affectedRows) {
        return logSuccessfulOperation("DELETE", tableName, recordId, oldValues, null,
                sqlStatement, executionTimeMs, affectedRows);
    }

    /**
     * Log SELECT operation
     * @param tableName Table name
     * @param recordId Record ID
     * @param sqlStatement SQL statement
     * @param executionTimeMs Execution time
     * @param affectedRows Affected rows
     * @return Mono<Void>
     */
    public Mono<Void> logSelectOperation(String tableName, String recordId, String sqlStatement,
                                       Long executionTimeMs, Integer affectedRows) {
        return logSuccessfulOperation("SELECT", tableName, recordId, null, null,
                sqlStatement, executionTimeMs, affectedRows);
    }

    /**
     * Convert Map to JSON string
     * @param data Data Map
     * @return JSON string
     */
    private String convertToJson(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.warn("JSON serialization failed: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * Get audit log statistics
     * @return Statistics Map
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
     * Clean up expired audit logs
     * @param retentionDays Retention days
     * @return Number of deleted records
     */
    public Mono<Long> cleanupOldAuditLogs(int retentionDays) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
        return databaseAuditLogRepository.deleteByCreatedAtBefore(cutoffTime)
                .map(deletedCount -> deletedCount != null ? deletedCount.longValue() : 0L)
                .doOnSuccess(deletedCount -> {
                    logger.info("Cleaned up {} expired audit logs", deletedCount);
                    auditLogger.info("Audit log cleanup: deleted {} records from {} days ago", deletedCount, retentionDays);
                })
                .doOnError(error -> {
                    logger.error("Failed to clean up expired audit logs", error);
                    auditLogger.error("Audit log cleanup failed: {}", error.getMessage());
                });
    }

    // The following methods are used for controller queries
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

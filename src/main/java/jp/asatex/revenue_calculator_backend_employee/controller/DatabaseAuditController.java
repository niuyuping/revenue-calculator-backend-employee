package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.entity.DatabaseAuditLog;
import jp.asatex.revenue_calculator_backend_employee.service.DatabaseAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Database audit controller
 * Provides API endpoints for database audit log query and management
 */
@RestController
@RequestMapping("/api/v1/audit/database")
@Tag(name = "Database Audit", description = "Database audit log query and management API")
public class DatabaseAuditController {

    @Autowired
    private DatabaseAuditService databaseAuditService;

    /**
     * Get audit log statistics
     * @return Statistics information
     */
    @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get database audit statistics", description = "Returns database audit log statistics including operation types, success/failure rates, etc.")
    public Mono<Map<String, Object>> getAuditStatistics() {
        return databaseAuditService.getAuditStatistics();
    }

    /**
     * Query audit logs by operation type
     * @param operationType Operation type
     * @return Audit log list
     */
    @GetMapping(value = "/logs/operation/{operationType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by operation type", description = "Query audit logs by operation type (INSERT, UPDATE, DELETE, SELECT)")
    public Flux<DatabaseAuditLog> getLogsByOperationType(
            @Parameter(description = "Operation type", required = true, example = "INSERT")
            @PathVariable String operationType) {
        return databaseAuditService.findByOperationType(operationType);
    }

    /**
     * Query audit logs by table name
     * @param tableName Table name
     * @return Audit log list
     */
    @GetMapping(value = "/logs/table/{tableName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by table name", description = "Query audit logs by table name")
    public Flux<DatabaseAuditLog> getLogsByTableName(
            @Parameter(description = "Table name", required = true, example = "employees")
            @PathVariable String tableName) {
        return databaseAuditService.findByTableName(tableName);
    }

    /**
     * Query audit logs by user ID
     * @param userId User ID
     * @return Audit log list
     */
    @GetMapping(value = "/logs/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by user ID", description = "Query audit logs by user ID")
    public Flux<DatabaseAuditLog> getLogsByUserId(
            @Parameter(description = "User ID", required = true, example = "user123")
            @PathVariable String userId) {
        return databaseAuditService.findByUserId(userId);
    }

    /**
     * Query audit logs by session ID
     * @param sessionId Session ID
     * @return Audit log list
     */
    @GetMapping(value = "/logs/session/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by session ID", description = "Query audit logs by session ID")
    public Flux<DatabaseAuditLog> getLogsBySessionId(
            @Parameter(description = "Session ID", required = true, example = "session-123")
            @PathVariable String sessionId) {
        return databaseAuditService.findBySessionId(sessionId);
    }

    /**
     * Query audit logs by request ID
     * @param requestId Request ID
     * @return Audit log list
     */
    @GetMapping(value = "/logs/request/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by request ID", description = "Query audit logs by request ID")
    public Flux<DatabaseAuditLog> getLogsByRequestId(
            @Parameter(description = "Request ID", required = true, example = "request-123")
            @PathVariable String requestId) {
        return databaseAuditService.findByRequestId(requestId);
    }

    /**
     * Query audit logs by record ID
     * @param recordId Record ID
     * @return Audit log list
     */
    @GetMapping(value = "/logs/record/{recordId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by record ID", description = "Query audit logs by record ID")
    public Flux<DatabaseAuditLog> getLogsByRecordId(
            @Parameter(description = "Record ID", required = true, example = "123")
            @PathVariable String recordId) {
        return databaseAuditService.findByRecordId(recordId);
    }

    /**
     * Query audit logs by operation status
     * @param operationStatus Operation status
     * @return Audit log list
     */
    @GetMapping(value = "/logs/status/{operationStatus}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by operation status", description = "Query audit logs by operation status (SUCCESS, FAILURE, ROLLBACK)")
    public Flux<DatabaseAuditLog> getLogsByOperationStatus(
            @Parameter(description = "Operation status", required = true, example = "SUCCESS")
            @PathVariable String operationStatus) {
        return databaseAuditService.findByOperationStatus(operationStatus);
    }

    /**
     * Query audit logs by time range
     * @param startTime Start time
     * @param endTime End time
     * @return Audit log list
     */
    @GetMapping(value = "/logs/time-range", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by time range", description = "Query audit logs within specified time range")
    public Flux<DatabaseAuditLog> getLogsByTimeRange(
            @Parameter(description = "Start time", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time", required = true, example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return databaseAuditService.findByCreatedAtBetween(startTime, endTime);
    }

    /**
     * Query audit logs by table name and record ID
     * @param tableName Table name
     * @param recordId Record ID
     * @return Audit log list
     */
    @GetMapping(value = "/logs/table/{tableName}/record/{recordId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by table name and record ID", description = "Query audit logs for specific record by table name and record ID")
    public Flux<DatabaseAuditLog> getLogsByTableAndRecord(
            @Parameter(description = "Table name", required = true, example = "employees")
            @PathVariable String tableName,
            @Parameter(description = "Record ID", required = true, example = "123")
            @PathVariable String recordId) {
        return databaseAuditService.findByTableNameAndRecordId(tableName, recordId);
    }

    /**
     * Query audit logs by user ID and time range
     * @param userId User ID
     * @param startTime Start time
     * @param endTime End time
     * @return Audit log list
     */
    @GetMapping(value = "/logs/user/{userId}/time-range", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by user ID and time range", description = "Query audit logs by user ID and time range")
    public Flux<DatabaseAuditLog> getLogsByUserAndTimeRange(
            @Parameter(description = "User ID", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "Start time", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time", required = true, example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return databaseAuditService.findByUserIdAndCreatedAtBetween(userId, startTime, endTime);
    }

    /**
     * Query audit logs by operation type and table name
     * @param operationType Operation type
     * @param tableName Table name
     * @return Audit log list
     */
    @GetMapping(value = "/logs/operation/{operationType}/table/{tableName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query audit logs by operation type and table name", description = "Query audit logs by operation type and table name")
    public Flux<DatabaseAuditLog> getLogsByOperationAndTable(
            @Parameter(description = "Operation type", required = true, example = "INSERT")
            @PathVariable String operationType,
            @Parameter(description = "Table name", required = true, example = "employees")
            @PathVariable String tableName) {
        return databaseAuditService.findByOperationTypeAndTableName(operationType, tableName);
    }

    /**
     * Query recent audit logs
     * @param limit Limit count
     * @return Audit log list
     */
    @GetMapping(value = "/logs/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query recent audit logs", description = "Query recent audit logs with optional limit")
    public Flux<DatabaseAuditLog> getRecentLogs(
            @Parameter(description = "Limit count", example = "100")
            @RequestParam(defaultValue = "100") int limit) {
        return databaseAuditService.findRecentLogs(limit);
    }

    /**
     * Query error audit logs
     * @param startTime Start time
     * @param endTime End time
     * @return Error audit log list
     */
    @GetMapping(value = "/logs/errors", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query error audit logs", description = "Query error audit logs within specified time range")
    public Flux<DatabaseAuditLog> getErrorLogs(
            @Parameter(description = "Start time", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time", required = true, example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return databaseAuditService.findErrorLogsByCreatedAtBetween(startTime, endTime);
    }

    /**
     * Query error audit logs for specific user
     * @param userId User ID
     * @return Error audit log list
     */
    @GetMapping(value = "/logs/errors/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Query error audit logs for specific user", description = "Query error audit logs for specific user")
    public Flux<DatabaseAuditLog> getErrorLogsByUserId(
            @Parameter(description = "User ID", required = true, example = "user123")
            @PathVariable String userId) {
        return databaseAuditService.findErrorLogsByUserId(userId);
    }

    /**
     * Clean up expired audit logs
     * @param retentionDays Retention days
     * @return Number of deleted records
     */
    @DeleteMapping(value = "/logs/cleanup", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Clean up expired audit logs", description = "Clean up audit logs older than specified days")
    public Mono<ResponseEntity<Map<String, Object>>> cleanupOldLogs(
            @Parameter(description = "Retention days", example = "90")
            @RequestParam(defaultValue = "90") int retentionDays) {
        return databaseAuditService.cleanupOldAuditLogs(retentionDays)
                .map(deletedCount -> {
                    Map<String, Object> response = Map.of(
                            "message", "Audit log cleanup completed",
                            "deletedCount", deletedCount,
                            "retentionDays", retentionDays,
                            "timestamp", LocalDateTime.now().toString()
                    );
                    return ResponseEntity.ok(response);
                });
    }
}

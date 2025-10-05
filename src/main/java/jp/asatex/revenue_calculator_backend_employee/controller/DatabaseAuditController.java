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
 * 数据库审计控制器
 * 提供数据库审计日志的查询和管理API端点
 */
@RestController
@RequestMapping("/api/v1/audit/database")
@Tag(name = "数据库审计", description = "数据库审计日志查询和管理API")
public class DatabaseAuditController {

    @Autowired
    private DatabaseAuditService databaseAuditService;

    /**
     * 获取审计日志统计信息
     * @return 统计信息
     */
    @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "获取数据库审计统计信息", description = "返回数据库审计日志的统计信息，包括操作类型、成功失败率等")
    public Mono<Map<String, Object>> getAuditStatistics() {
        return databaseAuditService.getAuditStatistics();
    }

    /**
     * 根据操作类型查询审计日志
     * @param operationType 操作类型
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/operation/{operationType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据操作类型查询审计日志", description = "根据操作类型（INSERT, UPDATE, DELETE, SELECT）查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByOperationType(
            @Parameter(description = "操作类型", required = true, example = "INSERT")
            @PathVariable String operationType) {
        return databaseAuditService.findByOperationType(operationType);
    }

    /**
     * 根据表名查询审计日志
     * @param tableName 表名
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/table/{tableName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据表名查询审计日志", description = "根据表名查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByTableName(
            @Parameter(description = "表名", required = true, example = "employees")
            @PathVariable String tableName) {
        return databaseAuditService.findByTableName(tableName);
    }

    /**
     * 根据用户ID查询审计日志
     * @param userId 用户ID
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据用户ID查询审计日志", description = "根据用户ID查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByUserId(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId) {
        return databaseAuditService.findByUserId(userId);
    }

    /**
     * 根据会话ID查询审计日志
     * @param sessionId 会话ID
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/session/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据会话ID查询审计日志", description = "根据会话ID查询审计日志")
    public Flux<DatabaseAuditLog> getLogsBySessionId(
            @Parameter(description = "会话ID", required = true, example = "session-123")
            @PathVariable String sessionId) {
        return databaseAuditService.findBySessionId(sessionId);
    }

    /**
     * 根据请求ID查询审计日志
     * @param requestId 请求ID
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/request/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据请求ID查询审计日志", description = "根据请求ID查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByRequestId(
            @Parameter(description = "请求ID", required = true, example = "request-123")
            @PathVariable String requestId) {
        return databaseAuditService.findByRequestId(requestId);
    }

    /**
     * 根据记录ID查询审计日志
     * @param recordId 记录ID
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/record/{recordId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据记录ID查询审计日志", description = "根据记录ID查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByRecordId(
            @Parameter(description = "记录ID", required = true, example = "123")
            @PathVariable String recordId) {
        return databaseAuditService.findByRecordId(recordId);
    }

    /**
     * 根据操作状态查询审计日志
     * @param operationStatus 操作状态
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/status/{operationStatus}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据操作状态查询审计日志", description = "根据操作状态（SUCCESS, FAILURE, ROLLBACK）查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByOperationStatus(
            @Parameter(description = "操作状态", required = true, example = "SUCCESS")
            @PathVariable String operationStatus) {
        return databaseAuditService.findByOperationStatus(operationStatus);
    }

    /**
     * 根据时间范围查询审计日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/time-range", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据时间范围查询审计日志", description = "根据指定的时间范围查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByTimeRange(
            @Parameter(description = "开始时间", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return databaseAuditService.findByCreatedAtBetween(startTime, endTime);
    }

    /**
     * 根据表名和记录ID查询审计日志
     * @param tableName 表名
     * @param recordId 记录ID
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/table/{tableName}/record/{recordId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据表名和记录ID查询审计日志", description = "根据表名和记录ID查询特定记录的审计日志")
    public Flux<DatabaseAuditLog> getLogsByTableAndRecord(
            @Parameter(description = "表名", required = true, example = "employees")
            @PathVariable String tableName,
            @Parameter(description = "记录ID", required = true, example = "123")
            @PathVariable String recordId) {
        return databaseAuditService.findByTableNameAndRecordId(tableName, recordId);
    }

    /**
     * 根据用户ID和时间范围查询审计日志
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/user/{userId}/time-range", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据用户ID和时间范围查询审计日志", description = "根据用户ID和时间范围查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByUserAndTimeRange(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "开始时间", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return databaseAuditService.findByUserIdAndCreatedAtBetween(userId, startTime, endTime);
    }

    /**
     * 根据操作类型和表名查询审计日志
     * @param operationType 操作类型
     * @param tableName 表名
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/operation/{operationType}/table/{tableName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "根据操作类型和表名查询审计日志", description = "根据操作类型和表名查询审计日志")
    public Flux<DatabaseAuditLog> getLogsByOperationAndTable(
            @Parameter(description = "操作类型", required = true, example = "INSERT")
            @PathVariable String operationType,
            @Parameter(description = "表名", required = true, example = "employees")
            @PathVariable String tableName) {
        return databaseAuditService.findByOperationTypeAndTableName(operationType, tableName);
    }

    /**
     * 查询最近的审计日志
     * @param limit 限制数量
     * @return 审计日志列表
     */
    @GetMapping(value = "/logs/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "查询最近的审计日志", description = "查询最近的审计日志，可指定数量限制")
    public Flux<DatabaseAuditLog> getRecentLogs(
            @Parameter(description = "限制数量", example = "100")
            @RequestParam(defaultValue = "100") int limit) {
        return databaseAuditService.findRecentLogs(limit);
    }

    /**
     * 查询错误审计日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误审计日志列表
     */
    @GetMapping(value = "/logs/errors", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "查询错误审计日志", description = "查询指定时间范围内的错误审计日志")
    public Flux<DatabaseAuditLog> getErrorLogs(
            @Parameter(description = "开始时间", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2024-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return databaseAuditService.findErrorLogsByCreatedAtBetween(startTime, endTime);
    }

    /**
     * 查询指定用户的错误审计日志
     * @param userId 用户ID
     * @return 错误审计日志列表
     */
    @GetMapping(value = "/logs/errors/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "查询指定用户的错误审计日志", description = "查询指定用户的错误审计日志")
    public Flux<DatabaseAuditLog> getErrorLogsByUserId(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId) {
        return databaseAuditService.findErrorLogsByUserId(userId);
    }

    /**
     * 清理过期的审计日志
     * @param retentionDays 保留天数
     * @return 删除的记录数
     */
    @DeleteMapping(value = "/logs/cleanup", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "清理过期的审计日志", description = "清理指定天数之前的审计日志")
    public Mono<ResponseEntity<Map<String, Object>>> cleanupOldLogs(
            @Parameter(description = "保留天数", example = "90")
            @RequestParam(defaultValue = "90") int retentionDays) {
        return databaseAuditService.cleanupOldAuditLogs(retentionDays)
                .map(deletedCount -> {
                    Map<String, Object> response = Map.of(
                            "message", "审计日志清理完成",
                            "deletedCount", deletedCount,
                            "retentionDays", retentionDays,
                            "timestamp", LocalDateTime.now().toString()
                    );
                    return ResponseEntity.ok(response);
                });
    }
}

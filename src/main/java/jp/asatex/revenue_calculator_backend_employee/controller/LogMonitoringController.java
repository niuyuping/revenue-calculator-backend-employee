package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.service.LogMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 日志监控控制器
 * 提供日志统计信息和健康状态的API端点
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "日志监控", description = "日志监控和统计信息API")
public class LogMonitoringController {

    @Autowired
    private LogMonitoringService logMonitoringService;

    /**
     * 获取日志统计信息
     * @return 日志统计信息
     */
    @GetMapping(value = "/logs/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "获取日志统计信息", description = "返回当前系统的日志统计信息，包括各种类型日志的数量和错误率")
    public Mono<LogMonitoringService.LogStats> getLogStats() {
        return Mono.just(logMonitoringService.getLogStats());
    }

    /**
     * 获取日志健康状态
     * @return 日志健康状态
     */
    @GetMapping(value = "/logs/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "获取日志健康状态", description = "返回当前系统的日志健康状态，包括错误率和状态评估")
    public Mono<LogMonitoringService.LogHealthStatus> getLogHealthStatus() {
        return Mono.just(logMonitoringService.getLogHealthStatus());
    }

    /**
     * 重置日志统计信息
     * @return 操作结果
     */
    @PostMapping(value = "/logs/reset", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "重置日志统计信息", description = "重置所有日志统计计数器")
    public Mono<ResponseEntity<String>> resetLogStats() {
        return Mono.fromRunnable(() -> logMonitoringService.resetLogStats())
                .then(Mono.just(ResponseEntity.ok("日志统计信息已重置")));
    }
}

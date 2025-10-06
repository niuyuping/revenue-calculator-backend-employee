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
 * Log monitoring controller
 * Provides API endpoints for log statistics and health status
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "Log Monitoring", description = "Log monitoring and statistics API")
public class LogMonitoringController {

    @Autowired
    private LogMonitoringService logMonitoringService;

    /**
     * Get log statistics
     * @return Log statistics information
     */
    @GetMapping(value = "/logs/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get log statistics", description = "Returns current system log statistics including counts and error rates for various log types")
    public Mono<LogMonitoringService.LogStats> getLogStats() {
        return Mono.just(logMonitoringService.getLogStats());
    }

    /**
     * Get log health status
     * @return Log health status
     */
    @GetMapping(value = "/logs/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get log health status", description = "Returns current system log health status including error rates and status assessment")
    public Mono<LogMonitoringService.LogHealthStatus> getLogHealthStatus() {
        return Mono.just(logMonitoringService.getLogHealthStatus());
    }

    /**
     * Reset log statistics
     * @return Operation result
     */
    @PostMapping(value = "/logs/reset", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Reset log statistics", description = "Reset all log statistics counters")
    public Mono<ResponseEntity<String>> resetLogStats() {
        return Mono.fromRunnable(() -> logMonitoringService.resetLogStats())
                .then(Mono.just(ResponseEntity.ok("Log statistics have been reset")));
    }
}

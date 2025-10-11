package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.service.SystemMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * System monitoring controller
 * Provides comprehensive API endpoints for system monitoring including database and transaction statistics
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "System Monitoring", description = "Comprehensive system monitoring and statistics API")
public class SystemMonitoringController {

    @Autowired
    private SystemMonitoringService systemMonitoringService;

    /**
     * Get comprehensive database monitoring information
     * @return Complete database monitoring information including health, performance, and table statistics
     */
    @GetMapping(value = "/database/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get comprehensive database statistics", description = "Returns complete database monitoring information including health status, performance metrics, and table statistics")
    public Mono<SystemMonitoringService.DatabaseStats> getDatabaseStats() {
        return systemMonitoringService.getDatabaseStats();
    }

    /**
     * Get transaction statistics
     * @return Transaction statistics information
     */
    @GetMapping(value = "/transaction/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get transaction statistics", description = "Returns current system transaction statistics including start, commit, rollback and error counts")
    public Mono<SystemMonitoringService.TransactionStats> getTransactionStats() {
        return Mono.just(systemMonitoringService.getTransactionStats());
    }
}

package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.service.DatabaseMonitoringService;
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
 * Database monitoring controller
 * Provides API endpoints for database monitoring and statistics
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "Database Monitoring", description = "Database monitoring and statistics API")
public class DatabaseMonitoringController {

    @Autowired
    private DatabaseMonitoringService databaseMonitoringService;

    /**
     * Get database connection statistics
     * @return Database connection statistics information
     */
    @GetMapping(value = "/database/connection/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get database connection statistics", description = "Returns current database connection pool statistics including active, idle, and total connections")
    public Mono<DatabaseMonitoringService.ConnectionStats> getConnectionStats() {
        return databaseMonitoringService.getConnectionStats();
    }

    /**
     * Get database performance statistics
     * @return Database performance statistics information
     */
    @GetMapping(value = "/database/performance/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get database performance statistics", description = "Returns database performance metrics including query execution times, connection times, and error rates")
    public Mono<DatabaseMonitoringService.PerformanceStats> getPerformanceStats() {
        return databaseMonitoringService.getPerformanceStats();
    }

    /**
     * Get database health information
     * @return Database health information
     */
    @GetMapping(value = "/database/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get database health information", description = "Returns detailed database health information including connection status, version, and configuration")
    public Mono<DatabaseMonitoringService.DatabaseHealth> getDatabaseHealth() {
        return databaseMonitoringService.getDatabaseHealth();
    }

    /**
     * Get database table statistics
     * @return Database table statistics information
     */
    @GetMapping(value = "/database/table/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get database table statistics", description = "Returns statistics for database tables including row counts, table sizes, and index information")
    public Mono<DatabaseMonitoringService.TableStats> getTableStats() {
        return databaseMonitoringService.getTableStats();
    }
}

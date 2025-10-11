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
     * Get comprehensive database monitoring information
     * @return Complete database monitoring information including health, performance, and table statistics
     */
    @GetMapping(value = "/database/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get comprehensive database statistics", description = "Returns complete database monitoring information including health status, performance metrics, and table statistics")
    public Mono<DatabaseMonitoringService.DatabaseStats> getDatabaseStats() {
        return databaseMonitoringService.getDatabaseStats();
    }
}

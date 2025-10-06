package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.service.CacheMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Cache monitoring controller
 * Provides API endpoints for cache statistics and management
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "Cache Monitoring", description = "Cache monitoring and management API")
public class CacheMonitoringController {

    @Autowired
    private CacheMonitoringService cacheMonitoringService;

    /**
     * Get cache statistics
     * @return Cache statistics information
     */
    @GetMapping(value = "/cache/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get cache statistics", description = "Returns current system cache statistics including hit rates, operation counts, etc.")
    public Mono<CacheMonitoringService.CacheStats> getCacheStats() {
        return Mono.just(cacheMonitoringService.getCacheStats());
    }

    /**
     * Clear all caches
     * @return Operation result
     */
    @DeleteMapping(value = "/cache/clear", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Clear all caches", description = "Clear all cache data in the system")
    public Mono<ResponseEntity<String>> clearAllCaches() {
        return Mono.fromRunnable(() -> cacheMonitoringService.clearAllCaches())
                .then(Mono.just(ResponseEntity.ok("All caches have been cleared")));
    }

    /**
     * Clear specified cache
     * @param cacheName Cache name
     * @return Operation result
     */
    @DeleteMapping(value = "/cache/clear/{cacheName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Clear specified cache", description = "Clear cache data for the specified cache name")
    public Mono<ResponseEntity<String>> clearCache(@PathVariable String cacheName) {
        return Mono.fromRunnable(() -> cacheMonitoringService.clearCache(cacheName))
                .then(Mono.just(ResponseEntity.ok("Cache " + cacheName + " has been cleared")));
    }
}

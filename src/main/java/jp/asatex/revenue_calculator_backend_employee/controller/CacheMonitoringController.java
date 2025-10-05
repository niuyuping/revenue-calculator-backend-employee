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
 * 缓存监控控制器
 * 提供缓存统计信息和管理的API端点
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "缓存监控", description = "缓存监控和管理API")
public class CacheMonitoringController {

    @Autowired
    private CacheMonitoringService cacheMonitoringService;

    /**
     * 获取缓存统计信息
     * @return 缓存统计信息
     */
    @GetMapping(value = "/cache/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "获取缓存统计信息", description = "返回当前系统的缓存统计信息，包括命中率、操作次数等")
    public Mono<CacheMonitoringService.CacheStats> getCacheStats() {
        return Mono.just(cacheMonitoringService.getCacheStats());
    }

    /**
     * 清除所有缓存
     * @return 操作结果
     */
    @DeleteMapping(value = "/cache/clear", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "清除所有缓存", description = "清除系统中所有的缓存数据")
    public Mono<ResponseEntity<String>> clearAllCaches() {
        return Mono.fromRunnable(() -> cacheMonitoringService.clearAllCaches())
                .then(Mono.just(ResponseEntity.ok("所有缓存已清除")));
    }

    /**
     * 清除指定缓存
     * @param cacheName 缓存名称
     * @return 操作结果
     */
    @DeleteMapping(value = "/cache/clear/{cacheName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "清除指定缓存", description = "清除指定名称的缓存数据")
    public Mono<ResponseEntity<String>> clearCache(@PathVariable String cacheName) {
        return Mono.fromRunnable(() -> cacheMonitoringService.clearCache(cacheName))
                .then(Mono.just(ResponseEntity.ok("缓存 " + cacheName + " 已清除")));
    }
}

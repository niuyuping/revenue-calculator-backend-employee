package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存监控服务
 * 提供缓存相关的监控和统计功能
 */
@Service
public class CacheMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(CacheMonitoringService.class);

    private final CacheManager cacheManager;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Counter cacheEvictCounter;
    private final Counter cachePutCounter;
    private final Timer cacheOperationTimer;

    // 缓存统计信息
    private final Map<String, AtomicLong> cacheHitCounts = new HashMap<>();
    private final Map<String, AtomicLong> cacheMissCounts = new HashMap<>();
    private final Map<String, AtomicLong> cacheEvictCounts = new HashMap<>();
    private final Map<String, AtomicLong> cachePutCounts = new HashMap<>();

    public CacheMonitoringService(CacheManager cacheManager, MeterRegistry meterRegistry) {
        this.cacheManager = cacheManager;
        
        this.cacheHitCounter = Counter.builder("cache.hit")
                .description("缓存命中次数")
                .register(meterRegistry);

        this.cacheMissCounter = Counter.builder("cache.miss")
                .description("缓存未命中次数")
                .register(meterRegistry);

        this.cacheEvictCounter = Counter.builder("cache.evict")
                .description("缓存清除次数")
                .register(meterRegistry);

        this.cachePutCounter = Counter.builder("cache.put")
                .description("缓存存储次数")
                .register(meterRegistry);

        this.cacheOperationTimer = Timer.builder("cache.operation.duration")
                .description("缓存操作执行时间")
                .register(meterRegistry);

        // 初始化缓存统计
        initializeCacheStatistics();
    }

    /**
     * 初始化缓存统计信息
     */
    private void initializeCacheStatistics() {
        for (String cacheName : cacheManager.getCacheNames()) {
            cacheHitCounts.put(cacheName, new AtomicLong(0));
            cacheMissCounts.put(cacheName, new AtomicLong(0));
            cacheEvictCounts.put(cacheName, new AtomicLong(0));
            cachePutCounts.put(cacheName, new AtomicLong(0));
        }
    }

    /**
     * 记录缓存命中
     * @param cacheName 缓存名称
     * @param key 缓存键
     */
    public void recordCacheHit(String cacheName, String key) {
        cacheHitCounter.increment();
        cacheHitCounts.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
        
        logger.debug("缓存命中 - 缓存: {}, 键: {}", cacheName, key);
    }

    /**
     * 记录缓存未命中
     * @param cacheName 缓存名称
     * @param key 缓存键
     */
    public void recordCacheMiss(String cacheName, String key) {
        cacheMissCounter.increment();
        cacheMissCounts.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
        
        logger.debug("缓存未命中 - 缓存: {}, 键: {}", cacheName, key);
    }

    /**
     * 记录缓存存储
     * @param cacheName 缓存名称
     * @param key 缓存键
     */
    public void recordCachePut(String cacheName, String key) {
        cachePutCounter.increment();
        cachePutCounts.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
        
        logger.debug("缓存存储 - 缓存: {}, 键: {}", cacheName, key);
    }

    /**
     * 记录缓存清除
     * @param cacheName 缓存名称
     * @param key 缓存键（如果为null表示清除所有）
     */
    public void recordCacheEvict(String cacheName, String key) {
        cacheEvictCounter.increment();
        cacheEvictCounts.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
        
        if (key != null) {
            logger.debug("缓存清除 - 缓存: {}, 键: {}", cacheName, key);
        } else {
            logger.debug("缓存全部清除 - 缓存: {}", cacheName);
        }
    }

    /**
     * 记录缓存操作时间
     * @param operation 操作类型
     * @param duration 执行时间（毫秒）
     */
    public void recordCacheOperationTime(String operation, long duration) {
        cacheOperationTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        logger.debug("缓存操作时间 - 操作: {}, 耗时: {}ms", operation, duration);
    }

    /**
     * 获取缓存统计信息
     * @return 缓存统计信息
     */
    public CacheStats getCacheStats() {
        Map<String, CacheInfo> cacheInfos = new HashMap<>();
        
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                CacheInfo cacheInfo = new CacheInfo(
                        cacheName,
                        cacheHitCounts.getOrDefault(cacheName, new AtomicLong(0)).get(),
                        cacheMissCounts.getOrDefault(cacheName, new AtomicLong(0)).get(),
                        cachePutCounts.getOrDefault(cacheName, new AtomicLong(0)).get(),
                        cacheEvictCounts.getOrDefault(cacheName, new AtomicLong(0)).get(),
                        cacheOperationTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)
                );
                cacheInfos.put(cacheName, cacheInfo);
            }
        }

        return new CacheStats(cacheInfos);
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                recordCacheEvict(cacheName, null);
                logger.info("清除缓存: {}", cacheName);
            }
        }
    }

    /**
     * 清除指定缓存
     * @param cacheName 缓存名称
     */
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            recordCacheEvict(cacheName, null);
            logger.info("清除缓存: {}", cacheName);
        }
    }

    /**
     * 缓存统计信息类
     */
    public static class CacheStats {
        private final Map<String, CacheInfo> cacheInfos;
        private final long totalHits;
        private final long totalMisses;
        private final long totalPuts;
        private final long totalEvicts;
        private final double hitRate;

        public CacheStats(Map<String, CacheInfo> cacheInfos) {
            this.cacheInfos = cacheInfos;
            this.totalHits = cacheInfos.values().stream().mapToLong(CacheInfo::getHits).sum();
            this.totalMisses = cacheInfos.values().stream().mapToLong(CacheInfo::getMisses).sum();
            this.totalPuts = cacheInfos.values().stream().mapToLong(CacheInfo::getPuts).sum();
            this.totalEvicts = cacheInfos.values().stream().mapToLong(CacheInfo::getEvicts).sum();
            this.hitRate = (totalHits + totalMisses) > 0 ? (double) totalHits / (totalHits + totalMisses) : 0.0;
        }

        public Map<String, CacheInfo> getCacheInfos() { return cacheInfos; }
        public long getTotalHits() { return totalHits; }
        public long getTotalMisses() { return totalMisses; }
        public long getTotalPuts() { return totalPuts; }
        public long getTotalEvicts() { return totalEvicts; }
        public double getHitRate() { return hitRate; }

        @Override
        public String toString() {
            return String.format("CacheStats{hits=%d, misses=%d, puts=%d, evicts=%d, hitRate=%.2f%%}",
                    totalHits, totalMisses, totalPuts, totalEvicts, hitRate * 100);
        }
    }

    /**
     * 单个缓存信息类
     */
    public static class CacheInfo {
        private final String name;
        private final long hits;
        private final long misses;
        private final long puts;
        private final long evicts;
        private final double averageOperationTime;

        public CacheInfo(String name, long hits, long misses, long puts, long evicts, double averageOperationTime) {
            this.name = name;
            this.hits = hits;
            this.misses = misses;
            this.puts = puts;
            this.evicts = evicts;
            this.averageOperationTime = averageOperationTime;
        }

        public String getName() { return name; }
        public long getHits() { return hits; }
        public long getMisses() { return misses; }
        public long getPuts() { return puts; }
        public long getEvicts() { return evicts; }
        public double getAverageOperationTime() { return averageOperationTime; }
        public double getHitRate() {
            return (hits + misses) > 0 ? (double) hits / (hits + misses) : 0.0;
        }

        @Override
        public String toString() {
            return String.format("CacheInfo{name='%s', hits=%d, misses=%d, puts=%d, evicts=%d, hitRate=%.2f%%, avgTime=%.2fms}",
                    name, hits, misses, puts, evicts, getHitRate() * 100, averageOperationTime);
        }
    }
}

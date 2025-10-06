package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Cache monitoring service
 * Provides cache-related monitoring and statistics functionality
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

    // Cache statistics
    private final Map<String, AtomicLong> cacheHitCounts = new HashMap<>();
    private final Map<String, AtomicLong> cacheMissCounts = new HashMap<>();
    private final Map<String, AtomicLong> cacheEvictCounts = new HashMap<>();
    private final Map<String, AtomicLong> cachePutCounts = new HashMap<>();

    public CacheMonitoringService(CacheManager cacheManager, MeterRegistry meterRegistry) {
        this.cacheManager = cacheManager;
        
        this.cacheHitCounter = Counter.builder("cache.hit")
                .description("Cache hit count")
                .register(meterRegistry);

        this.cacheMissCounter = Counter.builder("cache.miss")
                .description("Cache miss count")
                .register(meterRegistry);

        this.cacheEvictCounter = Counter.builder("cache.evict")
                .description("Cache evict count")
                .register(meterRegistry);

        this.cachePutCounter = Counter.builder("cache.put")
                .description("Cache put count")
                .register(meterRegistry);

        this.cacheOperationTimer = Timer.builder("cache.operation.duration")
                .description("Cache operation execution time")
                .register(meterRegistry);

        // Initialize cache statistics
        initializeCacheStatistics();
    }

    /**
     * Initialize cache statistics
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
     * Record cache hit
     * @param cacheName Cache name
     * @param key Cache key
     */
    public void recordCacheHit(String cacheName, String key) {
        cacheHitCounter.increment();
        cacheHitCounts.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
        
        logger.debug("Cache hit - Cache: {}, Key: {}", cacheName, key);
    }

    /**
     * Record cache miss
     * @param cacheName Cache name
     * @param key Cache key
     */
    public void recordCacheMiss(String cacheName, String key) {
        cacheMissCounter.increment();
        cacheMissCounts.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
        
        logger.debug("Cache miss - Cache: {}, Key: {}", cacheName, key);
    }

    /**
     * Record cache put
     * @param cacheName Cache name
     * @param key Cache key
     */
    public void recordCachePut(String cacheName, String key) {
        cachePutCounter.increment();
        cachePutCounts.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
        
        logger.debug("Cache put - Cache: {}, Key: {}", cacheName, key);
    }

    /**
     * Record cache evict
     * @param cacheName Cache name
     * @param key Cache key (null means evict all)
     */
    public void recordCacheEvict(String cacheName, String key) {
        cacheEvictCounter.increment();
        cacheEvictCounts.computeIfAbsent(cacheName, k -> new AtomicLong(0)).incrementAndGet();
        
        if (key != null) {
            logger.debug("Cache evict - Cache: {}, Key: {}", cacheName, key);
        } else {
            logger.debug("Cache evict all - Cache: {}", cacheName);
        }
    }

    /**
     * Record cache operation time
     * @param operation Operation type
     * @param duration Execution time (milliseconds)
     */
    public void recordCacheOperationTime(String operation, long duration) {
        cacheOperationTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        logger.debug("Cache operation time - Operation: {}, Duration: {}ms", operation, duration);
    }

    /**
     * Get cache statistics
     * @return Cache statistics
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
     * Clear all caches
     */
    public void clearAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                recordCacheEvict(cacheName, null);
                logger.info("Clear cache: {}", cacheName);
            }
        }
    }

    /**
     * Clear specified cache
     * @param cacheName Cache name
     */
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            recordCacheEvict(cacheName, null);
            logger.info("Clear cache: {}", cacheName);
        }
    }

    /**
     * Cache statistics class
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
     * Individual cache info class
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

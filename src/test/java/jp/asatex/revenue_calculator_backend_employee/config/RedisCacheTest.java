package jp.asatex.revenue_calculator_backend_employee.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis cache functionality test
 * Verifies Redis connection and cache functionality work properly
 * Uses local Docker Redis service
 */
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestContainersConfig.class})
class RedisCacheTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedisConnection() {
        // Test Redis connection
        redisTemplate.opsForValue().set("test:key", "test:value");
        String value = (String) redisTemplate.opsForValue().get("test:key");
        
        assertThat(value).isEqualTo("test:value");
        
        // Clean up test data
        redisTemplate.delete("test:key");
    }

    @Test
    void testCacheManager() {
        // Verify cache manager is configured correctly
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCacheNames()).contains("employees", "employeeList", "employeeSearch", "employeePagination");
    }

    @Test
    void testCacheOperations() {
        // Test cache operations
        var cache = cacheManager.getCache("employees");
        assertThat(cache).isNotNull();
        
        // Ensure cache is not null before operations
        if (cache != null) {
            // Store cache
            cache.put("test:employee:1", "test:employee:data");
            
            // Get cache
            var cachedValue = cache.get("test:employee:1");
            assertThat(cachedValue).isNotNull();
            if (cachedValue != null) {
                assertThat(cachedValue.get()).isEqualTo("test:employee:data");
            }
            
            // Clean up test data
            cache.evict("test:employee:1");
        }
    }
}


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
 * Redis缓存功能测试
 * 验证Redis连接和缓存功能是否正常工作
 * 使用本地Docker Redis服务
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
        // 测试Redis连接
        redisTemplate.opsForValue().set("test:key", "test:value");
        String value = (String) redisTemplate.opsForValue().get("test:key");
        
        assertThat(value).isEqualTo("test:value");
        
        // 清理测试数据
        redisTemplate.delete("test:key");
    }

    @Test
    void testCacheManager() {
        // 验证缓存管理器是否正确配置
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCacheNames()).contains("employees", "employeeList", "employeeSearch", "employeePagination");
    }

    @Test
    void testCacheOperations() {
        // 测试缓存操作
        var cache = cacheManager.getCache("employees");
        assertThat(cache).isNotNull();
        
        // 存储缓存
        cache.put("test:employee:1", "test:employee:data");
        
        // 获取缓存
        var cachedValue = cache.get("test:employee:1");
        assertThat(cachedValue).isNotNull();
        assertThat(cachedValue.get()).isEqualTo("test:employee:data");
        
        // 清理测试数据
        cache.evict("test:employee:1");
    }
}


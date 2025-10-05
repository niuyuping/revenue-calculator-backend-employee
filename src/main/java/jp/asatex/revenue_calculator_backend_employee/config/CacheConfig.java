package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存配置类
 * 配置Redis缓存管理器和缓存策略
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired
    private GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer;

    /**
     * 配置Redis缓存管理器
     * @param connectionFactory Redis连接工厂
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 默认30分钟过期
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(genericJackson2JsonRedisSerializer))
                .disableCachingNullValues(); // 不缓存null值

        // 不同缓存的特定配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 员工信息缓存 - 1小时过期
        cacheConfigurations.put("employees", defaultConfig
                .entryTtl(Duration.ofHours(1))
                .prefixCacheNameWith("employee:"));

        // 员工列表缓存 - 30分钟过期
        cacheConfigurations.put("employeeList", defaultConfig
                .entryTtl(Duration.ofMinutes(30))
                .prefixCacheNameWith("employee_list:"));

        // 员工搜索缓存 - 15分钟过期
        cacheConfigurations.put("employeeSearch", defaultConfig
                .entryTtl(Duration.ofMinutes(15))
                .prefixCacheNameWith("employee_search:"));

        // 分页缓存 - 10分钟过期
        cacheConfigurations.put("employeePagination", defaultConfig
                .entryTtl(Duration.ofMinutes(10))
                .prefixCacheNameWith("employee_pagination:"));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * 配置RedisTemplate
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 缓存键生成器
     * 用于生成统一的缓存键格式
     */
    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new CacheKeyGenerator();
    }

    /**
     * 自定义缓存键生成器
     */
    public static class CacheKeyGenerator {
        
        /**
         * 生成员工缓存键
         * @param id 员工ID
         * @return 缓存键
         */
        public String generateEmployeeKey(Long id) {
            return "employee:" + id;
        }

        /**
         * 生成员工号缓存键
         * @param employeeNumber 员工号
         * @return 缓存键
         */
        public String generateEmployeeNumberKey(String employeeNumber) {
            return "employee_number:" + employeeNumber;
        }

        /**
         * 生成员工列表缓存键
         * @param page 页码
         * @param size 每页大小
         * @param sortBy 排序字段
         * @param sortDirection 排序方向
         * @return 缓存键
         */
        public String generateEmployeeListKey(int page, int size, String sortBy, String sortDirection) {
            return String.format("employee_list:page_%d_size_%d_sort_%s_%s", 
                    page, size, sortBy, sortDirection);
        }

        /**
         * 生成员工搜索缓存键
         * @param searchType 搜索类型 (name/furigana)
         * @param keyword 搜索关键词
         * @param page 页码
         * @param size 每页大小
         * @param sortBy 排序字段
         * @param sortDirection 排序方向
         * @return 缓存键
         */
        public String generateEmployeeSearchKey(String searchType, String keyword, 
                                              int page, int size, String sortBy, String sortDirection) {
            return String.format("employee_search:%s_%s_page_%d_size_%d_sort_%s_%s", 
                    searchType, keyword, page, size, sortBy, sortDirection);
        }

        /**
         * 生成分页缓存键
         * @param operation 操作类型
         * @param params 参数
         * @return 缓存键
         */
        public String generatePaginationKey(String operation, Object... params) {
            StringBuilder key = new StringBuilder("pagination:").append(operation);
            for (Object param : params) {
                key.append(":").append(param != null ? param.toString() : "null");
            }
            return key.toString();
        }
    }
}
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
 * Cache configuration class
 * Configures Redis cache manager and cache strategies
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired
    private GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer;

    /**
     * Configure Redis cache manager
     * @param connectionFactory Redis connection factory
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // Default 30 minutes expiration
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(genericJackson2JsonRedisSerializer))
                .disableCachingNullValues(); // Do not cache null values

        // Specific configurations for different caches
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Employee information cache - 1 hour expiration
        cacheConfigurations.put("employees", defaultConfig
                .entryTtl(Duration.ofHours(1))
                .prefixCacheNameWith("employee:"));

        // Employee list cache - 30 minutes expiration
        cacheConfigurations.put("employeeList", defaultConfig
                .entryTtl(Duration.ofMinutes(30))
                .prefixCacheNameWith("employee_list:"));

        // Employee search cache - 15 minutes expiration
        cacheConfigurations.put("employeeSearch", defaultConfig
                .entryTtl(Duration.ofMinutes(15))
                .prefixCacheNameWith("employee_search:"));

        // Pagination cache - 10 minutes expiration
        cacheConfigurations.put("employeePagination", defaultConfig
                .entryTtl(Duration.ofMinutes(10))
                .prefixCacheNameWith("employee_pagination:"));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Configure RedisTemplate
     * @param connectionFactory Redis connection factory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Set serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Cache key generator
     * Used to generate unified cache key format
     */
    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new CacheKeyGenerator();
    }

    /**
     * Custom cache key generator
     */
    public static class CacheKeyGenerator {
        
        /**
         * Generate employee cache key
         * @param id Employee ID
         * @return Cache key
         */
        public String generateEmployeeKey(Long id) {
            return "employee:" + id;
        }

        /**
         * Generate employee number cache key
         * @param employeeNumber Employee number
         * @return Cache key
         */
        public String generateEmployeeNumberKey(String employeeNumber) {
            return "employee_number:" + employeeNumber;
        }

        /**
         * Generate employee list cache key
         * @param page Page number
         * @param size Page size
         * @param sortBy Sort field
         * @param sortDirection Sort direction
         * @return Cache key
         */
        public String generateEmployeeListKey(int page, int size, String sortBy, String sortDirection) {
            return String.format("employee_list:page_%d_size_%d_sort_%s_%s", 
                    page, size, sortBy, sortDirection);
        }

        /**
         * Generate employee search cache key
         * @param searchType Search type (name/furigana)
         * @param keyword Search keyword
         * @param page Page number
         * @param size Page size
         * @param sortBy Sort field
         * @param sortDirection Sort direction
         * @return Cache key
         */
        public String generateEmployeeSearchKey(String searchType, String keyword, 
                                              int page, int size, String sortBy, String sortDirection) {
            return String.format("employee_search:%s_%s_page_%d_size_%d_sort_%s_%s", 
                    searchType, keyword, page, size, sortBy, sortDirection);
        }

        /**
         * Generate pagination cache key
         * @param operation Operation type
         * @param params Parameters
         * @return Cache key
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
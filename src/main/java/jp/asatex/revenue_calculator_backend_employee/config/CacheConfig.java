package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 * 使用 Caffeine 作为内存缓存实现，无需 Redis
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置 Caffeine 缓存管理器
     * 这是 Spring Boot 的默认缓存实现
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // 设置缓存名称
        cacheManager.setCacheNames(java.util.Arrays.asList("employees"));
        
        return cacheManager;
    }
}

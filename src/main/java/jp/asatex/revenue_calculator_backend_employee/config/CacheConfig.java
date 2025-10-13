package jp.asatex.revenue_calculator_backend_employee.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration Class
 * Uses Caffeine as in-memory cache implementation, no Redis required
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Caffeine cache manager
     * This is Spring Boot's default cache implementation
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Set cache names
        cacheManager.setCacheNames(java.util.Arrays.asList("employees"));

        // Enable async cache mode for Resilience4j compatibility
        cacheManager.setAsyncCacheMode(true);

        // Configure Caffeine cache with statistics enabled
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)                    // Maximum cache entries
                .expireAfterWrite(5, TimeUnit.MINUTES) // Expire 5 minutes after write
                .expireAfterAccess(2, TimeUnit.MINUTES) // Expire 2 minutes after access
                .recordStats()                        // Enable statistics recording
        );

        return cacheManager;
    }
}

package jp.asatex.revenue_calculator_backend_employee.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CacheConfig test class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {"spring.flyway.enabled=false"})
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("CacheConfig Test")
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheConfig.CacheKeyGenerator cacheKeyGenerator;

    @Test
    @DisplayName("Cache manager should be configured correctly")
    void testCacheManagerConfiguration() {
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
    }

    @Test
    @DisplayName("Cache key generator should be configured correctly")
    void testCacheKeyGeneratorConfiguration() {
        assertThat(cacheKeyGenerator).isNotNull();
    }

    @Test
    @DisplayName("Employee cache key generation should work correctly")
    void testGenerateEmployeeKey() {
        String key = cacheKeyGenerator.generateEmployeeKey(1L);
        assertThat(key).isEqualTo("employee:1");
    }

    @Test
    @DisplayName("Employee number cache key generation should work correctly")
    void testGenerateEmployeeNumberKey() {
        String key = cacheKeyGenerator.generateEmployeeNumberKey("EMP001");
        assertThat(key).isEqualTo("employee_number:EMP001");
    }

    @Test
    @DisplayName("Employee list cache key generation should work correctly")
    void testGenerateEmployeeListKey() {
        String key = cacheKeyGenerator.generateEmployeeListKey(0, 10, "name", "ASC");
        assertThat(key).isEqualTo("employee_list:page_0_size_10_sort_name_ASC");
    }

    @Test
    @DisplayName("Employee search cache key generation should work correctly")
    void testGenerateEmployeeSearchKey() {
        String key = cacheKeyGenerator.generateEmployeeSearchKey("name", "Tanaka", 0, 10, "name", "ASC");
        assertThat(key).isEqualTo("employee_search:name_Tanaka_page_0_size_10_sort_name_ASC");
    }

    @Test
    @DisplayName("Pagination cache key generation should work correctly")
    void testGeneratePaginationKey() {
        String key = cacheKeyGenerator.generatePaginationKey("getAll", 0, 10, "name", "ASC");
        assertThat(key).isEqualTo("pagination:getAll:0:10:name:ASC");
    }

    @Test
    @DisplayName("Pagination cache key generation should handle null values")
    void testGeneratePaginationKeyWithNull() {
        String key = cacheKeyGenerator.generatePaginationKey("getAll", null, 10, "name", "ASC");
        assertThat(key).isEqualTo("pagination:getAll:null:10:name:ASC");
    }
}

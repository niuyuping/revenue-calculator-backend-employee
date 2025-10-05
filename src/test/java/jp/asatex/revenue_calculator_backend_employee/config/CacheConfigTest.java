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
 * CacheConfig 测试类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {"spring.flyway.enabled=false"})
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("CacheConfig テスト")
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheConfig.CacheKeyGenerator cacheKeyGenerator;

    @Test
    @DisplayName("缓存管理器应该正确配置")
    void testCacheManagerConfiguration() {
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
    }

    @Test
    @DisplayName("缓存键生成器应该正确配置")
    void testCacheKeyGeneratorConfiguration() {
        assertThat(cacheKeyGenerator).isNotNull();
    }

    @Test
    @DisplayName("员工缓存键生成应该正确工作")
    void testGenerateEmployeeKey() {
        String key = cacheKeyGenerator.generateEmployeeKey(1L);
        assertThat(key).isEqualTo("employee:1");
    }

    @Test
    @DisplayName("员工号缓存键生成应该正确工作")
    void testGenerateEmployeeNumberKey() {
        String key = cacheKeyGenerator.generateEmployeeNumberKey("EMP001");
        assertThat(key).isEqualTo("employee_number:EMP001");
    }

    @Test
    @DisplayName("员工列表缓存键生成应该正确工作")
    void testGenerateEmployeeListKey() {
        String key = cacheKeyGenerator.generateEmployeeListKey(0, 10, "name", "ASC");
        assertThat(key).isEqualTo("employee_list:page_0_size_10_sort_name_ASC");
    }

    @Test
    @DisplayName("员工搜索缓存键生成应该正确工作")
    void testGenerateEmployeeSearchKey() {
        String key = cacheKeyGenerator.generateEmployeeSearchKey("name", "田中", 0, 10, "name", "ASC");
        assertThat(key).isEqualTo("employee_search:name_田中_page_0_size_10_sort_name_ASC");
    }

    @Test
    @DisplayName("分页缓存键生成应该正确工作")
    void testGeneratePaginationKey() {
        String key = cacheKeyGenerator.generatePaginationKey("getAll", 0, 10, "name", "ASC");
        assertThat(key).isEqualTo("pagination:getAll:0:10:name:ASC");
    }

    @Test
    @DisplayName("分页缓存键生成应该处理null值")
    void testGeneratePaginationKeyWithNull() {
        String key = cacheKeyGenerator.generatePaginationKey("getAll", null, 10, "name", "ASC");
        assertThat(key).isEqualTo("pagination:getAll:null:10:name:ASC");
    }
}

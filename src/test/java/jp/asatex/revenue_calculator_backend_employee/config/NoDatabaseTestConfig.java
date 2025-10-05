package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * 无数据库测试配置
 * 用于不需要数据库连接的配置测试
 */
@TestConfiguration
@Profile("test")
public class NoDatabaseTestConfig {

    @Bean
    @Primary
    @Profile("test")
    public String disableFlyway() {
        // 禁用 Flyway 的占位符 Bean
        return "flyway-disabled";
    }
}

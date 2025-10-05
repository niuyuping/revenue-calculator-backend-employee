package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * TestContainers配置类
 * 为测试提供PostgreSQL容器
 * Redis使用本地Docker服务
 */
@TestConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    @SuppressWarnings("resource") // TestContainers在Spring Boot测试环境中会自动管理容器生命周期
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("asatex-revenue-test")
                .withUsername("db_user")
                .withPassword("local")
                .waitingFor(Wait.forListeningPort())
                .withReuse(true);
        
        // 启动容器
        container.start();
        return container;
    }

}

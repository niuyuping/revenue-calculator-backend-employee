package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * 数据库审计配置类
 * 启用R2DBC审计功能和定时清理任务
 */
@Configuration
@EnableR2dbcRepositories(basePackages = "jp.asatex.revenue_calculator_backend_employee.repository")
@EnableR2dbcAuditing
@EnableScheduling
public class DatabaseAuditConfig {

    /**
     * 配置数据库客户端
     * @param connectionFactory 连接工厂
     * @return DatabaseClient
     */
    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.create(connectionFactory);
    }

    /**
     * 定时清理过期的审计日志
     * 每天凌晨2点执行，保留90天的审计日志
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldAuditLogs() {
        // 这个任务会在DatabaseAuditService中实现
        // 这里只是配置定时任务的调度
    }
}

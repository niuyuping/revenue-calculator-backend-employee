package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Database audit configuration class
 * Enables R2DBC auditing functionality and scheduled cleanup tasks
 */
@Configuration
@EnableR2dbcRepositories(basePackages = "jp.asatex.revenue_calculator_backend_employee.repository")
@EnableR2dbcAuditing
@EnableScheduling
public class DatabaseAuditConfig {

    /**
     * Configure database client
     * @param connectionFactory Connection factory
     * @return DatabaseClient
     */
    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.create(connectionFactory);
    }

    /**
     * Scheduled cleanup of expired audit logs
     * Executes daily at 2 AM, retains audit logs for 90 days
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldAuditLogs() {
        // This task will be implemented in DatabaseAuditService
        // This is just configuring the scheduled task
    }
}

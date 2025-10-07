package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

/**
 * Production-specific R2DBC configuration
 * Ensures R2DBC beans are properly configured for production environment
 */
@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod")
public class ProductionR2dbcConfig {

    /**
     * Configure R2DBC entity template for production
     * This ensures the bean is available even if auto-configuration fails
     * @param connectionFactory Connection factory
     * @return R2dbcEntityTemplate
     */
    @Bean
    @Primary
    public R2dbcEntityTemplate productionR2dbcEntityTemplate(@Autowired ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    /**
     * Configure database client for production
     * @param connectionFactory Connection factory
     * @return DatabaseClient
     */
    @Bean
    @Primary
    public DatabaseClient productionDatabaseClient(@Autowired ConnectionFactory connectionFactory) {
        return DatabaseClient.create(connectionFactory);
    }
}

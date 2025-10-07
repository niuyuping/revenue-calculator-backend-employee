package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

/**
 * R2DBC configuration class
 * Configures R2DBC entity template and database client beans
 * ConnectionFactory is auto-configured by Spring Boot based on application properties
 */
@Configuration
public class R2dbcConfig {

    /**
     * Configure R2DBC entity template
     * Required for Spring Data R2DBC repositories
     * @param connectionFactory Connection factory (auto-configured by Spring Boot)
     * @return R2dbcEntityTemplate
     */
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    /**
     * Configure database client
     * @param connectionFactory Connection factory (auto-configured by Spring Boot)
     * @return DatabaseClient
     */
    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.create(connectionFactory);
    }
}

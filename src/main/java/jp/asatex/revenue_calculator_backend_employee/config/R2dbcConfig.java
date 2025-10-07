package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

import java.time.Duration;

/**
 * R2DBC configuration class
 * Configures PostgreSQL connection factory and related beans
 */
@Configuration
public class R2dbcConfig {

    @Value("${spring.r2dbc.url}")
    private String url;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password:}")
    private String password;

    @Value("${spring.r2dbc.pool.max-size:10}")
    private int maxSize;

    @Value("${spring.r2dbc.pool.max-idle-time:PT10S}")
    private Duration maxIdleTime;

    @Value("${spring.r2dbc.pool.max-life-time:PT10S}")
    private Duration maxLifeTime;

    @Value("${spring.r2dbc.pool.initial-size:1}")
    private int initialSize;

    /**
     * Configure PostgreSQL connection factory
     * @return ConnectionFactory
     */
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        // Parse the R2DBC URL to extract connection details
        String[] urlParts = url.replace("r2dbc:postgresql://", "").split("/");
        String[] hostPort = urlParts[0].split(":");
        String host = hostPort[0];
        int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 5432;
        String database = urlParts.length > 1 ? urlParts[1] : "postgres";

        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(username)
                .password(password)
                .build();

        PostgresqlConnectionFactory connectionFactory = new PostgresqlConnectionFactory(config);

        // Configure connection pool
        ConnectionPoolConfiguration poolConfig = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxSize(maxSize)
                .maxIdleTime(maxIdleTime)
                .maxLifeTime(maxLifeTime)
                .initialSize(initialSize)
                .build();

        return new ConnectionPool(poolConfig);
    }

    /**
     * Configure R2DBC entity template
     * @param connectionFactory Connection factory
     * @return R2dbcEntityTemplate
     */
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    /**
     * Configure database client
     * @param connectionFactory Connection factory
     * @return DatabaseClient
     */
    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.create(connectionFactory);
    }
}

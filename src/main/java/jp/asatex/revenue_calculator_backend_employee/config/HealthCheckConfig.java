package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

/**
 * 自定义健康检查配置
 * 提供数据库和Redis的详细健康状态信息
 */
@Component
public class HealthCheckConfig {

    /**
     * 数据库健康检查指示器
     */
    @Component
    public static class DatabaseHealthIndicator implements HealthIndicator {
        
        private final DatabaseClient databaseClient;
        
        public DatabaseHealthIndicator(DatabaseClient databaseClient) {
            this.databaseClient = databaseClient;
        }
        
        @Override
        public Health health() {
            try {
                return databaseClient.sql("SELECT 1")
                        .fetch()
                        .first()
                        .map(result -> Health.up()
                                .withDetail("database", "PostgreSQL")
                                .withDetail("status", "Connected")
                                .withDetail("validationQuery", "SELECT 1")
                                .build())
                        .onErrorReturn(Health.down()
                                .withDetail("database", "PostgreSQL")
                                .withDetail("status", "Connection failed")
                                .withDetail("error", "Database connection error")
                                .build())
                        .block();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connection failed")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        }
    }
    
    /**
     * Redis健康检查指示器
     */
    @Component
    public static class RedisHealthIndicator implements HealthIndicator {
        
        private final ReactiveRedisTemplate<String, String> redisTemplate;
        
        public RedisHealthIndicator(ReactiveRedisTemplate<String, String> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }
        
        @Override
        public Health health() {
            try {
                return redisTemplate.opsForValue()
                        .set("health-check", "test", java.time.Duration.ofSeconds(10))
                        .then(redisTemplate.opsForValue().get("health-check"))
                        .map(result -> {
                            if ("test".equals(result)) {
                                return Health.up()
                                        .withDetail("redis", "Connected")
                                        .withDetail("status", "Available")
                                        .withDetail("test", "Ping successful")
                                        .build();
                            } else {
                                return Health.down()
                                        .withDetail("redis", "Connection failed")
                                        .withDetail("status", "Test failed")
                                        .withDetail("error", "Redis test operation failed")
                                        .build();
                            }
                        })
                        .onErrorReturn(Health.down()
                                .withDetail("redis", "Connection failed")
                                .withDetail("status", "Unavailable")
                                .withDetail("error", "Redis connection error")
                                .build())
                        .block();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("redis", "Connection failed")
                        .withDetail("status", "Unavailable")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        }
    }
}

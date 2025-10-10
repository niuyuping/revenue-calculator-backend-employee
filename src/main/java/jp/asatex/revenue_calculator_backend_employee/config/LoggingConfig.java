package jp.asatex.revenue_calculator_backend_employee.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Logging configuration class
 * Configures request logging and monitoring functionality
 */
@Configuration
public class LoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);

    /**
     * Request logging web filter
     * Logs incoming requests with timing and request ID
     */
    @Bean
    @Order(-100) // High priority to ensure it runs early
    public WebFilter requestLoggingFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            String requestId = UUID.randomUUID().toString();
            Instant startTime = Instant.now();
            
            // Log incoming request
            logger.info("Incoming request: {} {} - Request ID: {}", 
                       exchange.getRequest().getMethod(), 
                       exchange.getRequest().getPath(), 
                       requestId);
            
            // Add request ID to response headers
            exchange.getResponse().getHeaders().add("X-Request-ID", requestId);
            
            return chain.filter(exchange)
                    .doOnSuccess(aVoid -> {
                        Duration duration = Duration.between(startTime, Instant.now());
                        logger.info("Request completed: {} {} - Status: {} - Duration: {}ms - Request ID: {}", 
                                   exchange.getRequest().getMethod(), 
                                   exchange.getRequest().getPath(), 
                                   exchange.getResponse().getStatusCode(), 
                                   duration.toMillis(), 
                                   requestId);
                    })
                    .doOnError(error -> {
                        Duration duration = Duration.between(startTime, Instant.now());
                        logger.error("Request failed: {} {} - Error: {} - Duration: {}ms - Request ID: {}", 
                                    exchange.getRequest().getMethod(), 
                                    exchange.getRequest().getPath(), 
                                    error.getMessage(), 
                                    duration.toMillis(), 
                                    requestId, 
                                    error);
                    });
        };
    }

    /**
     * Error logging configuration
     * Configures structured error logging
     */
    @Bean
    public Logger errorLogger() {
        return LoggerFactory.getLogger("ERROR_LOGGER");
    }

    /**
     * Performance logging configuration
     * Configures performance monitoring logs
     */
    @Bean
    public Logger performanceLogger() {
        return LoggerFactory.getLogger("PERFORMANCE_LOGGER");
    }
}

package jp.asatex.revenue_calculator_backend_employee.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Logging utility class
 * Provides unified logging methods and context management
 */
public class LoggingUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoggingUtil.class);

    /**
     * Log business operation
     */
    public static void logBusinessOperation(String operation, String details, Object... args) {
        logger.info("Business operation: {} - Details: {}", operation, String.format(details, args));
    }

    /**
     * Log performance
     */
    public static void logPerformance(String operation, Duration duration, String details) {
        if (duration.toMillis() > 1000) {
            logger.warn("Slow operation detected: {} - Duration: {}ms - Details: {}", 
                    operation, duration.toMillis(), details);
        } else {
            logger.debug("Operation completed: {} - Duration: {}ms - Details: {}", 
                    operation, duration.toMillis(), details);
        }
    }

    /**
     * Log error
     */
    public static void logError(String operation, Throwable error, String details) {
        logger.error("Error in operation: {} - Details: {} - Error: {}", 
                operation, details, error.getMessage(), error);
    }

    /**
     * Log data access
     */
    public static void logDataAccess(String operation, String table, Object id) {
        logger.debug("Data access: {} on table {} with ID: {}", operation, table, id);
    }

    /**
     * Log security-related logs
     */
    public static void logSecurity(String event, String details) {
        logger.warn("Security event: {} - Details: {}", event, details);
    }

    /**
     * Set MDC context
     */
    public static void setContext(String key, String value) {
        MDC.put(key, value);
    }

    /**
     * Clear MDC context
     */
    public static void clearContext() {
        MDC.clear();
    }

    /**
     * Log operation time in reactive stream
     */
    public static <T> Mono<T> logOperationTime(String operation, Mono<T> mono) {
        Instant startTime = Instant.now();
        return mono
                .doOnSuccess(result -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    logPerformance(operation, duration, "Success");
                })
                .doOnError(error -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    logError(operation, error, "Duration: " + duration.toMillis() + "ms");
                });
    }

    /**
     * Log operation time in reactive stream (with context)
     */
    public static <T> Mono<T> logOperationTimeWithContext(String operation, Mono<T> mono, Map<String, String> context) {
        return mono
                .contextWrite(ctx -> {
                    Context newContext = ctx;
                    for (Map.Entry<String, String> entry : context.entrySet()) {
                        newContext = newContext.put(entry.getKey(), entry.getValue());
                    }
                    return newContext;
                })
                .transform(m -> logOperationTime(operation, m));
    }

    /**
     * Log API call statistics
     */
    public static void logApiCall(String endpoint, String method, int statusCode, Duration duration) {
        String level = statusCode >= 400 ? "ERROR" : (duration.toMillis() > 1000 ? "WARN" : "INFO");
        
        switch (level) {
            case "ERROR":
                logger.error("API call: {} {} - Status: {} - Duration: {}ms", 
                        method, endpoint, statusCode, duration.toMillis());
                break;
            case "WARN":
                logger.warn("Slow API call: {} {} - Status: {} - Duration: {}ms", 
                        method, endpoint, statusCode, duration.toMillis());
                break;
            default:
                logger.info("API call: {} {} - Status: {} - Duration: {}ms", 
                        method, endpoint, statusCode, duration.toMillis());
        }
    }
}


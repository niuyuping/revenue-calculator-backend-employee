package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Log monitoring service
 * Provides log-related monitoring and statistics functionality
 */
@Service
public class LogMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(LogMonitoringService.class);

    private final Counter auditLogCounter;
    private final Counter securityLogCounter;
    private final Counter performanceLogCounter;
    private final Counter errorLogCounter;
    private final Counter requestLogCounter;
    private final Timer logProcessingTimer;

    // Log statistics
    private final Map<String, AtomicLong> logCounts = new HashMap<>();
    private final Map<String, AtomicLong> errorCounts = new HashMap<>();
    private final AtomicLong totalLogs = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);

   public LogMonitoringService(MeterRegistry meterRegistry) {
        this.auditLogCounter = Counter.builder("logs.audit")
                .description("Audit log count")
                .register(meterRegistry);

        this.securityLogCounter = Counter.builder("logs.security")
                .description("Security log count")
                .register(meterRegistry);

        this.performanceLogCounter = Counter.builder("logs.performance")
                .description("Performance log count")
                .register(meterRegistry);

        this.errorLogCounter = Counter.builder("logs.error")
                .description("Error log count")
                .register(meterRegistry);

        this.requestLogCounter = Counter.builder("logs.request")
                .description("Request log count")
                .register(meterRegistry);

        this.logProcessingTimer = Timer.builder("logs.processing.duration")
                .description("Log processing time")
                .register(meterRegistry);

        // Initialize log statistics
        initializeLogStatistics();
    }

    /**
     * Initialize log statistics
     */
    private void initializeLogStatistics() {
        logCounts.put("AUDIT", new AtomicLong(0));
        logCounts.put("SECURITY", new AtomicLong(0));
        logCounts.put("PERFORMANCE", new AtomicLong(0));
        logCounts.put("ERROR", new AtomicLong(0));
        logCounts.put("REQUEST", new AtomicLong(0));
        logCounts.put("APPLICATION", new AtomicLong(0));

        errorCounts.put("VALIDATION_ERROR", new AtomicLong(0));
        errorCounts.put("BUSINESS_ERROR", new AtomicLong(0));
        errorCounts.put("SYSTEM_ERROR", new AtomicLong(0));
        errorCounts.put("SECURITY_ERROR", new AtomicLong(0));
    }

    /**
     * Record audit log
     * @param operation Operation type
     * @param resource Resource type
     */
    public void recordAuditLog(String operation, String resource) {
        auditLogCounter.increment();
        logCounts.get("AUDIT").incrementAndGet();
        totalLogs.incrementAndGet();
        
        logger.debug("Audit log recorded - Operation: {}, Resource: {}", operation, resource);
    }

    /**
     * Record security log
     * @param eventType Event type
     * @param severity Severity level
     */
    public void recordSecurityLog(String eventType, String severity) {
        securityLogCounter.increment();
        logCounts.get("SECURITY").incrementAndGet();
        totalLogs.incrementAndGet();
        
        logger.debug("Security log recorded - Event type: {}, Severity: {}", eventType, severity);
    }

    /**
     * Record performance log
     * @param operation Operation name
     * @param duration Execution time (milliseconds)
     */
    public void recordPerformanceLog(String operation, long duration) {
        performanceLogCounter.increment();
        logCounts.get("PERFORMANCE").incrementAndGet();
        totalLogs.incrementAndGet();
        logProcessingTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        logger.debug("Performance log recorded - Operation: {}, Duration: {}ms", operation, duration);
    }

    /**
     * Record error log
     * @param errorType Error type
     * @param component Component name
     */
    public void recordErrorLog(String errorType, String component) {
        errorLogCounter.increment();
        logCounts.get("ERROR").incrementAndGet();
        totalLogs.incrementAndGet();
        totalErrors.incrementAndGet();
        
        // Record specific counter based on error type
        if (errorCounts.containsKey(errorType)) {
            errorCounts.get(errorType).incrementAndGet();
        }
        
        logger.debug("Error log recorded - Error type: {}, Component: {}", errorType, component);
    }

    /**
     * Record request log
     * @param method HTTP method
     * @param uri Request URI
     * @param statusCode Response status code
     */
    public void recordRequestLog(String method, String uri, int statusCode) {
        requestLogCounter.increment();
        logCounts.get("REQUEST").incrementAndGet();
        totalLogs.incrementAndGet();
        
        logger.debug("Request log recorded - Method: {}, URI: {}, Status code: {}", method, uri, statusCode);
    }

    /**
     * Record application log
     * @param level Log level
     * @param component Component name
     */
    public void recordApplicationLog(String level, String component) {
        logCounts.get("APPLICATION").incrementAndGet();
        totalLogs.incrementAndGet();
        
        logger.debug("Application log recorded - Level: {}, Component: {}", level, component);
    }

    /**
     * Get log statistics
     * @return Log statistics
     */
    public LogStats getLogStats() {
        Map<String, Long> logCountsMap = new HashMap<>();
        logCounts.forEach((key, value) -> logCountsMap.put(key, value.get()));

        Map<String, Long> errorCountsMap = new HashMap<>();
        errorCounts.forEach((key, value) -> errorCountsMap.put(key, value.get()));

        return new LogStats(
                totalLogs.get(),
                totalErrors.get(),
                logCountsMap,
                errorCountsMap,
                logProcessingTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)
        );
    }

    /**
     * Reset log statistics
     */
    public void resetLogStats() {
        logCounts.values().forEach(atomicLong -> atomicLong.set(0));
        errorCounts.values().forEach(atomicLong -> atomicLong.set(0));
        totalLogs.set(0);
        totalErrors.set(0);
        
        logger.info("Log statistics have been reset");
    }

    /**
     * Get log health status
     * @return Log health status
     */
    public LogHealthStatus getLogHealthStatus() {
        long totalLogsCount = totalLogs.get();
        long totalErrorsCount = totalErrors.get();
        
        double errorRate = totalLogsCount > 0 ? (double) totalErrorsCount / totalLogsCount : 0.0;
        
        String status;
        if (errorRate < 0.01) { // Error rate less than 1%
            status = "HEALTHY";
        } else if (errorRate < 0.05) { // Error rate less than 5%
            status = "WARNING";
        } else {
            status = "CRITICAL";
        }
        
        return new LogHealthStatus(status, errorRate, totalLogsCount, totalErrorsCount);
    }

    /**
     * Log statistics class
     */
    public static class LogStats {
        private final long totalLogs;
        private final long totalErrors;
        private final Map<String, Long> logCounts;
        private final Map<String, Long> errorCounts;
        private final double averageProcessingTimeMs;

        public LogStats(long totalLogs, long totalErrors, Map<String, Long> logCounts, 
                       Map<String, Long> errorCounts, double averageProcessingTimeMs) {
            this.totalLogs = totalLogs;
            this.totalErrors = totalErrors;
            this.logCounts = logCounts;
            this.errorCounts = errorCounts;
            this.averageProcessingTimeMs = averageProcessingTimeMs;
        }

        public long getTotalLogs() { return totalLogs; }
        public long getTotalErrors() { return totalErrors; }
        public Map<String, Long> getLogCounts() { return logCounts; }
        public Map<String, Long> getErrorCounts() { return errorCounts; }
        public double getAverageProcessingTimeMs() { return averageProcessingTimeMs; }
        public double getErrorRate() {
            return totalLogs > 0 ? (double) totalErrors / totalLogs : 0.0;
        }

        @Override
        public String toString() {
            return String.format("LogStats{totalLogs=%d, totalErrors=%d, errorRate=%.2f%%, avgProcessingTime=%.2fms}",
                    totalLogs, totalErrors, getErrorRate() * 100, averageProcessingTimeMs);
        }
    }

    /**
     * Log health status class
     */
    public static class LogHealthStatus {
        private final String status;
        private final double errorRate;
        private final long totalLogs;
        private final long totalErrors;
        private final String timestamp;

        public LogHealthStatus(String status, double errorRate, long totalLogs, long totalErrors) {
            this.status = status;
            this.errorRate = errorRate;
            this.totalLogs = totalLogs;
            this.totalErrors = totalErrors;
            this.timestamp = Instant.now().toString();
        }

        public String getStatus() { return status; }
        public double getErrorRate() { return errorRate; }
        public long getTotalLogs() { return totalLogs; }
        public long getTotalErrors() { return totalErrors; }
        public String getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("LogHealthStatus{status='%s', errorRate=%.2f%%, totalLogs=%d, totalErrors=%d, timestamp='%s'}",
                    status, errorRate * 100, totalLogs, totalErrors, timestamp);
        }
    }
}

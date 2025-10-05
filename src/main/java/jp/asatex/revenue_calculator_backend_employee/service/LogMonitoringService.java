package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 日志监控服务
 * 提供日志相关的监控和统计功能
 */
@Service
public class LogMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(LogMonitoringService.class);

    private final MeterRegistry meterRegistry;
    private final Counter auditLogCounter;
    private final Counter securityLogCounter;
    private final Counter performanceLogCounter;
    private final Counter errorLogCounter;
    private final Counter requestLogCounter;
    private final Timer logProcessingTimer;

    // 日志统计信息
    private final Map<String, AtomicLong> logCounts = new HashMap<>();
    private final Map<String, AtomicLong> errorCounts = new HashMap<>();
    private final AtomicLong totalLogs = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);

    @Autowired
    public LogMonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        this.auditLogCounter = Counter.builder("logs.audit")
                .description("审计日志数量")
                .register(meterRegistry);

        this.securityLogCounter = Counter.builder("logs.security")
                .description("安全日志数量")
                .register(meterRegistry);

        this.performanceLogCounter = Counter.builder("logs.performance")
                .description("性能日志数量")
                .register(meterRegistry);

        this.errorLogCounter = Counter.builder("logs.error")
                .description("错误日志数量")
                .register(meterRegistry);

        this.requestLogCounter = Counter.builder("logs.request")
                .description("请求日志数量")
                .register(meterRegistry);

        this.logProcessingTimer = Timer.builder("logs.processing.duration")
                .description("日志处理时间")
                .register(meterRegistry);

        // 初始化日志统计
        initializeLogStatistics();
    }

    /**
     * 初始化日志统计信息
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
     * 记录审计日志
     * @param operation 操作类型
     * @param resource 资源类型
     */
    public void recordAuditLog(String operation, String resource) {
        auditLogCounter.increment();
        logCounts.get("AUDIT").incrementAndGet();
        totalLogs.incrementAndGet();
        
        logger.debug("审计日志记录 - 操作: {}, 资源: {}", operation, resource);
    }

    /**
     * 记录安全日志
     * @param eventType 事件类型
     * @param severity 严重程度
     */
    public void recordSecurityLog(String eventType, String severity) {
        securityLogCounter.increment();
        logCounts.get("SECURITY").incrementAndGet();
        totalLogs.incrementAndGet();
        
        logger.debug("安全日志记录 - 事件类型: {}, 严重程度: {}", eventType, severity);
    }

    /**
     * 记录性能日志
     * @param operation 操作名称
     * @param duration 执行时间（毫秒）
     */
    public void recordPerformanceLog(String operation, long duration) {
        performanceLogCounter.increment();
        logCounts.get("PERFORMANCE").incrementAndGet();
        totalLogs.incrementAndGet();
        logProcessingTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        logger.debug("性能日志记录 - 操作: {}, 耗时: {}ms", operation, duration);
    }

    /**
     * 记录错误日志
     * @param errorType 错误类型
     * @param component 组件名称
     */
    public void recordErrorLog(String errorType, String component) {
        errorLogCounter.increment();
        logCounts.get("ERROR").incrementAndGet();
        totalLogs.incrementAndGet();
        totalErrors.incrementAndGet();
        
        // 根据错误类型记录特定计数器
        if (errorCounts.containsKey(errorType)) {
            errorCounts.get(errorType).incrementAndGet();
        }
        
        logger.debug("错误日志记录 - 错误类型: {}, 组件: {}", errorType, component);
    }

    /**
     * 记录请求日志
     * @param method HTTP方法
     * @param uri 请求URI
     * @param statusCode 响应状态码
     */
    public void recordRequestLog(String method, String uri, int statusCode) {
        requestLogCounter.increment();
        logCounts.get("REQUEST").incrementAndGet();
        totalLogs.incrementAndGet();
        
        logger.debug("请求日志记录 - 方法: {}, URI: {}, 状态码: {}", method, uri, statusCode);
    }

    /**
     * 记录应用日志
     * @param level 日志级别
     * @param component 组件名称
     */
    public void recordApplicationLog(String level, String component) {
        logCounts.get("APPLICATION").incrementAndGet();
        totalLogs.incrementAndGet();
        
        logger.debug("应用日志记录 - 级别: {}, 组件: {}", level, component);
    }

    /**
     * 获取日志统计信息
     * @return 日志统计信息
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
     * 重置日志统计信息
     */
    public void resetLogStats() {
        logCounts.values().forEach(atomicLong -> atomicLong.set(0));
        errorCounts.values().forEach(atomicLong -> atomicLong.set(0));
        totalLogs.set(0);
        totalErrors.set(0);
        
        logger.info("日志统计信息已重置");
    }

    /**
     * 获取日志健康状态
     * @return 日志健康状态
     */
    public LogHealthStatus getLogHealthStatus() {
        long totalLogsCount = totalLogs.get();
        long totalErrorsCount = totalErrors.get();
        
        double errorRate = totalLogsCount > 0 ? (double) totalErrorsCount / totalLogsCount : 0.0;
        
        String status;
        if (errorRate < 0.01) { // 错误率小于1%
            status = "HEALTHY";
        } else if (errorRate < 0.05) { // 错误率小于5%
            status = "WARNING";
        } else {
            status = "CRITICAL";
        }
        
        return new LogHealthStatus(status, errorRate, totalLogsCount, totalErrorsCount);
    }

    /**
     * 日志统计信息类
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
     * 日志健康状态类
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

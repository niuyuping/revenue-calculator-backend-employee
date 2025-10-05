package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * 事务监控服务
 * 提供事务相关的监控和日志功能
 */
@Service
public class TransactionMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionMonitoringService.class);

    private final Counter transactionStartCounter;
    private final Counter transactionCommitCounter;
    private final Counter transactionRollbackCounter;
    private final Counter transactionErrorCounter;
    private final Timer transactionDurationTimer;

    public TransactionMonitoringService(MeterRegistry meterRegistry) {
        this.transactionStartCounter = Counter.builder("transaction.start")
                .description("事务开始次数")
                .register(meterRegistry);

        this.transactionCommitCounter = Counter.builder("transaction.commit")
                .description("事务提交次数")
                .register(meterRegistry);

        this.transactionRollbackCounter = Counter.builder("transaction.rollback")
                .description("事务回滚次数")
                .register(meterRegistry);

        this.transactionErrorCounter = Counter.builder("transaction.error")
                .description("事务错误次数")
                .register(meterRegistry);

        this.transactionDurationTimer = Timer.builder("transaction.duration")
                .description("事务执行时间")
                .register(meterRegistry);
    }

    /**
     * 记录事务开始
     * @param operation 操作名称
     * @param details 详细信息
     * @return 事务开始时间
     */
    public Instant recordTransactionStart(String operation, String details) {
        Instant startTime = Instant.now();
        transactionStartCounter.increment();
        
        logger.info("事务开始 - 操作: {}, 详情: {}, 时间: {}", 
                operation, details, startTime);
        
        return startTime;
    }

    /**
     * 记录事务提交
     * @param operation 操作名称
     * @param startTime 开始时间
     * @param details 详细信息
     */
    public void recordTransactionCommit(String operation, Instant startTime, String details) {
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        
        transactionCommitCounter.increment();
        transactionDurationTimer.record(duration);
        
        logger.info("事务提交成功 - 操作: {}, 详情: {}, 耗时: {}ms", 
                operation, details, duration.toMillis());
    }

    /**
     * 记录事务回滚
     * @param operation 操作名称
     * @param startTime 开始时间
     * @param reason 回滚原因
     */
    public void recordTransactionRollback(String operation, Instant startTime, String reason) {
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        
        transactionRollbackCounter.increment();
        transactionDurationTimer.record(duration);
        
        logger.warn("事务回滚 - 操作: {}, 原因: {}, 耗时: {}ms", 
                operation, reason, duration.toMillis());
    }

    /**
     * 记录事务错误
     * @param operation 操作名称
     * @param startTime 开始时间
     * @param error 错误信息
     */
    public void recordTransactionError(String operation, Instant startTime, Throwable error) {
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        
        transactionErrorCounter.increment();
        transactionDurationTimer.record(duration);
        
        logger.error("事务错误 - 操作: {}, 错误: {}, 耗时: {}ms", 
                operation, error.getMessage(), duration.toMillis(), error);
    }

    /**
     * 包装事务操作，自动记录监控信息
     * @param operation 操作名称
     * @param details 详细信息
     * @param transactionOperation 事务操作
     * @return Mono<T>
     */
    public <T> Mono<T> monitorTransaction(String operation, String details, 
                                         Mono<T> transactionOperation) {
        Instant startTime = recordTransactionStart(operation, details);
        
        return transactionOperation
                .doOnSuccess(result -> recordTransactionCommit(operation, startTime, details))
                .doOnError(error -> recordTransactionError(operation, startTime, error));
    }

    /**
     * 获取事务统计信息
     * @return 事务统计信息
     */
    public TransactionStats getTransactionStats() {
        return new TransactionStats(
                (long) transactionStartCounter.count(),
                (long) transactionCommitCounter.count(),
                (long) transactionRollbackCounter.count(),
                (long) transactionErrorCounter.count(),
                transactionDurationTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)
        );
    }

    /**
     * 事务统计信息类
     */
    public static class TransactionStats {
        private final long totalStarts;
        private final long totalCommits;
        private final long totalRollbacks;
        private final long totalErrors;
        private final double averageDurationMs;

        public TransactionStats(long totalStarts, long totalCommits, long totalRollbacks, 
                              long totalErrors, double averageDurationMs) {
            this.totalStarts = totalStarts;
            this.totalCommits = totalCommits;
            this.totalRollbacks = totalRollbacks;
            this.totalErrors = totalErrors;
            this.averageDurationMs = averageDurationMs;
        }

        public long getTotalStarts() { return totalStarts; }
        public long getTotalCommits() { return totalCommits; }
        public long getTotalRollbacks() { return totalRollbacks; }
        public long getTotalErrors() { return totalErrors; }
        public double getAverageDurationMs() { return averageDurationMs; }

        @Override
        public String toString() {
            return String.format("TransactionStats{starts=%d, commits=%d, rollbacks=%d, errors=%d, avgDuration=%.2fms}",
                    totalStarts, totalCommits, totalRollbacks, totalErrors, averageDurationMs);
        }
    }
}

package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Transaction monitoring service
 * Provides transaction-related monitoring and logging functionality
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
                .description("Transaction start count")
                .register(meterRegistry);

        this.transactionCommitCounter = Counter.builder("transaction.commit")
                .description("Transaction commit count")
                .register(meterRegistry);

        this.transactionRollbackCounter = Counter.builder("transaction.rollback")
                .description("Transaction rollback count")
                .register(meterRegistry);

        this.transactionErrorCounter = Counter.builder("transaction.error")
                .description("Transaction error count")
                .register(meterRegistry);

        this.transactionDurationTimer = Timer.builder("transaction.duration")
                .description("Transaction execution time")
                .register(meterRegistry);
    }

    /**
     * Record transaction start
     * @param operation Operation name
     * @param details Details
     * @return Transaction start time
     */
    public Instant recordTransactionStart(String operation, String details) {
        Instant startTime = Instant.now();
        transactionStartCounter.increment();
        
        logger.info("Transaction started - Operation: {}, Details: {}, Time: {}", 
                operation, details, startTime);
        
        return startTime;
    }

    /**
     * Record transaction commit
     * @param operation Operation name
     * @param startTime Start time
     * @param details Details
     */
    public void recordTransactionCommit(String operation, Instant startTime, String details) {
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        
        transactionCommitCounter.increment();
        transactionDurationTimer.record(duration);
        
        logger.info("Transaction committed successfully - Operation: {}, Details: {}, Duration: {}ms", 
                operation, details, duration.toMillis());
    }

    /**
     * Record transaction rollback
     * @param operation Operation name
     * @param startTime Start time
     * @param reason Rollback reason
     */
    public void recordTransactionRollback(String operation, Instant startTime, String reason) {
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        
        transactionRollbackCounter.increment();
        transactionDurationTimer.record(duration);
        
        logger.warn("Transaction rolled back - Operation: {}, Reason: {}, Duration: {}ms", 
                operation, reason, duration.toMillis());
    }

    /**
     * Record transaction error
     * @param operation Operation name
     * @param startTime Start time
     * @param error Error information
     */
    public void recordTransactionError(String operation, Instant startTime, Throwable error) {
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        
        transactionErrorCounter.increment();
        transactionDurationTimer.record(duration);
        
        logger.error("Transaction error - Operation: {}, Error: {}, Duration: {}ms", 
                operation, error.getMessage(), duration.toMillis(), error);
    }

    /**
     * Wrap transaction operation with automatic monitoring
     * @param operation Operation name
     * @param details Details
     * @param transactionOperation Transaction operation
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
     * Get transaction statistics
     * @return Transaction statistics information
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
     * Transaction statistics information class
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

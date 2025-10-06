package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TransactionMonitoringService test class
 */
@DisplayName("TransactionMonitoringService Test")
class TransactionMonitoringServiceTest {

    private TransactionMonitoringService transactionMonitoringService;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        transactionMonitoringService = new TransactionMonitoringService(meterRegistry);
    }

    @Test
    @DisplayName("Recording transaction start should work correctly")
    void testRecordTransactionStart() {
        // Record transaction start
        Instant startTime = transactionMonitoringService.recordTransactionStart("TEST_OPERATION", "Test Operation");
        
        assertThat(startTime).isNotNull();
        
        // Verify counter increment
        Counter counter = meterRegistry.find("transaction.start").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Recording transaction commit should work correctly")
    void testRecordTransactionCommit() {
        Instant startTime = Instant.now().minus(Duration.ofMillis(100));
        
        // Record transaction commit
        transactionMonitoringService.recordTransactionCommit("TEST_OPERATION", startTime, "Test Operation");
        
        // Verify counter increment
        Counter counter = meterRegistry.find("transaction.commit").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
        
        // Verify timer recording
        Timer timer = meterRegistry.find("transaction.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Recording transaction rollback should work correctly")
    void testRecordTransactionRollback() {
        Instant startTime = Instant.now().minus(Duration.ofMillis(50));
        
        // Record transaction rollback
        transactionMonitoringService.recordTransactionRollback("TEST_OPERATION", startTime, "Test Rollback");
        
        // Verify counter increment
        Counter counter = meterRegistry.find("transaction.rollback").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
        
        // Verify timer recording
        Timer timer = meterRegistry.find("transaction.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Recording transaction errors should work correctly")
    void testRecordTransactionError() {
        Instant startTime = Instant.now().minus(Duration.ofMillis(75));
        RuntimeException error = new RuntimeException("Test error");
        
        // Record transaction error
        transactionMonitoringService.recordTransactionError("TEST_OPERATION", startTime, error);
        
        // Verify counter increment
        Counter counter = meterRegistry.find("transaction.error").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
        
        // Verify timer recording
        Timer timer = meterRegistry.find("transaction.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Monitoring transaction operations should work correctly")
    void testMonitorTransaction() {
        // Test successful transaction
        StepVerifier.create(transactionMonitoringService.monitorTransaction(
                "TEST_OPERATION", 
                "Test Operation", 
                Mono.just("Success Result")
        ))
                .expectNext("Success Result")
                .verifyComplete();
        
        // Verify counter
        Counter startCounter = meterRegistry.find("transaction.start").counter();
        Counter commitCounter = meterRegistry.find("transaction.commit").counter();
        
        assertThat(startCounter.count()).isEqualTo(1.0);
        assertThat(commitCounter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Monitoring transaction operation errors should work correctly")
    void testMonitorTransactionError() {
        RuntimeException error = new RuntimeException("Test error");
        
        // Test failed transaction
        StepVerifier.create(transactionMonitoringService.monitorTransaction(
                "TEST_OPERATION", 
                "Test Operation", 
                Mono.error(error)
        ))
                .expectError(RuntimeException.class)
                .verify();
        
        // Verify counter
        Counter startCounter = meterRegistry.find("transaction.start").counter();
        Counter errorCounter = meterRegistry.find("transaction.error").counter();
        
        assertThat(startCounter.count()).isEqualTo(1.0);
        assertThat(errorCounter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Getting transaction statistics should work correctly")
    void testGetTransactionStats() {
        // Execute some operations
        transactionMonitoringService.recordTransactionStart("OP1", "Operation 1");
        transactionMonitoringService.recordTransactionStart("OP2", "Operation 2");
        transactionMonitoringService.recordTransactionCommit("OP1", Instant.now(), "Operation 1");
        transactionMonitoringService.recordTransactionRollback("OP2", Instant.now(), "Operation 2");
        
        // Get statistics
        TransactionMonitoringService.TransactionStats stats = transactionMonitoringService.getTransactionStats();
        
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalStarts()).isEqualTo(2);
        assertThat(stats.getTotalCommits()).isEqualTo(1);
        assertThat(stats.getTotalRollbacks()).isEqualTo(1);
        assertThat(stats.getTotalErrors()).isEqualTo(0);
    }

    @Test
    @DisplayName("Transaction statistics toString should format correctly")
    void testTransactionStatsToString() {
        TransactionMonitoringService.TransactionStats stats = new TransactionMonitoringService.TransactionStats(
                10, 8, 1, 1, 150.5
        );
        
        String statsString = stats.toString();
        assertThat(statsString).contains("starts=10");
        assertThat(statsString).contains("commits=8");
        assertThat(statsString).contains("rollbacks=1");
        assertThat(statsString).contains("errors=1");
        assertThat(statsString).contains("avgDuration=150.50ms");
    }
}

package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * SystemMonitoringService test class
 */
@DisplayName("SystemMonitoringService Test")
class SystemMonitoringServiceTest {

    private SystemMonitoringService systemMonitoringService;
    private MeterRegistry meterRegistry;
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        databaseClient = mock(DatabaseClient.class);
        systemMonitoringService = new SystemMonitoringService(databaseClient, meterRegistry);
    }

    // ==================== Transaction Monitoring Tests ====================

    @Test
    @DisplayName("Recording transaction start should work correctly")
    void testRecordTransactionStart() {
        // Record transaction start
        Instant startTime = systemMonitoringService.recordTransactionStart("TEST_OPERATION", "Test Operation");
        
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
        systemMonitoringService.recordTransactionCommit("TEST_OPERATION", startTime, "Test Operation");
        
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
        systemMonitoringService.recordTransactionRollback("TEST_OPERATION", startTime, "Test Rollback");
        
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
        systemMonitoringService.recordTransactionError("TEST_OPERATION", startTime, error);
        
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
        StepVerifier.create(systemMonitoringService.monitorTransaction(
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
        StepVerifier.create(systemMonitoringService.monitorTransaction(
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
        systemMonitoringService.recordTransactionStart("OP1", "Operation 1");
        systemMonitoringService.recordTransactionStart("OP2", "Operation 2");
        systemMonitoringService.recordTransactionCommit("OP1", Instant.now(), "Operation 1");
        systemMonitoringService.recordTransactionRollback("OP2", Instant.now(), "Operation 2");
        
        // Get statistics
        SystemMonitoringService.TransactionStats stats = systemMonitoringService.getTransactionStats();
        
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalStarts()).isEqualTo(2);
        assertThat(stats.getTotalCommits()).isEqualTo(1);
        assertThat(stats.getTotalRollbacks()).isEqualTo(1);
        assertThat(stats.getTotalErrors()).isEqualTo(0);
    }

    @Test
    @DisplayName("Transaction statistics toString should format correctly")
    void testTransactionStatsToString() {
        SystemMonitoringService.TransactionStats stats = new SystemMonitoringService.TransactionStats(
                10, 8, 1, 1, 150.5
        );
        
        String statsString = stats.toString();
        assertThat(statsString).contains("starts=10");
        assertThat(statsString).contains("commits=8");
        assertThat(statsString).contains("rollbacks=1");
        assertThat(statsString).contains("errors=1");
        assertThat(statsString).contains("avgDuration=150.50ms");
    }

    // ==================== Database Monitoring Tests ====================
    // Note: Database monitoring tests are complex due to R2DBC mocking requirements
    // These tests are covered in integration tests with real database connections

    // ==================== Data Classes Tests ====================

    @Test
    @DisplayName("DatabaseStats should have correct getters")
    void testDatabaseStatsGetters() {
        SystemMonitoringService.DatabaseHealth health = new SystemMonitoringService.DatabaseHealth(
                "UP", "v1.0", "test_db", "user", Instant.now(), "OK"
        );
        SystemMonitoringService.TableStats tableStats = new SystemMonitoringService.TableStats(
                java.util.Map.of(), 0, 0, 0, 0
        );
        SystemMonitoringService.ConnectionStats connectionStats = new SystemMonitoringService.ConnectionStats(
                10, 5, 5, 0, 100.0
        );
        
        SystemMonitoringService.DatabaseStats stats = new SystemMonitoringService.DatabaseStats(
                health, tableStats, connectionStats
        );
        
        assertThat(stats.getHealth()).isEqualTo(health);
        assertThat(stats.getTableStats()).isEqualTo(tableStats);
        assertThat(stats.getConnectionStats()).isEqualTo(connectionStats);
    }

    @Test
    @DisplayName("ConnectionStats should have correct getters")
    void testConnectionStatsGetters() {
        SystemMonitoringService.ConnectionStats stats = new SystemMonitoringService.ConnectionStats(
                100, 50, 30, 5, 200.5
        );
        
        assertThat(stats.getTotalConnections()).isEqualTo(100);
        assertThat(stats.getActiveConnections()).isEqualTo(50);
        assertThat(stats.getIdleConnections()).isEqualTo(30);
        assertThat(stats.getConnectionErrors()).isEqualTo(5);
        assertThat(stats.getAverageConnectionTime()).isEqualTo(200.5);
    }

    @Test
    @DisplayName("DatabaseHealth should have correct getters")
    void testDatabaseHealthGetters() {
        Instant now = Instant.now();
        SystemMonitoringService.DatabaseHealth health = new SystemMonitoringService.DatabaseHealth(
                "UP", "v1.0", "test_db", "user", now, "OK"
        );
        
        assertThat(health.getStatus()).isEqualTo("UP");
        assertThat(health.getVersion()).isEqualTo("v1.0");
        assertThat(health.getDatabase()).isEqualTo("test_db");
        assertThat(health.getUser()).isEqualTo("user");
        assertThat(health.getLastChecked()).isEqualTo(now);
        assertThat(health.getMessage()).isEqualTo("OK");
    }

    @Test
    @DisplayName("TableStats should have correct getters")
    void testTableStatsGetters() {
        java.util.Map<String, SystemMonitoringService.TableInfo> tables = java.util.Map.of(
                "employees", new SystemMonitoringService.TableInfo("employees", 100, 50, 20, 5)
        );
        
        SystemMonitoringService.TableStats stats = new SystemMonitoringService.TableStats(
                tables, 100, 50, 20, 5
        );
        
        assertThat(stats.getTables()).isEqualTo(tables);
        assertThat(stats.getTotalRows()).isEqualTo(100);
        assertThat(stats.getTotalInserts()).isEqualTo(50);
        assertThat(stats.getTotalUpdates()).isEqualTo(20);
        assertThat(stats.getTotalDeletes()).isEqualTo(5);
    }

    @Test
    @DisplayName("TableInfo should have correct getters")
    void testTableInfoGetters() {
        SystemMonitoringService.TableInfo info = new SystemMonitoringService.TableInfo(
                "employees", 100, 50, 20, 5
        );
        
        assertThat(info.getTableName()).isEqualTo("employees");
        assertThat(info.getRowCount()).isEqualTo(100);
        assertThat(info.getInserts()).isEqualTo(50);
        assertThat(info.getUpdates()).isEqualTo(20);
        assertThat(info.getDeletes()).isEqualTo(5);
    }
}

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
 * TransactionMonitoringService 测试类
 */
@DisplayName("TransactionMonitoringService テスト")
class TransactionMonitoringServiceTest {

    private TransactionMonitoringService transactionMonitoringService;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        transactionMonitoringService = new TransactionMonitoringService(meterRegistry);
    }

    @Test
    @DisplayName("记录事务开始应该正确工作")
    void testRecordTransactionStart() {
        // 记录事务开始
        Instant startTime = transactionMonitoringService.recordTransactionStart("TEST_OPERATION", "测试操作");
        
        assertThat(startTime).isNotNull();
        
        // 验证计数器增加
        Counter counter = meterRegistry.find("transaction.start").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("记录事务提交应该正确工作")
    void testRecordTransactionCommit() {
        Instant startTime = Instant.now().minus(Duration.ofMillis(100));
        
        // 记录事务提交
        transactionMonitoringService.recordTransactionCommit("TEST_OPERATION", startTime, "测试操作");
        
        // 验证计数器增加
        Counter counter = meterRegistry.find("transaction.commit").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
        
        // 验证计时器记录
        Timer timer = meterRegistry.find("transaction.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("记录事务回滚应该正确工作")
    void testRecordTransactionRollback() {
        Instant startTime = Instant.now().minus(Duration.ofMillis(50));
        
        // 记录事务回滚
        transactionMonitoringService.recordTransactionRollback("TEST_OPERATION", startTime, "测试回滚");
        
        // 验证计数器增加
        Counter counter = meterRegistry.find("transaction.rollback").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
        
        // 验证计时器记录
        Timer timer = meterRegistry.find("transaction.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("记录事务错误应该正确工作")
    void testRecordTransactionError() {
        Instant startTime = Instant.now().minus(Duration.ofMillis(75));
        RuntimeException error = new RuntimeException("测试错误");
        
        // 记录事务错误
        transactionMonitoringService.recordTransactionError("TEST_OPERATION", startTime, error);
        
        // 验证计数器增加
        Counter counter = meterRegistry.find("transaction.error").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
        
        // 验证计时器记录
        Timer timer = meterRegistry.find("transaction.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("监控事务操作应该正确工作")
    void testMonitorTransaction() {
        // 测试成功的事务
        StepVerifier.create(transactionMonitoringService.monitorTransaction(
                "TEST_OPERATION", 
                "测试操作", 
                Mono.just("成功结果")
        ))
                .expectNext("成功结果")
                .verifyComplete();
        
        // 验证计数器
        Counter startCounter = meterRegistry.find("transaction.start").counter();
        Counter commitCounter = meterRegistry.find("transaction.commit").counter();
        
        assertThat(startCounter.count()).isEqualTo(1.0);
        assertThat(commitCounter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("监控事务操作错误应该正确工作")
    void testMonitorTransactionError() {
        RuntimeException error = new RuntimeException("测试错误");
        
        // 测试失败的事务
        StepVerifier.create(transactionMonitoringService.monitorTransaction(
                "TEST_OPERATION", 
                "测试操作", 
                Mono.error(error)
        ))
                .expectError(RuntimeException.class)
                .verify();
        
        // 验证计数器
        Counter startCounter = meterRegistry.find("transaction.start").counter();
        Counter errorCounter = meterRegistry.find("transaction.error").counter();
        
        assertThat(startCounter.count()).isEqualTo(1.0);
        assertThat(errorCounter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("获取事务统计信息应该正确工作")
    void testGetTransactionStats() {
        // 执行一些操作
        transactionMonitoringService.recordTransactionStart("OP1", "操作1");
        transactionMonitoringService.recordTransactionStart("OP2", "操作2");
        transactionMonitoringService.recordTransactionCommit("OP1", Instant.now(), "操作1");
        transactionMonitoringService.recordTransactionRollback("OP2", Instant.now(), "操作2");
        
        // 获取统计信息
        TransactionMonitoringService.TransactionStats stats = transactionMonitoringService.getTransactionStats();
        
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalStarts()).isEqualTo(2);
        assertThat(stats.getTotalCommits()).isEqualTo(1);
        assertThat(stats.getTotalRollbacks()).isEqualTo(1);
        assertThat(stats.getTotalErrors()).isEqualTo(0);
    }

    @Test
    @DisplayName("事务统计信息toString应该正确格式化")
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

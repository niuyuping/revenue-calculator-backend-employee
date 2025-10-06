package jp.asatex.revenue_calculator_backend_employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.asatex.revenue_calculator_backend_employee.entity.DatabaseAuditLog;
import jp.asatex.revenue_calculator_backend_employee.repository.DatabaseAuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DatabaseAuditService test class
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DatabaseAuditService Test")
class DatabaseAuditServiceTest {

    @Mock
    private DatabaseAuditLogRepository databaseAuditLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DatabaseAuditService databaseAuditService;

    @BeforeEach
    void setUp() {
        // Set MDC
        MDC.put("userId", "test-user");
        MDC.put("sessionId", "test-session");
        MDC.put("requestId", "test-request");
        MDC.put("ipAddress", "192.168.1.100");
        MDC.put("userAgent", "Test-Agent/1.0");
    }

    @Test
    @DisplayName("Logging successful database operations should work correctly")
    void testLogSuccessfulOperation() {
        // Given
        String operationType = "INSERT";
        String tableName = "employees";
        String recordId = "123";
        Map<String, Object> oldValues = new HashMap<>();
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("name", "Test Employee");
        String sqlStatement = "INSERT INTO employees (name) VALUES (?)";
        Long executionTimeMs = 150L;
        Integer affectedRows = 1;

        DatabaseAuditLog savedLog = new DatabaseAuditLog();
        savedLog.setId(1L);
        savedLog.setOperationType(operationType);
        savedLog.setTableName(tableName);

        when(databaseAuditLogRepository.save(any(DatabaseAuditLog.class)))
                .thenReturn(Mono.just(savedLog));

        // When
        Mono<Void> result = databaseAuditService.logSuccessfulOperation(
                operationType, tableName, recordId, oldValues, newValues,
                sqlStatement, executionTimeMs, affectedRows);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).save(any(DatabaseAuditLog.class));
    }

    @Test
    @DisplayName("Logging failed database operations should work correctly")
    void testLogFailedOperation() {
        // Given
        String operationType = "UPDATE";
        String tableName = "employees";
        String recordId = "123";
        String sqlStatement = "UPDATE employees SET name = ? WHERE id = ?";
        Long executionTimeMs = 100L;
        String errorMessage = "Constraint violation";

        DatabaseAuditLog savedLog = new DatabaseAuditLog();
        savedLog.setId(1L);
        savedLog.setOperationType(operationType);
        savedLog.setTableName(tableName);
        savedLog.setOperationStatus("FAILURE");

        when(databaseAuditLogRepository.save(any(DatabaseAuditLog.class)))
                .thenReturn(Mono.just(savedLog));

        // When
        Mono<Void> result = databaseAuditService.logFailedOperation(
                operationType, tableName, recordId, sqlStatement, executionTimeMs, errorMessage);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).save(any(DatabaseAuditLog.class));
    }

    @Test
    @DisplayName("Logging INSERT operations should work correctly")
    void testLogInsertOperation() {
        // Given
        String tableName = "employees";
        String recordId = "123";
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("name", "New Employee");
        String sqlStatement = "INSERT INTO employees (name) VALUES (?)";
        Long executionTimeMs = 120L;
        Integer affectedRows = 1;

        DatabaseAuditLog savedLog = new DatabaseAuditLog();
        savedLog.setId(1L);
        savedLog.setOperationType("INSERT");

        when(databaseAuditLogRepository.save(any(DatabaseAuditLog.class)))
                .thenReturn(Mono.just(savedLog));

        // When
        Mono<Void> result = databaseAuditService.logInsertOperation(
                tableName, recordId, newValues, sqlStatement, executionTimeMs, affectedRows);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).save(any(DatabaseAuditLog.class));
    }

    @Test
    @DisplayName("Recording UPDATE operations should work correctly")
    void testLogUpdateOperation() {
        // Given
        String tableName = "employees";
        String recordId = "123";
        Map<String, Object> oldValues = new HashMap<>();
        oldValues.put("name", "Old Name");
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("name", "New Name");
        String sqlStatement = "UPDATE employees SET name = ? WHERE id = ?";
        Long executionTimeMs = 80L;
        Integer affectedRows = 1;

        DatabaseAuditLog savedLog = new DatabaseAuditLog();
        savedLog.setId(1L);
        savedLog.setOperationType("UPDATE");

        when(databaseAuditLogRepository.save(any(DatabaseAuditLog.class)))
                .thenReturn(Mono.just(savedLog));

        // When
        Mono<Void> result = databaseAuditService.logUpdateOperation(
                tableName, recordId, oldValues, newValues, sqlStatement, executionTimeMs, affectedRows);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).save(any(DatabaseAuditLog.class));
    }

    @Test
    @DisplayName("Recording DELETE operations should work correctly")
    void testLogDeleteOperation() {
        // Given
        String tableName = "employees";
        String recordId = "123";
        Map<String, Object> oldValues = new HashMap<>();
        oldValues.put("name", "Deleted Employee");
        String sqlStatement = "DELETE FROM employees WHERE id = ?";
        Long executionTimeMs = 60L;
        Integer affectedRows = 1;

        DatabaseAuditLog savedLog = new DatabaseAuditLog();
        savedLog.setId(1L);
        savedLog.setOperationType("DELETE");

        when(databaseAuditLogRepository.save(any(DatabaseAuditLog.class)))
                .thenReturn(Mono.just(savedLog));

        // When
        Mono<Void> result = databaseAuditService.logDeleteOperation(
                tableName, recordId, oldValues, sqlStatement, executionTimeMs, affectedRows);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).save(any(DatabaseAuditLog.class));
    }

    @Test
    @DisplayName("Recording SELECT operations should work correctly")
    void testLogSelectOperation() {
        // Given
        String tableName = "employees";
        String recordId = "123";
        String sqlStatement = "SELECT * FROM employees WHERE id = ?";
        Long executionTimeMs = 30L;
        Integer affectedRows = 1;

        DatabaseAuditLog savedLog = new DatabaseAuditLog();
        savedLog.setId(1L);
        savedLog.setOperationType("SELECT");

        when(databaseAuditLogRepository.save(any(DatabaseAuditLog.class)))
                .thenReturn(Mono.just(savedLog));

        // When
        Mono<Void> result = databaseAuditService.logSelectOperation(
                tableName, recordId, sqlStatement, executionTimeMs, affectedRows);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).save(any(DatabaseAuditLog.class));
    }

    @Test
    @DisplayName("Getting audit statistics should work correctly")
    void testGetAuditStatistics() {
        // Return different values for different time ranges
        org.mockito.Mockito.lenient().when(databaseAuditLogRepository.countByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Mono.just(10L))  // 24 hours
                .thenReturn(Mono.just(50L))  // 7 days
                .thenReturn(Mono.just(200L)); // 30 days
        org.mockito.Mockito.lenient().when(databaseAuditLogRepository.countByOperationType("INSERT")).thenReturn(Mono.just(50L));
        org.mockito.Mockito.lenient().when(databaseAuditLogRepository.countByOperationType("UPDATE")).thenReturn(Mono.just(30L));
        org.mockito.Mockito.lenient().when(databaseAuditLogRepository.countByOperationType("DELETE")).thenReturn(Mono.just(10L));
        org.mockito.Mockito.lenient().when(databaseAuditLogRepository.countByOperationType("SELECT")).thenReturn(Mono.just(100L));
        org.mockito.Mockito.lenient().when(databaseAuditLogRepository.countByOperationStatus("SUCCESS")).thenReturn(Mono.just(180L));
        org.mockito.Mockito.lenient().when(databaseAuditLogRepository.countByOperationStatus("FAILURE")).thenReturn(Mono.just(10L));
        org.mockito.Mockito.lenient().when(databaseAuditLogRepository.countByOperationStatus("ROLLBACK")).thenReturn(Mono.just(0L));

        // When
        Mono<Map<String, Object>> result = databaseAuditService.getAuditStatistics();

        // Then
        StepVerifier.create(result)
                .assertNext(stats -> {
                    assertThat(stats.get("last24Hours")).isEqualTo(10L);
                    assertThat(stats.get("last7Days")).isEqualTo(50L);
                    assertThat(stats.get("last30Days")).isEqualTo(200L);
                    assertThat(stats.get("totalInserts")).isEqualTo(50L);
                    assertThat(stats.get("totalUpdates")).isEqualTo(30L);
                    assertThat(stats.get("totalDeletes")).isEqualTo(10L);
                    assertThat(stats.get("totalSelects")).isEqualTo(100L);
                    assertThat(stats.get("totalSuccess")).isEqualTo(180L);
                    assertThat(stats.get("totalFailures")).isEqualTo(10L);
                    assertThat(stats.get("totalRollbacks")).isEqualTo(0L);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Cleaning up expired audit logs should work correctly")
    void testCleanupOldAuditLogs() {
        // Given
        int retentionDays = 90;
        int deletedCount = 25;

        when(databaseAuditLogRepository.deleteByCreatedAtBefore(any(LocalDateTime.class)))
                .thenReturn(Mono.just(deletedCount));

        // When
        Mono<Long> result = databaseAuditService.cleanupOldAuditLogs(retentionDays);

        // Then
        StepVerifier.create(result)
                .assertNext(count -> {
                    assertThat(count).isEqualTo((long) deletedCount);
                })
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).deleteByCreatedAtBefore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Querying audit logs by operation type should work correctly")
    void testFindByOperationType() {
        // Given
        String operationType = "INSERT";
        DatabaseAuditLog log1 = new DatabaseAuditLog();
        log1.setId(1L);
        log1.setOperationType(operationType);
        DatabaseAuditLog log2 = new DatabaseAuditLog();
        log2.setId(2L);
        log2.setOperationType(operationType);

        when(databaseAuditLogRepository.findByOperationType(operationType))
                .thenReturn(Flux.just(log1, log2));

        // When
        Flux<DatabaseAuditLog> result = databaseAuditService.findByOperationType(operationType);

        // Then
        StepVerifier.create(result)
                .assertNext(log -> {
                    assertThat(log.getOperationType()).isEqualTo(operationType);
                })
                .assertNext(log -> {
                    assertThat(log.getOperationType()).isEqualTo(operationType);
                })
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).findByOperationType(operationType);
    }

    @Test
    @DisplayName("Querying audit logs by table name should work correctly")
    void testFindByTableName() {
        // Given
        String tableName = "employees";
        DatabaseAuditLog log = new DatabaseAuditLog();
        log.setId(1L);
        log.setTableName(tableName);

        when(databaseAuditLogRepository.findByTableName(tableName))
                .thenReturn(Flux.just(log));

        // When
        Flux<DatabaseAuditLog> result = databaseAuditService.findByTableName(tableName);

        // Then
        StepVerifier.create(result)
                .assertNext(auditLog -> {
                    assertThat(auditLog.getTableName()).isEqualTo(tableName);
                })
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).findByTableName(tableName);
    }

    @Test
    @DisplayName("Querying audit logs by user ID should work correctly")
    void testFindByUserId() {
        // Given
        String userId = "user123";
        DatabaseAuditLog log = new DatabaseAuditLog();
        log.setId(1L);
        log.setUserId(userId);

        when(databaseAuditLogRepository.findByUserId(userId))
                .thenReturn(Flux.just(log));

        // When
        Flux<DatabaseAuditLog> result = databaseAuditService.findByUserId(userId);

        // Then
        StepVerifier.create(result)
                .assertNext(auditLog -> {
                    assertThat(auditLog.getUserId()).isEqualTo(userId);
                })
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Querying audit logs by time range should work correctly")
    void testFindByCreatedAtBetween() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        DatabaseAuditLog log = new DatabaseAuditLog();
        log.setId(1L);
        log.setCreatedAt(startTime.plusHours(1));

        when(databaseAuditLogRepository.findByCreatedAtBetween(startTime, endTime))
                .thenReturn(Flux.just(log));

        // When
        Flux<DatabaseAuditLog> result = databaseAuditService.findByCreatedAtBetween(startTime, endTime);

        // Then
        StepVerifier.create(result)
                .assertNext(auditLog -> {
                    assertThat(auditLog.getId()).isEqualTo(1L);
                })
                .verifyComplete();

        verify(databaseAuditLogRepository, times(1)).findByCreatedAtBetween(startTime, endTime);
    }
}

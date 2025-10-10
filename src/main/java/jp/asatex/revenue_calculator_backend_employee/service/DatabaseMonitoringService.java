package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Database monitoring service
 * Provides database-related monitoring and statistics functionality
 */
@Service
public class DatabaseMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMonitoringService.class);

    private final DatabaseClient databaseClient;

    // Database operation counters
    private final Counter queryCounter;
    private final Counter insertCounter;
    private final Counter updateCounter;
    private final Counter deleteCounter;
    private final Counter errorCounter;
    private final Timer queryDurationTimer;
    private final Timer connectionTimeTimer;

    // Connection pool statistics
    private final AtomicLong totalConnections = new AtomicLong(0);
    private final AtomicLong activeConnections = new AtomicLong(0);
    private final AtomicLong idleConnections = new AtomicLong(0);
    private final AtomicLong connectionErrors = new AtomicLong(0);

    public DatabaseMonitoringService(DatabaseClient databaseClient, MeterRegistry meterRegistry) {
        this.databaseClient = databaseClient;

        // Initialize counters
        this.queryCounter = Counter.builder("database.query.total")
                .description("Total database queries executed")
                .register(meterRegistry);

        this.insertCounter = Counter.builder("database.insert.total")
                .description("Total database insert operations")
                .register(meterRegistry);

        this.updateCounter = Counter.builder("database.update.total")
                .description("Total database update operations")
                .register(meterRegistry);

        this.deleteCounter = Counter.builder("database.delete.total")
                .description("Total database delete operations")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("database.error.total")
                .description("Total database errors")
                .register(meterRegistry);

        this.queryDurationTimer = Timer.builder("database.query.duration")
                .description("Database query execution time")
                .register(meterRegistry);

        this.connectionTimeTimer = Timer.builder("database.connection.time")
                .description("Database connection establishment time")
                .register(meterRegistry);
    }

    /**
     * Record database query execution
     * @param operation Operation type (SELECT, INSERT, UPDATE, DELETE)
     * @param query Query string
     * @param duration Execution duration
     */
    public void recordQuery(String operation, String query, Duration duration) {
        queryCounter.increment();
        queryDurationTimer.record(duration);

        switch (operation.toUpperCase()) {
            case "INSERT":
                insertCounter.increment();
                break;
            case "UPDATE":
                updateCounter.increment();
                break;
            case "DELETE":
                deleteCounter.increment();
                break;
        }

        logger.debug("Database query executed - Operation: {}, Duration: {}ms, Query: {}", 
                operation, duration.toMillis(), query);
    }

    /**
     * Record database error
     * @param operation Operation type
     * @param error Error information
     */
    public void recordError(String operation, Throwable error) {
        errorCounter.increment();
        logger.error("Database error - Operation: {}, Error: {}", operation, error.getMessage(), error);
    }

    /**
     * Record connection establishment time
     * @param duration Connection duration
     */
    public void recordConnectionTime(Duration duration) {
        connectionTimeTimer.record(duration);
        totalConnections.incrementAndGet();
    }

    /**
     * Record active connection
     */
    public void recordActiveConnection() {
        activeConnections.incrementAndGet();
    }

    /**
     * Record idle connection
     */
    public void recordIdleConnection() {
        idleConnections.incrementAndGet();
    }

    /**
     * Record connection error
     */
    public void recordConnectionError() {
        connectionErrors.incrementAndGet();
    }

    /**
     * Get database connection statistics
     * @return Connection statistics
     */
    public Mono<ConnectionStats> getConnectionStats() {
        return Mono.fromCallable(() -> {
            // Get connection pool information from database
            return databaseClient.sql("SELECT 1")
                    .fetch()
                    .first()
                    .map(result -> new ConnectionStats(
                            totalConnections.get(),
                            activeConnections.get(),
                            idleConnections.get(),
                            connectionErrors.get(),
                            connectionTimeTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)
                    ))
                    .onErrorReturn(new ConnectionStats(
                            totalConnections.get(),
                            activeConnections.get(),
                            idleConnections.get(),
                            connectionErrors.get(),
                            connectionTimeTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)
                    ))
                    .block();
        });
    }

    /**
     * Get database performance statistics
     * @return Performance statistics
     */
    public Mono<PerformanceStats> getPerformanceStats() {
        return Mono.fromCallable(() -> {
            return new PerformanceStats(
                    (long) queryCounter.count(),
                    (long) insertCounter.count(),
                    (long) updateCounter.count(),
                    (long) deleteCounter.count(),
                    (long) errorCounter.count(),
                    queryDurationTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS),
                    queryDurationTimer.max(java.util.concurrent.TimeUnit.MILLISECONDS),
                    calculateErrorRate()
            );
        });
    }

    /**
     * Get database health information
     * @return Database health information
     */
    public Mono<DatabaseHealth> getDatabaseHealth() {
        return databaseClient.sql("SELECT version() as version, current_database() as database, current_user as user")
                .fetch()
                .first()
                .map(result -> new DatabaseHealth(
                        "UP",
                        (String) result.get("version"),
                        (String) result.get("database"),
                        (String) result.get("user"),
                        Instant.now(),
                        "Connected successfully"
                ))
                .onErrorReturn(new DatabaseHealth(
                        "DOWN",
                        "Unknown",
                        "Unknown",
                        "Unknown",
                        Instant.now(),
                        "Connection failed"
                ));
    }

    /**
     * Get database table statistics
     * @return Table statistics
     */
    public Mono<TableStats> getTableStats() {
        return databaseClient.sql("""
                SELECT 
                    schemaname,
                    tablename,
                    n_tup_ins as inserts,
                    n_tup_upd as updates,
                    n_tup_del as deletes,
                    n_live_tup as live_tuples,
                    n_dead_tup as dead_tuples
                FROM pg_stat_user_tables 
                WHERE schemaname = 'public'
                ORDER BY tablename
                """)
                .fetch()
                .all()
                .collectList()
                .map(rows -> {
                    Map<String, TableInfo> tables = new HashMap<>();
                    long totalRows = 0;
                    long totalInserts = 0;
                    long totalUpdates = 0;
                    long totalDeletes = 0;

                    for (Map<String, Object> row : rows) {
                        String tableName = (String) row.get("tablename");
                        long liveTuples = ((Number) row.get("live_tuples")).longValue();
                        long inserts = ((Number) row.get("inserts")).longValue();
                        long updates = ((Number) row.get("updates")).longValue();
                        long deletes = ((Number) row.get("deletes")).longValue();

                        tables.put(tableName, new TableInfo(tableName, liveTuples, inserts, updates, deletes));
                        totalRows += liveTuples;
                        totalInserts += inserts;
                        totalUpdates += updates;
                        totalDeletes += deletes;
                    }

                    return new TableStats(tables, totalRows, totalInserts, totalUpdates, totalDeletes);
                })
                .onErrorReturn(new TableStats(new HashMap<>(), 0, 0, 0, 0));
    }

    /**
     * Calculate error rate
     * @return Error rate percentage
     */
    private double calculateErrorRate() {
        long totalOperations = (long) (queryCounter.count() + errorCounter.count());
        if (totalOperations == 0) {
            return 0.0;
        }
        return (errorCounter.count() / totalOperations) * 100.0;
    }

    /**
     * Connection statistics information class
     */
    public static class ConnectionStats {
        private final long totalConnections;
        private final long activeConnections;
        private final long idleConnections;
        private final long connectionErrors;
        private final double averageConnectionTime;

        public ConnectionStats(long totalConnections, long activeConnections, long idleConnections, 
                             long connectionErrors, double averageConnectionTime) {
            this.totalConnections = totalConnections;
            this.activeConnections = activeConnections;
            this.idleConnections = idleConnections;
            this.connectionErrors = connectionErrors;
            this.averageConnectionTime = averageConnectionTime;
        }

        // Getters
        public long getTotalConnections() { return totalConnections; }
        public long getActiveConnections() { return activeConnections; }
        public long getIdleConnections() { return idleConnections; }
        public long getConnectionErrors() { return connectionErrors; }
        public double getAverageConnectionTime() { return averageConnectionTime; }
    }

    /**
     * Performance statistics information class
     */
    public static class PerformanceStats {
        private final long totalQueries;
        private final long totalInserts;
        private final long totalUpdates;
        private final long totalDeletes;
        private final long totalErrors;
        private final double averageQueryTime;
        private final double maxQueryTime;
        private final double errorRate;

        public PerformanceStats(long totalQueries, long totalInserts, long totalUpdates, 
                              long totalDeletes, long totalErrors, double averageQueryTime, 
                              double maxQueryTime, double errorRate) {
            this.totalQueries = totalQueries;
            this.totalInserts = totalInserts;
            this.totalUpdates = totalUpdates;
            this.totalDeletes = totalDeletes;
            this.totalErrors = totalErrors;
            this.averageQueryTime = averageQueryTime;
            this.maxQueryTime = maxQueryTime;
            this.errorRate = errorRate;
        }

        // Getters
        public long getTotalQueries() { return totalQueries; }
        public long getTotalInserts() { return totalInserts; }
        public long getTotalUpdates() { return totalUpdates; }
        public long getTotalDeletes() { return totalDeletes; }
        public long getTotalErrors() { return totalErrors; }
        public double getAverageQueryTime() { return averageQueryTime; }
        public double getMaxQueryTime() { return maxQueryTime; }
        public double getErrorRate() { return errorRate; }
    }

    /**
     * Database health information class
     */
    public static class DatabaseHealth {
        private final String status;
        private final String version;
        private final String database;
        private final String user;
        private final Instant lastChecked;
        private final String message;

        public DatabaseHealth(String status, String version, String database, String user, 
                            Instant lastChecked, String message) {
            this.status = status;
            this.version = version;
            this.database = database;
            this.user = user;
            this.lastChecked = lastChecked;
            this.message = message;
        }

        // Getters
        public String getStatus() { return status; }
        public String getVersion() { return version; }
        public String getDatabase() { return database; }
        public String getUser() { return user; }
        public Instant getLastChecked() { return lastChecked; }
        public String getMessage() { return message; }
    }

    /**
     * Table statistics information class
     */
    public static class TableStats {
        private final Map<String, TableInfo> tables;
        private final long totalRows;
        private final long totalInserts;
        private final long totalUpdates;
        private final long totalDeletes;

        public TableStats(Map<String, TableInfo> tables, long totalRows, long totalInserts, 
                         long totalUpdates, long totalDeletes) {
            this.tables = tables;
            this.totalRows = totalRows;
            this.totalInserts = totalInserts;
            this.totalUpdates = totalUpdates;
            this.totalDeletes = totalDeletes;
        }

        // Getters
        public Map<String, TableInfo> getTables() { return tables; }
        public long getTotalRows() { return totalRows; }
        public long getTotalInserts() { return totalInserts; }
        public long getTotalUpdates() { return totalUpdates; }
        public long getTotalDeletes() { return totalDeletes; }
    }

    /**
     * Table information class
     */
    public static class TableInfo {
        private final String tableName;
        private final long rowCount;
        private final long inserts;
        private final long updates;
        private final long deletes;

        public TableInfo(String tableName, long rowCount, long inserts, long updates, long deletes) {
            this.tableName = tableName;
            this.rowCount = rowCount;
            this.inserts = inserts;
            this.updates = updates;
            this.deletes = deletes;
        }

        // Getters
        public String getTableName() { return tableName; }
        public long getRowCount() { return rowCount; }
        public long getInserts() { return inserts; }
        public long getUpdates() { return updates; }
        public long getDeletes() { return deletes; }
    }
}

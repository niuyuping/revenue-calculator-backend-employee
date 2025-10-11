package jp.asatex.revenue_calculator_backend_employee.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Database monitoring service
 * Provides database-related monitoring and statistics functionality
 */
@Service
public class DatabaseMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMonitoringService.class);

    private final DatabaseClient databaseClient;

    public DatabaseMonitoringService(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    /**
     * Get comprehensive database statistics
     * @return Complete database monitoring information
     */
    public Mono<DatabaseStats> getDatabaseStats() {
        return Mono.zip(
                getDatabaseHealth(),
                getTableStats(),
                getConnectionPoolStats()
        ).map(tuple -> new DatabaseStats(
                tuple.getT1(), // health
                tuple.getT2(), // table stats
                tuple.getT3()  // connection stats
        )).onErrorReturn(new DatabaseStats(
                new DatabaseHealth("DOWN", "Unknown", "Unknown", "Unknown", Instant.now(), "Connection failed"),
                new TableStats(new HashMap<>(), 0, 0, 0, 0),
                new ConnectionStats(0, 0, 0, 0, 0.0)
        ));
    }

    /**
     * Get database health information
     * @return Database health information
     */
    private Mono<DatabaseHealth> getDatabaseHealth() {
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
    private Mono<TableStats> getTableStats() {
        return databaseClient.sql("""
                SELECT 
                    t.table_name,
                    COALESCE(s.n_tup_ins, 0) as inserts,
                    COALESCE(s.n_tup_upd, 0) as updates,
                    COALESCE(s.n_tup_del, 0) as deletes,
                    COALESCE(s.n_live_tup, 0) as live_tuples,
                    COALESCE(s.n_dead_tup, 0) as dead_tuples,
                    COALESCE(c.reltuples::bigint, 0) as estimated_rows
                FROM information_schema.tables t
                LEFT JOIN pg_stat_user_tables s ON t.table_name = s.relname
                LEFT JOIN pg_class c ON c.relname = t.table_name
                WHERE t.table_schema = 'public' 
                AND t.table_type = 'BASE TABLE'
                ORDER BY t.table_name
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
                        String tableName = (String) row.get("table_name");
                        long liveTuples = ((Number) row.get("live_tuples")).longValue();
                        long estimatedRows = ((Number) row.get("estimated_rows")).longValue();
                        long inserts = ((Number) row.get("inserts")).longValue();
                        long updates = ((Number) row.get("updates")).longValue();
                        long deletes = ((Number) row.get("deletes")).longValue();

                        // Use estimated rows if live_tuples is 0 (for new tables)
                        long actualRowCount = liveTuples > 0 ? liveTuples : estimatedRows;

                        tables.put(tableName, new TableInfo(tableName, actualRowCount, inserts, updates, deletes));
                        totalRows += actualRowCount;
                        totalInserts += inserts;
                        totalUpdates += updates;
                        totalDeletes += deletes;
                    }

                    return new TableStats(tables, totalRows, totalInserts, totalUpdates, totalDeletes);
                })
                .onErrorReturn(new TableStats(new HashMap<>(), 0, 0, 0, 0));
    }

    /**
     * Get connection pool statistics from PostgreSQL
     * @return Connection pool statistics
     */
    private Mono<ConnectionStats> getConnectionPoolStats() {
        return databaseClient.sql("""
                SELECT 
                    (SELECT setting::int FROM pg_settings WHERE name = 'max_connections') as max_connections,
                    (SELECT count(*) FROM pg_stat_activity WHERE state = 'active') as active_connections,
                    (SELECT count(*) FROM pg_stat_activity WHERE state = 'idle') as idle_connections,
                    (SELECT count(*) FROM pg_stat_activity) as total_connections
                """)
                .fetch()
                .first()
                .map(result -> {
                    int maxConnections = ((Number) result.get("max_connections")).intValue();
                    int activeConnections = ((Number) result.get("active_connections")).intValue();
                    int idleConnections = ((Number) result.get("idle_connections")).intValue();
                    int totalConnections = ((Number) result.get("total_connections")).intValue();
                    
                    return new ConnectionStats(
                            totalConnections,
                            activeConnections,
                            idleConnections,
                            0, // connection errors - not easily available from pg_stat_activity
                            0.0 // average connection time - not easily available
                    );
                })
                .onErrorReturn(new ConnectionStats(0, 0, 0, 0, 0.0));
    }

    /**
     * Comprehensive database statistics information class
     */
    public static class DatabaseStats {
        private final DatabaseHealth health;
        private final TableStats tableStats;
        private final ConnectionStats connectionStats;

        public DatabaseStats(DatabaseHealth health, TableStats tableStats, ConnectionStats connectionStats) {
            this.health = health;
            this.tableStats = tableStats;
            this.connectionStats = connectionStats;
        }

        // Getters
        public DatabaseHealth getHealth() { return health; }
        public TableStats getTableStats() { return tableStats; }
        public ConnectionStats getConnectionStats() { return connectionStats; }
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

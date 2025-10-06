package jp.asatex.revenue_calculator_backend_employee.config;

import jp.asatex.revenue_calculator_backend_employee.service.DatabaseAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.binding.Bindings;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * R2DBC audit interceptor
 * Intercepts database operations and records audit logs
 */
@Component
public class R2dbcAuditInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(R2dbcAuditInterceptor.class);

    @Autowired
    private DatabaseAuditService databaseAuditService;

    /**
     * Intercept and log database operations
     * @param sql SQL statement
     * @param bindings Binding parameters
     * @param operation Operation type
     * @return Wrapped Mono
     */
    public <T> Mono<T> interceptOperation(String sql, Bindings bindings, String operation, Mono<T> operationMono) {
        long startTime = System.currentTimeMillis();
        String tableName = extractTableName(sql);
        String recordId = extractRecordId(sql, bindings);

        return operationMono
                .doOnSuccess(result -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logSuccessfulOperation(operation, tableName, recordId, sql, executionTime, 1);
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    logFailedOperation(operation, tableName, recordId, sql, executionTime, error.getMessage());
                });
    }

    /**
     * Intercept INSERT operations
     * @param sql SQL statement
     * @param bindings Binding parameters
     * @param operationMono Operation Mono
     * @return Wrapped Mono
     */
    public <T> Mono<T> interceptInsert(String sql, Bindings bindings, Mono<T> operationMono) {
        return interceptOperation(sql, bindings, "INSERT", operationMono);
    }

    /**
     * Intercept UPDATE operations
     * @param sql SQL statement
     * @param bindings Binding parameters
     * @param operationMono Operation Mono
     * @return Wrapped Mono
     */
    public <T> Mono<T> interceptUpdate(String sql, Bindings bindings, Mono<T> operationMono) {
        return interceptOperation(sql, bindings, "UPDATE", operationMono);
    }

    /**
     * Intercept DELETE operations
     * @param sql SQL statement
     * @param bindings Binding parameters
     * @param operationMono Operation Mono
     * @return Wrapped Mono
     */
    public <T> Mono<T> interceptDelete(String sql, Bindings bindings, Mono<T> operationMono) {
        return interceptOperation(sql, bindings, "DELETE", operationMono);
    }

    /**
     * Intercept SELECT operations
     * @param sql SQL statement
     * @param bindings Binding parameters
     * @param operationMono Operation Mono
     * @return Wrapped Mono
     */
    public <T> Mono<T> interceptSelect(String sql, Bindings bindings, Mono<T> operationMono) {
        return interceptOperation(sql, bindings, "SELECT", operationMono);
    }

    /**
     * Log successful operation
     * @param operation Operation type
     * @param tableName Table name
     * @param recordId Record ID
     * @param sql SQL statement
     * @param executionTime Execution time
     * @param affectedRows Affected rows
     */
    private void logSuccessfulOperation(String operation, String tableName, String recordId, 
                                      String sql, long executionTime, int affectedRows) {
        try {
            Map<String, Object> newValues = extractValuesFromSql(sql);
            databaseAuditService.logSuccessfulOperation(operation, tableName, recordId, 
                    null, newValues, sql, executionTime, affectedRows)
                    .subscribe();
        } catch (Exception e) {
            logger.error("Failed to log successful operation audit", e);
        }
    }

    /**
     * Log failed operation
     * @param operation Operation type
     * @param tableName Table name
     * @param recordId Record ID
     * @param sql SQL statement
     * @param executionTime Execution time
     * @param errorMessage Error message
     */
    private void logFailedOperation(String operation, String tableName, String recordId, 
                                  String sql, long executionTime, String errorMessage) {
        try {
            databaseAuditService.logFailedOperation(operation, tableName, recordId, 
                    sql, executionTime, errorMessage)
                    .subscribe();
        } catch (Exception e) {
            logger.error("Failed to log failed operation audit", e);
        }
    }

    /**
     * Extract table name from SQL statement
     * @param sql SQL statement
     * @return Table name
     */
    private String extractTableName(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "unknown";
        }

        String upperSql = sql.trim().toUpperCase();
        
        if (upperSql.startsWith("INSERT INTO")) {
            return extractTableNameFromInsert(upperSql);
        } else if (upperSql.startsWith("UPDATE")) {
            return extractTableNameFromUpdate(upperSql);
        } else if (upperSql.startsWith("DELETE FROM")) {
            return extractTableNameFromDelete(upperSql);
        } else if (upperSql.startsWith("SELECT")) {
            return extractTableNameFromSelect(upperSql);
        }
        
        return "unknown";
    }

    /**
     * Extract table name from INSERT statement
     * @param sql SQL statement
     * @return Table name
     */
    private String extractTableNameFromInsert(String sql) {
        try {
            String[] parts = sql.split("\\s+");
            for (int i = 0; i < parts.length - 1; i++) {
                if ("INTO".equals(parts[i])) {
                    return parts[i + 1].toLowerCase();
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to extract INSERT table name: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * Extract table name from UPDATE statement
     * @param sql SQL statement
     * @return Table name
     */
    private String extractTableNameFromUpdate(String sql) {
        try {
            String[] parts = sql.split("\\s+");
            if (parts.length > 1) {
                return parts[1].toLowerCase();
            }
        } catch (Exception e) {
            logger.debug("Failed to extract UPDATE table name: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * Extract table name from DELETE statement
     * @param sql SQL statement
     * @return Table name
     */
    private String extractTableNameFromDelete(String sql) {
        try {
            String[] parts = sql.split("\\s+");
            for (int i = 0; i < parts.length - 1; i++) {
                if ("FROM".equals(parts[i])) {
                    return parts[i + 1].toLowerCase();
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to extract DELETE table name: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * Extract table name from SELECT statement
     * @param sql SQL statement
     * @return Table name
     */
    private String extractTableNameFromSelect(String sql) {
        try {
            String[] parts = sql.split("\\s+");
            for (int i = 0; i < parts.length - 1; i++) {
                if ("FROM".equals(parts[i])) {
                    return parts[i + 1].toLowerCase();
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to extract SELECT table name: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * Extract record ID from SQL statement and binding parameters
     * @param sql SQL statement
     * @param bindings Binding parameters
     * @return Record ID
     */
    private String extractRecordId(String sql, Bindings bindings) {
        try {
            // Try to extract ID from WHERE clause
            if (sql.toUpperCase().contains("WHERE")) {
                String whereClause = sql.substring(sql.toUpperCase().indexOf("WHERE"));
                if (whereClause.contains("id =")) {
                    // Can further parse binding parameters to get actual ID value
                    return "extracted_from_where";
                }
            }
            
            // For INSERT operations, try to extract ID from VALUES
            if (sql.toUpperCase().startsWith("INSERT")) {
                return "new_record";
            }
            
        } catch (Exception e) {
            logger.debug("Failed to extract record ID: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * Extract values from SQL statement
     * @param sql SQL statement
     * @return Value map
     */
    private Map<String, Object> extractValuesFromSql(String sql) {
        Map<String, Object> values = new HashMap<>();
        try {
            // Can implement more complex SQL parsing logic here
            // Currently just returns a simple identifier
            values.put("sql_operation", "parsed");
        } catch (Exception e) {
            logger.debug("Failed to extract values from SQL: {}", e.getMessage());
        }
        return values;
    }
}

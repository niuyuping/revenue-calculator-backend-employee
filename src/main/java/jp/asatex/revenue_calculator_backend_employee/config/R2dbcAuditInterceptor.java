package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import jp.asatex.revenue_calculator_backend_employee.service.DatabaseAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;
import org.springframework.r2dbc.core.binding.BindTarget;
import org.springframework.r2dbc.core.binding.Bindings;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * R2DBC审计拦截器
 * 拦截数据库操作并记录审计日志
 */
@Component
public class R2dbcAuditInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(R2dbcAuditInterceptor.class);

    @Autowired
    private DatabaseAuditService databaseAuditService;

    /**
     * 拦截并记录数据库操作
     * @param sql SQL语句
     * @param bindings 绑定参数
     * @param operation 操作类型
     * @return 包装后的Mono
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
     * 拦截INSERT操作
     * @param sql SQL语句
     * @param bindings 绑定参数
     * @param operationMono 操作Mono
     * @return 包装后的Mono
     */
    public <T> Mono<T> interceptInsert(String sql, Bindings bindings, Mono<T> operationMono) {
        return interceptOperation(sql, bindings, "INSERT", operationMono);
    }

    /**
     * 拦截UPDATE操作
     * @param sql SQL语句
     * @param bindings 绑定参数
     * @param operationMono 操作Mono
     * @return 包装后的Mono
     */
    public <T> Mono<T> interceptUpdate(String sql, Bindings bindings, Mono<T> operationMono) {
        return interceptOperation(sql, bindings, "UPDATE", operationMono);
    }

    /**
     * 拦截DELETE操作
     * @param sql SQL语句
     * @param bindings 绑定参数
     * @param operationMono 操作Mono
     * @return 包装后的Mono
     */
    public <T> Mono<T> interceptDelete(String sql, Bindings bindings, Mono<T> operationMono) {
        return interceptOperation(sql, bindings, "DELETE", operationMono);
    }

    /**
     * 拦截SELECT操作
     * @param sql SQL语句
     * @param bindings 绑定参数
     * @param operationMono 操作Mono
     * @return 包装后的Mono
     */
    public <T> Mono<T> interceptSelect(String sql, Bindings bindings, Mono<T> operationMono) {
        return interceptOperation(sql, bindings, "SELECT", operationMono);
    }

    /**
     * 记录成功的操作
     * @param operation 操作类型
     * @param tableName 表名
     * @param recordId 记录ID
     * @param sql SQL语句
     * @param executionTime 执行时间
     * @param affectedRows 影响行数
     */
    private void logSuccessfulOperation(String operation, String tableName, String recordId, 
                                      String sql, long executionTime, int affectedRows) {
        try {
            Map<String, Object> newValues = extractValuesFromSql(sql);
            databaseAuditService.logSuccessfulOperation(operation, tableName, recordId, 
                    null, newValues, sql, executionTime, affectedRows)
                    .subscribe();
        } catch (Exception e) {
            logger.error("记录成功操作审计日志失败", e);
        }
    }

    /**
     * 记录失败的操作
     * @param operation 操作类型
     * @param tableName 表名
     * @param recordId 记录ID
     * @param sql SQL语句
     * @param executionTime 执行时间
     * @param errorMessage 错误消息
     */
    private void logFailedOperation(String operation, String tableName, String recordId, 
                                  String sql, long executionTime, String errorMessage) {
        try {
            databaseAuditService.logFailedOperation(operation, tableName, recordId, 
                    sql, executionTime, errorMessage)
                    .subscribe();
        } catch (Exception e) {
            logger.error("记录失败操作审计日志失败", e);
        }
    }

    /**
     * 从SQL语句中提取表名
     * @param sql SQL语句
     * @return 表名
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
     * 从INSERT语句中提取表名
     * @param sql SQL语句
     * @return 表名
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
            logger.debug("提取INSERT表名失败: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 从UPDATE语句中提取表名
     * @param sql SQL语句
     * @return 表名
     */
    private String extractTableNameFromUpdate(String sql) {
        try {
            String[] parts = sql.split("\\s+");
            if (parts.length > 1) {
                return parts[1].toLowerCase();
            }
        } catch (Exception e) {
            logger.debug("提取UPDATE表名失败: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 从DELETE语句中提取表名
     * @param sql SQL语句
     * @return 表名
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
            logger.debug("提取DELETE表名失败: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 从SELECT语句中提取表名
     * @param sql SQL语句
     * @return 表名
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
            logger.debug("提取SELECT表名失败: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 从SQL语句和绑定参数中提取记录ID
     * @param sql SQL语句
     * @param bindings 绑定参数
     * @return 记录ID
     */
    private String extractRecordId(String sql, Bindings bindings) {
        try {
            // 尝试从WHERE条件中提取ID
            if (sql.toUpperCase().contains("WHERE")) {
                String whereClause = sql.substring(sql.toUpperCase().indexOf("WHERE"));
                if (whereClause.contains("id =")) {
                    // 这里可以进一步解析绑定参数来获取实际的ID值
                    return "extracted_from_where";
                }
            }
            
            // 对于INSERT操作，尝试从VALUES中提取ID
            if (sql.toUpperCase().startsWith("INSERT")) {
                return "new_record";
            }
            
        } catch (Exception e) {
            logger.debug("提取记录ID失败: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 从SQL语句中提取值
     * @param sql SQL语句
     * @return 值Map
     */
    private Map<String, Object> extractValuesFromSql(String sql) {
        Map<String, Object> values = new HashMap<>();
        try {
            // 这里可以实现更复杂的SQL解析逻辑
            // 目前只是简单返回一个标识
            values.put("sql_operation", "parsed");
        } catch (Exception e) {
            logger.debug("从SQL提取值失败: {}", e.getMessage());
        }
        return values;
    }
}

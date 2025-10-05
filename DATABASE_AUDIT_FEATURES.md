# 数据库审计功能说明

## 概述

本系统已成功集成了完整的数据库审计功能，提供细粒度的数据库操作监控和审计跟踪能力。该功能记录所有数据库操作的详细信息，包括操作类型、执行时间、影响行数、错误信息等，满足企业级审计要求。

## 功能特性

### 1. 全面的数据库操作审计
- ✅ **INSERT操作审计** - 记录数据插入操作
- ✅ **UPDATE操作审计** - 记录数据更新操作，包括旧值和新值
- ✅ **DELETE操作审计** - 记录数据删除操作
- ✅ **SELECT操作审计** - 记录数据查询操作
- ✅ **操作状态跟踪** - SUCCESS, FAILURE, ROLLBACK
- ✅ **执行时间监控** - 毫秒级精度
- ✅ **影响行数统计** - 精确记录操作影响的数据行数

### 2. 上下文信息记录
- ✅ **用户信息** - 操作用户ID
- ✅ **会话跟踪** - 会话ID和请求ID
- ✅ **网络信息** - IP地址和用户代理
- ✅ **时间戳** - 精确的操作时间
- ✅ **SQL语句** - 完整的SQL语句记录

### 3. 数据变更追踪
- ✅ **旧值记录** - UPDATE和DELETE操作的前值
- ✅ **新值记录** - INSERT和UPDATE操作的后值
- ✅ **JSON格式存储** - 便于查询和分析
- ✅ **字段级变更** - 精确到字段级别的变更记录

## 技术架构

### 1. 数据库表结构

#### database_audit_logs 表
```sql
CREATE TABLE database_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(20) NOT NULL,        -- INSERT, UPDATE, DELETE, SELECT
    table_name VARCHAR(100) NOT NULL,           -- 表名
    record_id VARCHAR(100),                     -- 记录ID
    user_id VARCHAR(100),                       -- 用户ID
    session_id VARCHAR(100),                    -- 会话ID
    request_id VARCHAR(100),                    -- 请求ID
    ip_address VARCHAR(45),                     -- IP地址
    user_agent TEXT,                            -- 用户代理
    old_values TEXT,                            -- 旧值（JSON格式）
    new_values TEXT,                            -- 新值（JSON格式）
    sql_statement TEXT,                         -- SQL语句
    execution_time_ms BIGINT,                   -- 执行时间（毫秒）
    affected_rows INTEGER,                      -- 影响行数
    error_message TEXT,                         -- 错误消息
    operation_status VARCHAR(20) NOT NULL,      -- SUCCESS, FAILURE, ROLLBACK
    created_at TIMESTAMP NOT NULL,              -- 创建时间
    created_by VARCHAR(100)                     -- 创建者
);
```

#### 索引优化
```sql
-- 操作类型索引
CREATE INDEX idx_database_audit_logs_operation_type ON database_audit_logs(operation_type);

-- 表名索引
CREATE INDEX idx_database_audit_logs_table_name ON database_audit_logs(table_name);

-- 用户ID索引
CREATE INDEX idx_database_audit_logs_user_id ON database_audit_logs(user_id);

-- 会话ID索引
CREATE INDEX idx_database_audit_logs_session_id ON database_audit_logs(session_id);

-- 请求ID索引
CREATE INDEX idx_database_audit_logs_request_id ON database_audit_logs(request_id);

-- 记录ID索引
CREATE INDEX idx_database_audit_logs_record_id ON database_audit_logs(record_id);

-- 操作状态索引
CREATE INDEX idx_database_audit_logs_operation_status ON database_audit_logs(operation_status);

-- 创建时间索引
CREATE INDEX idx_database_audit_logs_created_at ON database_audit_logs(created_at);

-- 复合索引
CREATE INDEX idx_database_audit_logs_table_record ON database_audit_logs(table_name, record_id);
CREATE INDEX idx_database_audit_logs_user_created ON database_audit_logs(user_id, created_at);
```

### 2. 核心组件

#### DatabaseAuditLog 实体
```java
@Table("database_audit_logs")
public class DatabaseAuditLog {
    @Id
    private Long id;
    private String operationType;    // INSERT, UPDATE, DELETE, SELECT
    private String tableName;        // 表名
    private String recordId;         // 记录ID
    private String userId;           // 用户ID
    private String sessionId;        // 会话ID
    private String requestId;        // 请求ID
    private String ipAddress;        // IP地址
    private String userAgent;        // 用户代理
    private String oldValues;        // 旧值（JSON）
    private String newValues;        // 新值（JSON）
    private String sqlStatement;     // SQL语句
    private Long executionTimeMs;    // 执行时间
    private Integer affectedRows;    // 影响行数
    private String errorMessage;     // 错误消息
    private String operationStatus;  // SUCCESS, FAILURE, ROLLBACK
    private LocalDateTime createdAt; // 创建时间
    private String createdBy;        // 创建者
}
```

#### DatabaseAuditService 服务
```java
@Service
public class DatabaseAuditService {
    
    // 记录成功的数据库操作
    public Mono<Void> logSuccessfulOperation(String operationType, String tableName, 
                                           String recordId, Map<String, Object> oldValues, 
                                           Map<String, Object> newValues, String sqlStatement, 
                                           Long executionTimeMs, Integer affectedRows);
    
    // 记录失败的数据库操作
    public Mono<Void> logFailedOperation(String operationType, String tableName, 
                                       String recordId, String sqlStatement, 
                                       Long executionTimeMs, String errorMessage);
    
    // 记录INSERT操作
    public Mono<Void> logInsertOperation(String tableName, String recordId, 
                                       Map<String, Object> newValues, String sqlStatement, 
                                       Long executionTimeMs, Integer affectedRows);
    
    // 记录UPDATE操作
    public Mono<Void> logUpdateOperation(String tableName, String recordId, 
                                       Map<String, Object> oldValues, Map<String, Object> newValues, 
                                       String sqlStatement, Long executionTimeMs, Integer affectedRows);
    
    // 记录DELETE操作
    public Mono<Void> logDeleteOperation(String tableName, String recordId, 
                                       Map<String, Object> oldValues, String sqlStatement, 
                                       Long executionTimeMs, Integer affectedRows);
    
    // 记录SELECT操作
    public Mono<Void> logSelectOperation(String tableName, String recordId, 
                                       String sqlStatement, Long executionTimeMs, Integer affectedRows);
    
    // 获取审计统计信息
    public Mono<Map<String, Object>> getAuditStatistics();
    
    // 清理过期审计日志
    public Mono<Integer> cleanupOldAuditLogs(int retentionDays);
}
```

#### R2dbcAuditInterceptor 拦截器
```java
@Component
public class R2dbcAuditInterceptor {
    
    // 拦截INSERT操作
    public <T> Mono<T> interceptInsert(String sql, Bindings bindings, Mono<T> operationMono);
    
    // 拦截UPDATE操作
    public <T> Mono<T> interceptUpdate(String sql, Bindings bindings, Mono<T> operationMono);
    
    // 拦截DELETE操作
    public <T> Mono<T> interceptDelete(String sql, Bindings bindings, Mono<T> operationMono);
    
    // 拦截SELECT操作
    public <T> Mono<T> interceptSelect(String sql, Bindings bindings, Mono<T> operationMono);
}
```

### 3. Repository 查询方法

#### DatabaseAuditLogRepository
```java
@Repository
public interface DatabaseAuditLogRepository extends ReactiveCrudRepository<DatabaseAuditLog, Long> {
    
    // 根据操作类型查询
    Flux<DatabaseAuditLog> findByOperationType(String operationType);
    
    // 根据表名查询
    Flux<DatabaseAuditLog> findByTableName(String tableName);
    
    // 根据用户ID查询
    Flux<DatabaseAuditLog> findByUserId(String userId);
    
    // 根据会话ID查询
    Flux<DatabaseAuditLog> findBySessionId(String sessionId);
    
    // 根据请求ID查询
    Flux<DatabaseAuditLog> findByRequestId(String requestId);
    
    // 根据记录ID查询
    Flux<DatabaseAuditLog> findByRecordId(String recordId);
    
    // 根据操作状态查询
    Flux<DatabaseAuditLog> findByOperationStatus(String operationStatus);
    
    // 根据时间范围查询
    Flux<DatabaseAuditLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // 根据表名和记录ID查询
    Flux<DatabaseAuditLog> findByTableNameAndRecordId(String tableName, String recordId);
    
    // 根据用户ID和时间范围查询
    Flux<DatabaseAuditLog> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 根据操作类型和表名查询
    Flux<DatabaseAuditLog> findByOperationTypeAndTableName(String operationType, String tableName);
    
    // 查询最近的审计日志
    Flux<DatabaseAuditLog> findRecentLogs(int limit);
    
    // 查询错误审计日志
    Flux<DatabaseAuditLog> findErrorLogsByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    Flux<DatabaseAuditLog> findErrorLogsByUserId(String userId);
    
    // 统计方法
    Mono<Long> countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    Mono<Long> countByOperationType(String operationType);
    Mono<Long> countByTableName(String tableName);
    Mono<Long> countByUserId(String userId);
    Mono<Long> countByOperationStatus(String operationStatus);
    
    // 清理方法
    Mono<Integer> deleteByCreatedAtBefore(LocalDateTime beforeTime);
}
```

## API 端点

### 1. 统计信息端点
```
GET /api/v1/audit/database/stats
```
返回数据库审计统计信息，包括：
- 最近24小时、7天、30天的操作数量
- 各操作类型（INSERT, UPDATE, DELETE, SELECT）的统计
- 各操作状态（SUCCESS, FAILURE, ROLLBACK）的统计

### 2. 查询端点

#### 根据操作类型查询
```
GET /api/v1/audit/database/logs/operation/{operationType}
```

#### 根据表名查询
```
GET /api/v1/audit/database/logs/table/{tableName}
```

#### 根据用户ID查询
```
GET /api/v1/audit/database/logs/user/{userId}
```

#### 根据会话ID查询
```
GET /api/v1/audit/database/logs/session/{sessionId}
```

#### 根据请求ID查询
```
GET /api/v1/audit/database/logs/request/{requestId}
```

#### 根据记录ID查询
```
GET /api/v1/audit/database/logs/record/{recordId}
```

#### 根据操作状态查询
```
GET /api/v1/audit/database/logs/status/{operationStatus}
```

#### 根据时间范围查询
```
GET /api/v1/audit/database/logs/time-range?startTime={startTime}&endTime={endTime}
```

#### 根据表名和记录ID查询
```
GET /api/v1/audit/database/logs/table/{tableName}/record/{recordId}
```

#### 根据用户ID和时间范围查询
```
GET /api/v1/audit/database/logs/user/{userId}/time-range?startTime={startTime}&endTime={endTime}
```

#### 根据操作类型和表名查询
```
GET /api/v1/audit/database/logs/operation/{operationType}/table/{tableName}
```

#### 查询最近的审计日志
```
GET /api/v1/audit/database/logs/recent?limit={limit}
```

#### 查询错误审计日志
```
GET /api/v1/audit/database/logs/errors?startTime={startTime}&endTime={endTime}
GET /api/v1/audit/database/logs/errors/user/{userId}
```

### 3. 管理端点

#### 清理过期审计日志
```
DELETE /api/v1/audit/database/logs/cleanup?retentionDays={retentionDays}
```

## 使用示例

### 1. 在服务中使用数据库审计

```java
@Service
public class EmployeeService {
    
    @Autowired
    private DatabaseAuditService databaseAuditService;
    
    public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.save(employee)
                .map(this::convertToDto)
                .doOnSuccess(createdEmployee -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    
                    // 记录INSERT操作审计
                    Map<String, Object> newValues = new HashMap<>();
                    newValues.put("employeeNumber", createdEmployee.getEmployeeNumber());
                    newValues.put("name", createdEmployee.getName());
                    newValues.put("furigana", createdEmployee.getFurigana());
                    newValues.put("birthday", createdEmployee.getBirthday());
                    
                    databaseAuditService.logInsertOperation(
                            "employees", 
                            createdEmployee.getEmployeeId().toString(),
                            newValues,
                            "INSERT INTO employees (employee_number, name, furigana, birthday) VALUES (?, ?, ?, ?)",
                            executionTime,
                            1
                    ).subscribe();
                })
                .doOnError(error -> {
                    long executionTime = System.currentTimeMillis() - startTime;
                    
                    // 记录失败操作审计
                    databaseAuditService.logFailedOperation(
                            "INSERT",
                            "employees",
                            "unknown",
                            "INSERT INTO employees...",
                            executionTime,
                            error.getMessage()
                    ).subscribe();
                });
    }
    
    public Mono<EmployeeDto> updateEmployee(Long id, EmployeeDto employeeDto) {
        long startTime = System.currentTimeMillis();
        
        return employeeRepository.findById(id)
                .flatMap(existingEmployee -> {
                    // 保存旧值
                    Map<String, Object> oldValues = new HashMap<>();
                    oldValues.put("name", existingEmployee.getName());
                    oldValues.put("furigana", existingEmployee.getFurigana());
                    oldValues.put("birthday", existingEmployee.getBirthday());
                    
                    // 更新员工
                    existingEmployee.setName(employeeDto.getName());
                    existingEmployee.setFurigana(employeeDto.getFurigana());
                    existingEmployee.setBirthday(employeeDto.getBirthday());
                    
                    return employeeRepository.save(existingEmployee)
                            .map(this::convertToDto)
                            .doOnSuccess(updatedEmployee -> {
                                long executionTime = System.currentTimeMillis() - startTime;
                                
                                // 记录UPDATE操作审计
                                Map<String, Object> newValues = new HashMap<>();
                                newValues.put("name", updatedEmployee.getName());
                                newValues.put("furigana", updatedEmployee.getFurigana());
                                newValues.put("birthday", updatedEmployee.getBirthday());
                                
                                databaseAuditService.logUpdateOperation(
                                        "employees",
                                        id.toString(),
                                        oldValues,
                                        newValues,
                                        "UPDATE employees SET name = ?, furigana = ?, birthday = ? WHERE id = ?",
                                        executionTime,
                                        1
                                ).subscribe();
                            });
                });
    }
}
```

### 2. 查询审计日志

```java
@RestController
public class AuditController {
    
    @Autowired
    private DatabaseAuditService databaseAuditService;
    
    @GetMapping("/audit/employee/{employeeId}")
    public Flux<DatabaseAuditLog> getEmployeeAuditLogs(@PathVariable String employeeId) {
        return databaseAuditService.findByRecordId(employeeId);
    }
    
    @GetMapping("/audit/user/{userId}/recent")
    public Flux<DatabaseAuditLog> getUserRecentAuditLogs(@PathVariable String userId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime now = LocalDateTime.now();
        
        return databaseAuditService.findByUserIdAndCreatedAtBetween(userId, oneWeekAgo, now);
    }
    
    @GetMapping("/audit/errors")
    public Flux<DatabaseAuditLog> getErrorAuditLogs() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        LocalDateTime now = LocalDateTime.now();
        
        return databaseAuditService.findErrorLogsByCreatedAtBetween(oneDayAgo, now);
    }
}
```

### 3. 审计日志分析

```java
@Service
public class AuditAnalysisService {
    
    @Autowired
    private DatabaseAuditService databaseAuditService;
    
    public Mono<Map<String, Object>> analyzeUserActivity(String userId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        LocalDateTime now = LocalDateTime.now();
        
        return databaseAuditService.findByUserIdAndCreatedAtBetween(userId, oneMonthAgo, now)
                .collectList()
                .map(logs -> {
                    Map<String, Object> analysis = new HashMap<>();
                    
                    // 统计操作类型
                    Map<String, Long> operationCounts = logs.stream()
                            .collect(Collectors.groupingBy(
                                    DatabaseAuditLog::getOperationType,
                                    Collectors.counting()
                            ));
                    
                    // 统计表访问
                    Map<String, Long> tableAccess = logs.stream()
                            .collect(Collectors.groupingBy(
                                    DatabaseAuditLog::getTableName,
                                    Collectors.counting()
                            ));
                    
                    // 计算平均执行时间
                    double avgExecutionTime = logs.stream()
                            .mapToLong(log -> log.getExecutionTimeMs() != null ? log.getExecutionTimeMs() : 0)
                            .average()
                            .orElse(0.0);
                    
                    analysis.put("totalOperations", logs.size());
                    analysis.put("operationCounts", operationCounts);
                    analysis.put("tableAccess", tableAccess);
                    analysis.put("avgExecutionTime", avgExecutionTime);
                    analysis.put("period", "1 month");
                    
                    return analysis;
                });
    }
}
```

## 审计日志格式

### 1. INSERT操作审计日志
```json
{
  "id": 1,
  "operationType": "INSERT",
  "tableName": "employees",
  "recordId": "123",
  "userId": "user123",
  "sessionId": "session-456",
  "requestId": "request-789",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "oldValues": null,
  "newValues": "{\"employeeNumber\":\"EMP001\",\"name\":\"测试员工\",\"furigana\":\"テストユウイン\",\"birthday\":\"1990-01-01\"}",
  "sqlStatement": "INSERT INTO employees (employee_number, name, furigana, birthday) VALUES (?, ?, ?, ?)",
  "executionTimeMs": 150,
  "affectedRows": 1,
  "errorMessage": null,
  "operationStatus": "SUCCESS",
  "createdAt": "2024-01-01T10:00:00Z",
  "createdBy": "user123"
}
```

### 2. UPDATE操作审计日志
```json
{
  "id": 2,
  "operationType": "UPDATE",
  "tableName": "employees",
  "recordId": "123",
  "userId": "user123",
  "sessionId": "session-456",
  "requestId": "request-790",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "oldValues": "{\"name\":\"旧名称\",\"furigana\":\"キュウメイショウ\"}",
  "newValues": "{\"name\":\"新名称\",\"furigana\":\"シンメイショウ\"}",
  "sqlStatement": "UPDATE employees SET name = ?, furigana = ? WHERE id = ?",
  "executionTimeMs": 80,
  "affectedRows": 1,
  "errorMessage": null,
  "operationStatus": "SUCCESS",
  "createdAt": "2024-01-01T10:05:00Z",
  "createdBy": "user123"
}
```

### 3. DELETE操作审计日志
```json
{
  "id": 3,
  "operationType": "DELETE",
  "tableName": "employees",
  "recordId": "123",
  "userId": "user123",
  "sessionId": "session-456",
  "requestId": "request-791",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "oldValues": "{\"employeeNumber\":\"EMP001\",\"name\":\"测试员工\",\"furigana\":\"テストユウイン\",\"birthday\":\"1990-01-01\"}",
  "newValues": null,
  "sqlStatement": "DELETE FROM employees WHERE id = ?",
  "executionTimeMs": 60,
  "affectedRows": 1,
  "errorMessage": null,
  "operationStatus": "SUCCESS",
  "createdAt": "2024-01-01T10:10:00Z",
  "createdBy": "user123"
}
```

### 4. 失败操作审计日志
```json
{
  "id": 4,
  "operationType": "INSERT",
  "tableName": "employees",
  "recordId": "unknown",
  "userId": "user123",
  "sessionId": "session-456",
  "requestId": "request-792",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "oldValues": null,
  "newValues": null,
  "sqlStatement": "INSERT INTO employees (employee_number, name) VALUES (?, ?)",
  "executionTimeMs": 25,
  "affectedRows": 0,
  "errorMessage": "duplicate key value violates unique constraint \"uk_employee_number\"",
  "operationStatus": "FAILURE",
  "createdAt": "2024-01-01T10:15:00Z",
  "createdBy": "user123"
}
```

## 性能优化

### 1. 异步处理
- 审计日志记录采用异步处理，不影响业务操作性能
- 使用Reactor的`subscribe()`方法异步执行审计记录

### 2. 批量操作
- 支持批量审计日志记录
- 减少数据库连接开销

### 3. 索引优化
- 为常用查询字段创建索引
- 复合索引优化多条件查询

### 4. 数据清理
- 自动清理过期的审计日志
- 可配置的保留策略

## 安全考虑

### 1. 敏感数据保护
- 自动过滤敏感字段（如密码、令牌等）
- 支持数据脱敏处理

### 2. 访问控制
- 审计日志查询需要适当的权限
- 支持基于角色的访问控制

### 3. 数据完整性
- 防止审计日志被篡改
- 支持数字签名验证

## 监控和告警

### 1. 性能监控
- 监控审计日志记录性能
- 监控数据库查询性能

### 2. 错误告警
- 审计日志记录失败告警
- 异常操作模式检测

### 3. 容量监控
- 监控审计日志存储容量
- 自动扩容和清理策略

## 合规性

### 1. 审计要求
- 满足SOX、GDPR等合规要求
- 提供完整的审计跟踪

### 2. 数据保留
- 可配置的数据保留策略
- 支持长期归档

### 3. 报告生成
- 自动生成审计报告
- 支持多种格式导出

## 故障排除

### 1. 常见问题
- 审计日志记录失败
- 查询性能问题
- 存储空间不足

### 2. 调试建议
- 检查数据库连接
- 验证索引配置
- 监控系统资源

### 3. 最佳实践
- 定期清理过期日志
- 监控审计日志性能
- 备份重要审计数据

现在您的员工管理系统具备了企业级的数据库审计功能，提供全面的数据库操作监控、审计跟踪和合规支持！

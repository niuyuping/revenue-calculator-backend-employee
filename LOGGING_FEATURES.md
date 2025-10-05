# 日志功能说明

## 概述

本系统已成功集成了完整的日志功能，包括结构化日志、审计日志、性能日志、安全日志等，提供全面的系统监控和审计能力。

## 日志架构

### 1. 日志分类

#### 应用日志 (Application Logs)
- **位置**: `logs/revenue-calculator-employee.log`
- **内容**: 应用程序运行日志
- **格式**: JSON 结构化格式
- **保留**: 30天，最大100MB/文件，总计3GB

#### 审计日志 (Audit Logs)
- **位置**: `logs/revenue-calculator-employee-audit.log`
- **内容**: 用户操作、数据访问、业务操作审计
- **格式**: JSON 结构化格式
- **保留**: 90天，最大100MB/文件，总计5GB

#### 安全日志 (Security Logs)
- **位置**: `logs/revenue-calculator-employee-security.log`
- **内容**: 安全事件、认证失败、权限违规
- **格式**: JSON 结构化格式
- **保留**: 365天，最大100MB/文件，总计10GB

#### 性能日志 (Performance Logs)
- **位置**: `logs/revenue-calculator-employee-performance.log`
- **内容**: 性能监控、响应时间、资源使用
- **格式**: JSON 结构化格式
- **保留**: 30天，最大100MB/文件，总计3GB

#### 错误日志 (Error Logs)
- **位置**: `logs/revenue-calculator-employee-error.log`
- **内容**: 系统错误、异常、故障
- **格式**: JSON 结构化格式
- **保留**: 90天，最大100MB/文件，总计5GB

### 2. 日志配置

#### Logback 配置
```xml
<!-- 结构化JSON日志配置 -->
<encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
    <providers>
        <timestamp/>
        <logLevel/>
        <loggerName/>
        <message/>
        <mdc/>
        <stackTrace/>
        <pattern>
            <pattern>
                {
                    "service": "${APP_NAME}",
                    "environment": "${spring.profiles.active:-default}",
                    "version": "1.0.0"
                }
            </pattern>
        </pattern>
    </providers>
</encoder>
```

#### 环境特定配置
- **开发环境**: DEBUG级别，详细日志
- **测试环境**: INFO级别，简化日志
- **生产环境**: INFO级别，优化性能

## 审计日志功能

### 1. AuditLogService

提供以下审计日志记录功能：

#### 用户操作审计
```java
auditLogService.logUserOperation(
    "CREATE",           // 操作类型
    "Employee",         // 资源类型
    "123",             // 资源ID
    "user123",         // 用户ID
    details            // 详细信息
);
```

#### 数据访问审计
```java
auditLogService.logDataAccess(
    "UPDATE",          // 操作类型 (CREATE, READ, UPDATE, DELETE)
    "employees",       // 表名
    "123",            // 记录ID
    "user123",        // 用户ID
    oldValues,        // 旧值
    newValues         // 新值
);
```

#### 安全事件审计
```java
auditLogService.logSecurityEvent(
    "LOGIN_FAILED",    // 事件类型
    "HIGH",           // 严重程度
    "登录失败次数过多", // 描述
    "user123",        // 用户ID
    details           // 详细信息
);
```

#### 性能监控审计
```java
auditLogService.logPerformance(
    "CREATE_EMPLOYEE", // 操作名称
    150,              // 执行时间（毫秒）
    "SUCCESS",        // 状态
    details           // 详细信息
);
```

#### API调用审计
```java
auditLogService.logApiCall(
    "POST",           // HTTP方法
    "/api/v1/employee", // 请求URI
    201,              // 响应状态码
    200,              // 执行时间（毫秒）
    "user123",        // 用户ID
    1024,             // 请求大小
    512               // 响应大小
);
```

### 2. 审计日志格式

#### 用户操作审计日志
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "operation": "CREATE",
  "resource": "Employee",
  "resourceId": "123",
  "userId": "user123",
  "sessionId": "session-456",
  "requestId": "request-789",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "details": {
    "employeeNumber": "EMP001",
    "name": "测试员工"
  }
}
```

#### 数据访问审计日志
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "operation": "UPDATE",
  "table": "employees",
  "recordId": "123",
  "userId": "user123",
  "sessionId": "session-456",
  "requestId": "request-789",
  "ipAddress": "192.168.1.100",
  "oldValues": {
    "name": "旧名称"
  },
  "newValues": {
    "name": "新名称"
  }
}
```

## 请求日志功能

### 1. RequestLoggingFilter

自动记录所有HTTP请求的详细信息：

#### 请求开始日志
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "requestId": "request-123",
  "sessionId": "session-456",
  "method": "POST",
  "uri": "/api/v1/employee",
  "path": "/api/v1/employee",
  "queryParams": {},
  "headers": {
    "Content-Type": "application/json",
    "User-Agent": "Mozilla/5.0..."
  },
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

#### 请求结束日志
```json
{
  "timestamp": "2024-01-01T10:00:01Z",
  "requestId": "request-123",
  "sessionId": "session-456",
  "method": "POST",
  "uri": "/api/v1/employee",
  "path": "/api/v1/employee",
  "statusCode": 201,
  "duration": 150,
  "success": true,
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0..."
}
```

### 2. MDC (Mapped Diagnostic Context)

自动设置以下MDC字段：
- `requestId`: 请求唯一标识
- `sessionId`: 会话标识
- `ipAddress`: 客户端IP地址
- `userAgent`: 用户代理

## 日志监控功能

### 1. LogMonitoringService

提供日志统计和监控功能：

#### 监控指标
- `logs.audit` - 审计日志数量
- `logs.security` - 安全日志数量
- `logs.performance` - 性能日志数量
- `logs.error` - 错误日志数量
- `logs.request` - 请求日志数量
- `logs.processing.duration` - 日志处理时间

#### 统计信息
```java
LogStats stats = logMonitoringService.getLogStats();
// 返回：
// - 总日志数量
// - 总错误数量
// - 错误率
// - 各类型日志数量
// - 平均处理时间
```

#### 健康状态
```java
LogHealthStatus health = logMonitoringService.getLogHealthStatus();
// 返回：
// - 健康状态 (HEALTHY, WARNING, CRITICAL)
// - 错误率
// - 总日志数量
// - 总错误数量
```

### 2. 健康状态评估

- **HEALTHY**: 错误率 < 1%
- **WARNING**: 错误率 1% - 5%
- **CRITICAL**: 错误率 > 5%

## API 端点

### 日志监控端点
```
GET /api/v1/monitoring/logs/stats
```
返回日志统计信息

```
GET /api/v1/monitoring/logs/health
```
返回日志健康状态

```
POST /api/v1/monitoring/logs/reset
```
重置日志统计信息

### 缓存监控端点
```
GET /api/v1/monitoring/cache/stats
```
返回缓存统计信息

```
DELETE /api/v1/monitoring/cache/clear
```
清除所有缓存

### 事务监控端点
```
GET /api/v1/monitoring/transaction/stats
```
返回事务统计信息

## 使用示例

### 1. 在服务中使用审计日志
```java
@Service
public class EmployeeService {
    
    @Autowired
    private AuditLogService auditLogService;
    
    public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
        return employeeRepository.save(employee)
                .map(this::convertToDto)
                .doOnSuccess(createdEmployee -> {
                    // 记录数据访问审计
                    Map<String, Object> details = new HashMap<>();
                    details.put("employeeNumber", createdEmployee.getEmployeeNumber());
                    details.put("name", createdEmployee.getName());
                    
                    auditLogService.logDataAccess("CREATE", "employees", 
                            createdEmployee.getEmployeeId().toString(), 
                            "system", null, details);
                    
                    // 记录业务操作审计
                    auditLogService.logBusinessOperation("CREATE_EMPLOYEE", "Employee", 
                            createdEmployee.getEmployeeId().toString(), 
                            "system", "SUCCESS", details);
                })
                .doOnError(error -> {
                    // 记录错误审计
                    Map<String, Object> errorDetails = new HashMap<>();
                    errorDetails.put("errorMessage", error.getMessage());
                    errorDetails.put("errorType", error.getClass().getSimpleName());
                    
                    auditLogService.logError("CREATE_EMPLOYEE_FAILED", "EmployeeService", 
                            "Failed to create employee", "system", errorDetails);
                });
    }
}
```

### 2. 监控日志统计
```java
@RestController
public class MonitoringController {
    
    @Autowired
    private LogMonitoringService logMonitoringService;
    
    @GetMapping("/logs/stats")
    public Mono<LogStats> getLogStats() {
        return Mono.just(logMonitoringService.getLogStats());
    }
    
    @GetMapping("/logs/health")
    public Mono<LogHealthStatus> getLogHealthStatus() {
        return Mono.just(logMonitoringService.getLogHealthStatus());
    }
}
```

## 日志分析

### 1. 结构化日志优势
- **易于解析**: JSON格式便于日志分析工具处理
- **字段标准化**: 统一的字段结构便于查询和统计
- **上下文信息**: 包含请求ID、会话ID等上下文信息
- **可扩展性**: 易于添加新的字段和属性

### 2. 日志查询示例
```bash
# 查询特定用户的审计日志
grep '"userId":"user123"' logs/revenue-calculator-employee-audit.log

# 查询错误日志
grep '"level":"ERROR"' logs/revenue-calculator-employee-error.log

# 查询特定操作的性能日志
grep '"operation":"CREATE_EMPLOYEE"' logs/revenue-calculator-employee-performance.log

# 查询安全事件
grep '"eventType":"LOGIN_FAILED"' logs/revenue-calculator-employee-security.log
```

### 3. 日志分析工具
- **ELK Stack**: Elasticsearch + Logstash + Kibana
- **Fluentd**: 日志收集和转发
- **Prometheus + Grafana**: 指标监控和可视化
- **Splunk**: 企业级日志分析平台

## 性能优化

### 1. 异步日志
- 使用异步Appender减少I/O阻塞
- 批量写入提高性能
- 内存缓冲优化

### 2. 日志级别控制
- 生产环境使用INFO级别
- 开发环境使用DEBUG级别
- 动态调整日志级别

### 3. 日志轮转
- 按大小和时间轮转
- 自动压缩历史日志
- 定期清理过期日志

## 安全考虑

### 1. 敏感信息过滤
- 自动过滤密码、令牌等敏感信息
- 脱敏处理个人隐私数据
- 审计日志访问权限控制

### 2. 日志完整性
- 防止日志篡改
- 数字签名验证
- 备份和恢复机制

### 3. 合规性
- 满足审计要求
- 数据保留政策
- 隐私保护法规

## 故障排除

### 1. 常见问题
- **日志文件过大**: 检查轮转配置
- **日志丢失**: 检查磁盘空间和权限
- **性能影响**: 调整日志级别和异步配置
- **格式错误**: 检查JSON序列化配置

### 2. 调试建议
- 启用DEBUG级别查看详细日志
- 检查MDC设置是否正确
- 验证日志配置和路径
- 监控日志处理性能

## 最佳实践

### 1. 日志记录原则
1. **结构化**: 使用JSON格式记录结构化日志
2. **上下文**: 包含足够的上下文信息
3. **级别**: 合理使用日志级别
4. **性能**: 避免影响业务性能
5. **安全**: 保护敏感信息

### 2. 审计日志原则
1. **完整性**: 记录所有重要操作
2. **准确性**: 确保日志信息准确
3. **及时性**: 实时记录操作日志
4. **可追溯**: 支持操作追溯
5. **合规性**: 满足审计要求

现在您的员工管理系统具备了企业级的日志功能，提供全面的系统监控、审计和故障排除能力！

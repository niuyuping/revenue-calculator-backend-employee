# 事务功能说明

## 概述

本系统已成功集成了完整的事务支持，确保数据的一致性和完整性。事务功能基于 Spring 的响应式事务管理，使用 R2DBC 作为数据库连接层。

## 功能特性

### 1. 事务管理器配置
- **R2DBC 响应式事务管理器**: 支持响应式编程模式
- **自动事务管理**: 通过 `@Transactional` 注解自动管理事务
- **事务操作器**: 提供编程式事务控制

### 2. 事务注解支持
以下方法已添加事务支持：
- `createEmployee()` - 创建员工
- `updateEmployee()` - 更新员工信息
- `deleteEmployeeById()` - 按ID删除员工
- `deleteEmployeeByNumber()` - 按员工号删除员工

### 3. 事务监控和日志
- **实时监控**: 跟踪事务开始、提交、回滚和错误
- **性能指标**: 记录事务执行时间
- **详细日志**: 记录事务操作的详细信息
- **统计信息**: 提供事务统计数据的API端点

### 4. 异常处理
- **事务异常类**: `TransactionException` 用于处理事务相关异常
- **全局异常处理**: 在 `GlobalExceptionHandler` 中处理事务异常
- **自动回滚**: 异常发生时自动回滚事务

## API 端点

### 事务监控端点
```
GET /api/v1/monitoring/transaction/stats
```
返回事务统计信息，包括：
- 事务开始次数
- 事务提交次数
- 事务回滚次数
- 事务错误次数
- 平均执行时间

## 配置类

### TransactionConfig
```java
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
    
    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}
```

## 监控服务

### TransactionMonitoringService
提供以下功能：
- 记录事务开始时间
- 监控事务提交和回滚
- 记录事务错误
- 提供统计信息
- 包装事务操作进行自动监控

## 使用示例

### 1. 创建员工（带事务）
```java
@Transactional
public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
    return transactionMonitoringService.monitorTransaction(
        "CREATE_EMPLOYEE", 
        "Employee number: " + employeeDto.getEmployeeNumber(),
        // 事务操作逻辑
        employeeRepository.existsByEmployeeNumber(employeeDto.getEmployeeNumber())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new DuplicateEmployeeNumberException("员工号已存在"));
                }
                return employeeRepository.save(convertToEntity(employeeDto));
            })
            .map(this::convertToDto)
    );
}
```

### 2. 获取事务统计信息
```java
@GetMapping("/api/v1/monitoring/transaction/stats")
public Mono<TransactionStats> getTransactionStats() {
    return Mono.just(transactionMonitoringService.getTransactionStats());
}
```

## 测试覆盖

### 单元测试
- `TransactionConfigTest` - 事务配置测试
- `TransactionExceptionTest` - 事务异常测试
- `TransactionMonitoringServiceTest` - 事务监控服务测试

### 集成测试
- `TransactionIntegrationTest` - 事务功能集成测试
  - 事务提交测试
  - 事务回滚测试
  - 异常处理测试

## 监控指标

系统提供以下 Micrometer 指标：
- `transaction.start` - 事务开始次数
- `transaction.commit` - 事务提交次数
- `transaction.rollback` - 事务回滚次数
- `transaction.error` - 事务错误次数
- `transaction.duration` - 事务执行时间

## 日志格式

### 事务开始日志
```
INFO - 事务开始 - 操作: CREATE_EMPLOYEE, 详情: Employee number: EMP001, 时间: 2024-01-01T10:00:00Z
```

### 事务提交日志
```
INFO - 事务提交成功 - 操作: CREATE_EMPLOYEE, 详情: Employee number: EMP001, 耗时: 150ms
```

### 事务回滚日志
```
WARN - 事务回滚 - 操作: CREATE_EMPLOYEE, 原因: 员工号已存在, 耗时: 50ms
```

### 事务错误日志
```
ERROR - 事务错误 - 操作: CREATE_EMPLOYEE, 错误: 数据库连接失败, 耗时: 200ms
```

## 最佳实践

1. **事务边界**: 在服务层方法上使用 `@Transactional` 注解
2. **异常处理**: 让业务异常自动触发事务回滚
3. **监控**: 使用 `TransactionMonitoringService` 包装重要的事务操作
4. **日志**: 记录关键的事务操作和结果
5. **测试**: 编写完整的事务测试用例

## 注意事项

1. 事务注解只对 public 方法有效
2. 响应式事务需要返回 `Mono` 或 `Flux` 类型
3. 事务监控会增加少量性能开销
4. 建议在生产环境中启用事务监控
5. 定期检查事务统计信息以识别性能问题

## 故障排除

### 常见问题
1. **事务不生效**: 检查方法是否为 public，是否在同一个类中调用
2. **监控数据不准确**: 确保 `TransactionMonitoringService` 正确配置
3. **性能问题**: 检查事务执行时间指标，优化慢查询

### 调试建议
1. 启用 DEBUG 级别日志查看事务详情
2. 使用 `/api/v1/monitoring/transaction/stats` 端点查看统计信息
3. 检查 Micrometer 指标确认事务行为

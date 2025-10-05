# 缓存和限流功能说明

## 概述

本系统已成功集成了完整的缓存和限流功能，以提升系统性能和保护系统稳定性。缓存功能基于 Redis，限流功能基于 Resilience4j。

## 缓存功能

### 1. 缓存配置

#### CacheConfig
- **Redis 缓存管理器**: 使用 Redis 作为缓存存储
- **多级缓存策略**: 不同数据类型使用不同的过期时间
- **序列化配置**: 使用 JSON 序列化存储复杂对象
- **缓存键生成器**: 统一的缓存键生成策略

#### 缓存策略
```java
// 员工信息缓存 - 1小时过期
cacheConfigurations.put("employees", defaultConfig
        .entryTtl(Duration.ofHours(1))
        .prefixCacheNameWith("employee:"));

// 员工列表缓存 - 30分钟过期
cacheConfigurations.put("employeeList", defaultConfig
        .entryTtl(Duration.ofMinutes(30))
        .prefixCacheNameWith("employee_list:"));

// 员工搜索缓存 - 15分钟过期
cacheConfigurations.put("employeeSearch", defaultConfig
        .entryTtl(Duration.ofMinutes(15))
        .prefixCacheNameWith("employee_search:"));

// 分页缓存 - 10分钟过期
cacheConfigurations.put("employeePagination", defaultConfig
        .entryTtl(Duration.ofMinutes(10))
        .prefixCacheNameWith("employee_pagination:"));
```

### 2. 缓存注解

#### 查询方法缓存
```java
@Cacheable(value = "employees", key = "#id")
public Mono<EmployeeDto> getEmployeeById(Long id) {
    // 查询逻辑
}

@Cacheable(value = "employees", key = "'number:' + #employeeNumber")
public Mono<EmployeeDto> getEmployeeByNumber(String employeeNumber) {
    // 查询逻辑
}

@Cacheable(value = "employeePagination", key = "#pageRequest.page + '_' + #pageRequest.size + '_' + #pageRequest.sortBy + '_' + #pageRequest.sortDirection")
public Mono<PageResponse<EmployeeDto>> getAllEmployeesWithPagination(PageRequest pageRequest) {
    // 分页查询逻辑
}
```

#### 修改方法缓存清除
```java
@Transactional
@CacheEvict(value = {"employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
public Mono<EmployeeDto> createEmployee(EmployeeDto employeeDto) {
    // 创建逻辑
}

@Transactional
@CacheEvict(value = {"employees", "employeeList", "employeePagination", "employeeSearch"}, allEntries = true)
public Mono<EmployeeDto> updateEmployee(Long id, EmployeeDto employeeDto) {
    // 更新逻辑
}
```

### 3. 缓存监控

#### CacheMonitoringService
提供以下功能：
- 缓存命中/未命中统计
- 缓存操作时间监控
- 缓存清除统计
- 实时缓存状态查询

#### 监控指标
- `cache.hit` - 缓存命中次数
- `cache.miss` - 缓存未命中次数
- `cache.evict` - 缓存清除次数
- `cache.put` - 缓存存储次数
- `cache.operation.duration` - 缓存操作执行时间

## 限流功能

### 1. 限流配置

#### RateLimitConfig
配置了多个限流器，针对不同的 API 端点：

```java
// 员工API限流器 - 每分钟100次请求
@Bean("employee-api")
public RateLimiter employeeApiRateLimiter(RateLimiterRegistry registry) {
    RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(100)
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .timeoutDuration(Duration.ofSeconds(1))
            .build();
    return registry.rateLimiter("employee-api", config);
}
```

### 2. 限流策略

| 限流器名称 | 限制 | 时间窗口 | 等待时间 | 适用场景 |
|-----------|------|----------|----------|----------|
| employee-api | 100次 | 1分钟 | 1秒 | 一般查询操作 |
| employee-search | 50次 | 1分钟 | 1秒 | 搜索操作 |
| employee-create | 20次 | 1分钟 | 2秒 | 创建操作 |
| employee-update | 30次 | 1分钟 | 1秒 | 更新操作 |
| employee-delete | 10次 | 1分钟 | 2秒 | 删除操作 |
| employee-pagination | 200次 | 1分钟 | 1秒 | 分页查询 |
| monitoring-api | 10次 | 1分钟 | 1秒 | 监控接口 |
| global-api | 1000次 | 1分钟 | 1秒 | 全局限制 |

### 3. 限流注解

#### 控制器方法限流
```java
@GetMapping
@RateLimiter(name = "employee-pagination")
public Flux<EmployeeDto> getAllEmployees() {
    // 查询逻辑
}

@PostMapping
@RateLimiter(name = "employee-create")
public Mono<ResponseEntity<EmployeeDto>> createEmployee(@RequestBody @Valid EmployeeDto employeeDto) {
    // 创建逻辑
}

@PutMapping("/{id}")
@RateLimiter(name = "employee-update")
public Mono<ResponseEntity<EmployeeDto>> updateEmployee(@PathVariable Long id, @RequestBody @Valid EmployeeDto employeeDto) {
    // 更新逻辑
}

@DeleteMapping("/{id}")
@RateLimiter(name = "employee-delete")
public Mono<ResponseEntity<Void>> deleteEmployeeById(@PathVariable Long id) {
    // 删除逻辑
}
```

## API 端点

### 缓存监控端点
```
GET /api/v1/monitoring/cache/stats
```
返回缓存统计信息，包括：
- 各缓存的命中率
- 操作次数统计
- 平均操作时间

```
DELETE /api/v1/monitoring/cache/clear
```
清除所有缓存

```
DELETE /api/v1/monitoring/cache/clear/{cacheName}
```
清除指定缓存

### 事务监控端点
```
GET /api/v1/monitoring/transaction/stats
```
返回事务统计信息

## 使用示例

### 1. 缓存使用
```java
// 查询员工 - 自动使用缓存
EmployeeDto employee = employeeService.getEmployeeById(1L).block();

// 分页查询 - 自动使用缓存
PageRequest pageRequest = new PageRequest(0, 10, "name", SortDirection.ASC);
PageResponse<EmployeeDto> page = employeeService.getAllEmployeesWithPagination(pageRequest).block();

// 搜索 - 自动使用缓存
PageResponse<EmployeeDto> searchResult = employeeService.searchEmployeesByNameWithPagination("田中", pageRequest).block();
```

### 2. 缓存管理
```java
// 获取缓存统计
CacheStats stats = cacheMonitoringService.getCacheStats();
System.out.println("缓存命中率: " + stats.getHitRate());

// 清除所有缓存
cacheMonitoringService.clearAllCaches();

// 清除指定缓存
cacheMonitoringService.clearCache("employees");
```

### 3. 限流测试
```bash
# 正常请求
curl -X GET "http://localhost:8080/api/v1/employee/1"

# 超过限流限制时的响应
# HTTP 429 Too Many Requests
```

## 性能优化

### 1. 缓存优化
- **分层缓存**: 不同数据类型使用不同的过期时间
- **智能键生成**: 避免键冲突，提高缓存效率
- **自动清除**: 数据变更时自动清除相关缓存
- **监控统计**: 实时监控缓存性能

### 2. 限流优化
- **分级限流**: 不同操作类型使用不同的限流策略
- **快速失败**: 超过限制时快速返回，避免资源浪费
- **监控统计**: 实时监控限流状态

## 监控和日志

### 1. 缓存日志
```
DEBUG - 缓存命中 - 缓存: employees, 键: 1
DEBUG - 缓存未命中 - 缓存: employees, 键: 2
DEBUG - 缓存存储 - 缓存: employees, 键: 1
DEBUG - 缓存清除 - 缓存: employees, 键: 1
```

### 2. 限流日志
```
WARN - 限流触发 - 限流器: employee-create, 当前请求数: 21, 限制: 20
INFO - 限流通过 - 限流器: employee-search, 当前请求数: 15, 限制: 50
```

### 3. 性能指标
- 缓存命中率
- 平均缓存操作时间
- 限流触发次数
- API 响应时间

## 最佳实践

### 1. 缓存最佳实践
1. **合理设置过期时间**: 根据数据更新频率设置合适的过期时间
2. **避免缓存雪崩**: 使用随机过期时间
3. **监控缓存性能**: 定期检查缓存命中率
4. **及时清除缓存**: 数据变更时及时清除相关缓存

### 2. 限流最佳实践
1. **合理设置限制**: 根据系统容量和业务需求设置合理的限流值
2. **分级限流**: 对不同重要性的操作设置不同的限流策略
3. **监控限流状态**: 实时监控限流触发情况
4. **优雅降级**: 超过限制时提供友好的错误信息

## 故障排除

### 1. 缓存问题
- **缓存不生效**: 检查 Redis 连接和配置
- **缓存命中率低**: 检查缓存键生成和过期时间设置
- **内存使用过高**: 检查缓存大小和过期策略

### 2. 限流问题
- **限流不生效**: 检查限流器配置和注解
- **误触发限流**: 检查限流值设置是否合理
- **性能影响**: 检查限流等待时间设置

## 配置参数

### 1. 缓存配置
```properties
# Redis 配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0

# 缓存配置
spring.cache.type=redis
spring.cache.redis.time-to-live=1800000
```

### 2. 限流配置
```properties
# Resilience4j 配置
resilience4j.ratelimiter.instances.employee-api.limit-for-period=100
resilience4j.ratelimiter.instances.employee-api.limit-refresh-period=60s
resilience4j.ratelimiter.instances.employee-api.timeout-duration=1s
```

## 测试覆盖

### 1. 单元测试
- `CacheConfigTest` - 缓存配置测试
- `RateLimitConfigTest` - 限流配置测试
- `CacheMonitoringServiceTest` - 缓存监控服务测试

### 2. 集成测试
- `CacheAndRateLimitIntegrationTest` - 缓存和限流集成测试
- 缓存命中/未命中测试
- 限流触发测试
- 缓存清除测试

现在您的员工管理系统具备了企业级的缓存和限流功能，能够有效提升系统性能并保护系统稳定性！

# 员工管理微服务 (Revenue Calculator Backend Employee)

基于 Spring Boot 3.x、R2DBC、WebFlux 的响应式员工管理系统后端服务，提供全面的企业级功能。

## 🌐 语言选择

- 🇨🇳 **中文版** - 完整的中文文档（本文件）
- 🇺🇸 [English Version](README_EN.md) - Complete English documentation
- 🇯🇵 [日本語版 (Japanese)](README_JA.md) - 完全な日本語文書

---

## 🚀 技术栈

- **Java 21** - 编程语言
- **Spring Boot 3.5.6** - 应用框架
- **Spring WebFlux** - 响应式Web框架
- **Spring Data R2DBC** - 响应式数据库访问
- **PostgreSQL** - 关系型数据库
- **Flyway** - 数据库迁移工具
- **Jakarta Validation** - 数据验证
- **Spring Boot Actuator** - 应用监控
- **Resilience4j** - 限流和熔断
- **Swagger/OpenAPI 3** - API文档
- **Gradle** - 构建工具
- **JUnit 5** - 测试框架
- **Mockito** - Mock测试框架
- **TestContainers** - 集成测试

## 📋 功能特性

### 核心功能

- ✅ **员工CRUD操作** - 员工信息的创建、读取、更新、删除
- ✅ **员工搜索** - 支持按姓名搜索
- ✅ **分页查询** - 支持分页和排序的员工列表查询
- ✅ **数据验证** - 完整的输入数据验证和约束
- ✅ **异常处理** - 统一的异常处理和错误响应
- ✅ **响应式编程** - 完全非阻塞响应式架构
- ✅ **API限流** - Resilience4j限流保护
- ✅ **监控指标** - 完整的业务和性能监控
- ✅ **API文档** - 完整的Swagger/OpenAPI文档

### 企业级功能

#### 🔄 限流保护

- **限流保护**: 不同操作类型设置不同限制（20-100请求/分钟）


#### 🔄 事务管理

- **ACID合规**: 基于R2DBC的事务支持
- **自动事务管理**: `@Transactional`注解支持
- **事务监控**: 实时事务跟踪
- **性能指标**: 事务执行时间监控


### 数据验证

- 员工编号格式验证（字母、数字、下划线、连字符）
- 姓名长度验证（1-100字符）
- 假名格式验证（平假名、片假名、拉丁字母、空格、括号）
- 生日验证（必须是过去的日期）
- 路径参数和查询参数验证

### 监控和健康检查

- Spring Boot Actuator集成
- 健康检查端点
- Flyway迁移状态监控
- 应用信息端点
- 自定义业务指标
- 性能监控指标

## 🏗️ 项目结构

```text
src/
├── main/
│   ├── java/jp/asatex/revenue_calculator_backend_employee/
│   │   ├── config/           # 配置类
│   │   │   ├── CacheConfig.java
│   │   │   ├── JacksonConfig.java
│   │   │   ├── MetricsConfig.java
│   │   │   ├── RateLimitConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   ├── TransactionConfig.java
│   │   │   ├── ValidationConfig.java
│   │   │   ├── WebFluxConfig.java
│   │   │   └── WebFluxJacksonConfig.java
│   │   ├── controller/       # REST控制器
│   │   │   ├── CacheMonitoringController.java
│   │   │   ├── EmployeeController.java
│   │   │   └── TransactionMonitoringController.java
│   │   ├── dto/             # 数据传输对象
│   │   │   ├── EmployeeDto.java
│   │   │   ├── PageRequest.java
│   │   │   ├── PageResponse.java
│   │   │   └── SortDirection.java
│   │   ├── entity/          # 实体类
│   │   │   └── Employee.java
│   │   ├── exception/       # 异常处理
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   ├── DuplicateEmployeeNumberException.java
│   │   │   └── TransactionException.java
│   │   ├── repository/      # 数据访问层
│   │   │   └── EmployeeRepository.java
│   │   ├── service/         # 业务逻辑层
│   │   │   ├── EmployeeService.java
│   │   │   ├── CacheMonitoringService.java
│   │   │   └── TransactionMonitoringService.java
│   │   └── util/            # 工具类
│   │   └── RevenueCalculatorBackendEmployeeApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       ├── messages.properties          # 国际化资源文件
│       └── db/migration/    # 数据库迁移脚本
│           ├── V1__Create_employees_table.sql
│           ├── V2__Insert_initial_employee_data.sql
│           ├── V3__Add_constraints_to_employees_table.sql
│           └── V4__Add_soft_delete_columns.sql
└── test/                    # 测试代码
    ├── java/jp/asatex/revenue_calculator_backend_employee/
    │   ├── config/          # 配置测试
    │   ├── controller/      # 控制器测试
    │   ├── exception/       # 异常处理测试
    │   ├── integration/     # 集成测试
    │   ├── repository/      # 数据访问测试
    │   ├── service/         # 业务逻辑测试
    │   ├── util/            # 工具类测试
    │   └── validation/      # 验证测试
    └── resources/
        └── application-test.properties
```

## 🚀 快速开始

### 前提条件

- Java 21+
- PostgreSQL 12+
- Gradle 8.0+

### 安装和运行

1. **克隆项目**

   ```bash
   git clone https://github.com/niuyuping/revenue-calculator-backend-employee.git
   cd revenue-calculator-backend-employee
   ```

2. **数据库设置**

   ```bash
   # 创建PostgreSQL数据库
   createdb asatex-revenue
   ```


4. **应用配置**

   编辑 `src/main/resources/application.properties`:

   ```properties
   # 数据库配置
   spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
   spring.r2dbc.username=your_username
   spring.r2dbc.password=your_password
   
   
   # Flyway配置
   spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
   spring.flyway.user=your_username
   spring.flyway.password=your_password
   ```

5. **运行应用**

   ```bash
   ./gradlew bootRun
   ```

6. **验证运行**

   ```bash
   curl http://localhost:9001/api/v1/employee/health
   ```

## 📚 API文档

### Swagger UI访问

启动应用后，可以通过以下链接访问Swagger UI：

- **Swagger UI**: <http://localhost:9001/swagger-ui.html>
- **OpenAPI JSON**: <http://localhost:9001/v3/api-docs>
- **Swagger配置**: <http://localhost:9001/v3/api-docs/swagger-config>

### 🌐 API文档特性

- **完整的Swagger/OpenAPI 3文档**
- **交互式API测试界面**
- **详细的请求/响应示例**
- **参数验证说明**
- **错误代码说明**

### 基础URL

```text
http://localhost:9001/api/v1/employee
```

### 端点列表

#### 1. 获取所有员工

```http
GET /api/v1/employee
```

**响应示例:**

```json
[
  {
    "employeeId": 1,
    "employeeNumber": "EMP001",
    "name": "田中太郎",
    "furigana": "タナカタロウ",
    "birthday": "1990-05-15"
  }
]
```

#### 2. 根据ID获取员工

```http
GET /api/v1/employee/{id}
```

**参数:**

- `id` (路径参数): 员工ID (必须是正数)

#### 3. 根据员工编号获取员工

```http
GET /api/v1/employee/number/{employeeNumber}
```

**参数:**

- `employeeNumber` (路径参数): 员工编号 (1-20字符，英数字下划线连字符)

#### 4. 根据姓名搜索员工

```http
GET /api/v1/employee/search/name?name={name}
```

**参数:**

- `name` (查询参数): 姓名关键词 (1-100字符)

#### 5. 分页获取员工列表

```http
GET /api/v1/employee/paged?page={page}&size={size}&sortBy={sortBy}&sortDirection={sortDirection}
```

**参数:**

- `page` (查询参数): 页码，从0开始 (默认: 0)
- `size` (查询参数): 每页大小 (默认: 10, 最大: 100)
- `sortBy` (查询参数): 排序字段 (默认: employeeId)
- `sortDirection` (查询参数): 排序方向 (ASC/DESC, 默认: ASC)

#### 6. 创建员工

```http
POST /api/v1/employee
Content-Type: application/json
```

**请求体:**

```json
{
  "employeeNumber": "EMP001",
  "name": "田中太郎",
  "furigana": "タナカタロウ",
  "birthday": "1990-05-15"
}
```

#### 7. 更新员工

```http
PUT /api/v1/employee/{id}
Content-Type: application/json
```

#### 8. 根据ID删除员工

```http
DELETE /api/v1/employee/{id}
```

#### 9. 根据员工编号删除员工

```http
DELETE /api/v1/employee/number/{employeeNumber}
```

#### 10. 健康检查

```http
GET /api/v1/employee/health
```

### 监控端点

#### 缓存监控

```http
GET /api/v1/monitoring/cache/stats
DELETE /api/v1/monitoring/cache/clear
DELETE /api/v1/monitoring/cache/clear/{cacheName}
```


#### 事务监控

```http
GET /api/v1/monitoring/transaction/stats
```


### 错误响应格式

```json
{
  "error": "错误类型",
  "message": "错误描述",
  "details": "详细错误信息",
  "status": 400
}
```

### HTTP状态码

- `200 OK` - 请求成功
- `201 Created` - 资源创建成功
- `204 No Content` - 删除成功
- `400 Bad Request` - 请求参数错误
- `404 Not Found` - 资源不存在
- `409 Conflict` - 资源冲突（员工编号重复等）
- `429 Too Many Requests` - 请求过于频繁（限流）
- `500 Internal Server Error` - 服务器内部错误

## 🧪 测试

### 运行所有测试

```bash
./gradlew test
```

### 运行特定测试类

```bash
./gradlew test --tests "EmployeeServiceTest"
./gradlew test --tests "EmployeeRepositoryTest"
./gradlew test --tests "EmployeeIntegrationTest"
```

### 测试覆盖率

项目具有全面的测试覆盖率：

- **Service层测试** - 业务逻辑测试
- **Repository层测试** - 数据访问测试
- **Controller层测试** - API端点测试
- **异常处理测试** - 错误处理测试
- **参数验证测试** - 输入验证测试
- **集成测试** - 端到端测试
- **配置测试** - 配置类测试
- **缓存测试** - 缓存功能测试
- **限流测试** - 限流功能测试
- **事务测试** - 事务管理测试

## 🔧 配置

### 应用配置 (application.properties)

```properties
# 服务器配置
# 开发环境使用9001端口，生产环境使用8080端口（通过环境变量PORT设置）
server.port=9001

# 数据库配置
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
spring.r2dbc.username=db_user
spring.r2dbc.password=${DB_PASSWORD}


# Flyway配置
spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
spring.flyway.user=db_user
spring.flyway.password=${DB_PASSWORD}
spring.flyway.baseline-on-migrate=true


# 限流配置
resilience4j.ratelimiter.instances.employee-api.limit-for-period=100
resilience4j.ratelimiter.instances.employee-search.limit-for-period=50
resilience4j.ratelimiter.instances.employee-write.limit-for-period=20

# Actuator配置
management.endpoints.web.exposure.include=health,info,flyway,metrics
management.endpoint.health.show-details=when-authorized
management.endpoint.flyway.enabled=true

# 日志配置
logging.level.jp.asatex.revenue_calculator_backend_employee=INFO
logging.file.name=logs/revenue-calculator-employee.log
```

### 环境变量

**开发环境环境变量:**
- `DB_PASSWORD` - 数据库密码

**生产环境环境变量:**
- `PORT` - 服务器端口 (默认: 8080)
- `DB_URL` - 数据库连接URL
- `DB_USER` - 数据库用户名
- `DB_PASSWORD` - 数据库密码
- `FLYWAY_URL` - Flyway数据库连接URL
- `DB_POOL_MAX_SIZE` - 数据库连接池最大大小 (默认: 10)
- `DB_POOL_MAX_IDLE_TIME` - 连接池最大空闲时间 (默认: PT10M)
- `DB_POOL_MAX_LIFE_TIME` - 连接池最大生存时间 (默认: PT30M)
- `DB_POOL_INITIAL_SIZE` - 连接池初始大小 (默认: 2)

### 生产环境配置

生产环境使用 `application-prod.properties` 配置文件：

```properties
# 生产环境服务器配置
server.port=${PORT:8080}

# 生产环境数据库配置
spring.r2dbc.url=${DB_URL}
spring.r2dbc.username=${DB_USER}
spring.r2dbc.password=${DB_PASSWORD}


# 生产环境数据库连接池配置
spring.r2dbc.pool.max-size=${DB_POOL_MAX_SIZE:10}
spring.r2dbc.pool.max-idle-time=${DB_POOL_MAX_IDLE_TIME:PT10M}
spring.r2dbc.pool.max-life-time=${DB_POOL_MAX_LIFE_TIME:PT30M}
spring.r2dbc.pool.initial-size=${DB_POOL_INITIAL_SIZE:2}
```

## 📊 监控

### Actuator端点

- `GET /actuator/health` - 应用健康状态
- `GET /actuator/info` - 应用信息
- `GET /actuator/flyway` - 数据库迁移状态
- `GET /actuator/metrics` - 系统指标

### 自定义监控端点

#### **数据库监控**:
- `GET /api/v1/monitoring/database/connection/stats` - 数据库连接统计
- `GET /api/v1/monitoring/database/performance/stats` - 数据库性能统计
- `GET /api/v1/monitoring/database/health` - 数据库健康信息
- `GET /api/v1/monitoring/database/table/stats` - 数据库表统计

#### **缓存监控**:
- `GET /api/v1/monitoring/cache/stats` - 缓存统计信息
- `DELETE /api/v1/monitoring/cache/clear` - 清除所有缓存
- `DELETE /api/v1/monitoring/cache/clear/{cacheName}` - 清除指定缓存

#### **事务监控**:
- `GET /api/v1/monitoring/transaction/stats` - 事务统计信息

### 健康检查响应示例

**基础健康检查** (生产环境默认):
```json
{
  "status": "UP"
}
```

**详细健康检查** (配置 `show-details=when-authorized` 后):
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "status": "Connected",
        "validationQuery": "SELECT 1"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 123456789012,
        "threshold": 10485760
      }
    }
  }
}
```

  }
}
```

### 自定义监控端点

#### **数据库监控端点**:

**数据库连接统计**:
```json
{
  "totalConnections": 25,
  "activeConnections": 3,
  "idleConnections": 22,
  "connectionErrors": 0,
  "averageConnectionTime": 45.2
}
```

**数据库性能统计**:
```json
{
  "totalQueries": 1250,
  "totalInserts": 45,
  "totalUpdates": 120,
  "totalDeletes": 8,
  "totalErrors": 2,
  "averageQueryTime": 12.5,
  "maxQueryTime": 150.0,
  "errorRate": 0.16
}
```

**数据库健康信息**:
```json
{
  "status": "UP",
  "version": "PostgreSQL 15.4",
  "database": "asatex-revenue",
  "user": "db_user",
  "lastChecked": "2024-01-15T10:30:00Z",
  "message": "Connected successfully"
}
```

**数据库表统计**:
```json
{
  "tables": {
    "employees": {
      "tableName": "employees",
      "rowCount": 1250,
      "inserts": 45,
      "updates": 120,
      "deletes": 8
    },
    "database_audit_logs": {
      "tableName": "database_audit_logs",
      "rowCount": 5000,
      "inserts": 200,
      "updates": 0,
      "deletes": 0
    }
  },
  "totalRows": 6250,
  "totalInserts": 245,
  "totalUpdates": 120,
  "totalDeletes": 8
}
```

#### **缓存监控端点**:

**缓存统计信息**:
```json
{
  "totalCaches": 3,
  "totalHits": 1250,
  "totalMisses": 45,
  "totalPuts": 1295,
  "totalEvictions": 12,
  "averageHitRate": 0.965,
  "cacheDetails": [
    {
      "cacheName": "employeeCache",
      "hits": 800,
      "misses": 20,
      "puts": 820,
      "evictions": 5,
      "hitRate": 0.975
    }
  ]
}
```

### 生产环境监控使用说明

#### 1. **部署更新后的配置**

重新部署应用以应用新的健康检查配置：

```bash
# 构建并部署到Cloud Run
./gradlew build
gcloud run deploy revenue-calculator-employee \
  --source . \
  --platform managed \
  --region asia-northeast1 \
  --allow-unauthenticated
```

#### 2. **验证系统状态**

部署后，访问健康检查端点应该能看到详细的组件信息：

```bash
# 检查详细健康状态
curl https://your-domain.com/actuator/health

# 检查缓存统计
curl https://your-domain.com/actuator/metrics/cache.hit
curl https://your-domain.com/actuator/metrics/cache.miss

# 检查自定义缓存统计
curl https://your-domain.com/api/v1/monitoring/cache/stats

# 检查数据库连接统计
curl https://your-domain.com/api/v1/monitoring/database/connection/stats

# 检查数据库性能统计
curl https://your-domain.com/api/v1/monitoring/database/performance/stats

# 检查数据库健康信息
curl https://your-domain.com/api/v1/monitoring/database/health

# 检查数据库表统计
curl https://your-domain.com/api/v1/monitoring/database/table/stats

# 检查事务统计
curl https://your-domain.com/api/v1/monitoring/transaction/stats
```

#### 3. **连接状态判断**


**数据库连接状态**:
- **正常状态**: `"db": {"status": "UP"}` 且 `"status": "Connected"`
- **连接失败**: `"db": {"status": "DOWN"}` 且包含错误信息
- **性能问题**: 检查 `errorRate` 和 `averageQueryTime` 指标

**数据库性能监控**:
- **查询性能**: `averageQueryTime` < 100ms 为正常
- **错误率**: `errorRate` < 1% 为正常
- **连接池**: `activeConnections` 不应超过 `totalConnections` 的80%

#### 4. **常见问题解决**

**Favicon 500错误**:
如果生产环境出现favicon.ico的500错误，已通过以下方式解决：
- 添加了favicon.ico文件到 `src/main/resources/static/`
- 配置了WebFlux静态资源处理
- 添加了全局异常处理器
- 在生产环境配置中禁用了favicon处理

**系统故障排查**:

```bash
# 检查环境变量
gcloud run services describe revenue-calculator-employee \
  --region asia-northeast1 \
  --format="value(spec.template.spec.template.spec.containers[0].env[].name,spec.template.spec.template.spec.containers[0].env[].value)"

# 检查应用日志
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=revenue-calculator-employee" --limit=50
```

### 自定义指标

- `employee.operations.total` - 员工操作总数
- `employee.create.total` - 员工创建总数
- `employee.update.total` - 员工更新总数
- `employee.delete.total` - 员工删除总数
- `employee.query.total` - 员工查询总数
- `cache.hits.total` - 缓存命中总数
- `cache.misses.total` - 缓存未命中总数
- `rate.limit.triggered.total` - 限流触发总数
- `transaction.start` - 事务开始次数
- `transaction.commit` - 事务提交次数
- `transaction.rollback` - 事务回滚次数
- `transaction.error` - 事务错误次数

## 🗄️ 数据库

### 表结构

#### employees表

```sql
CREATE TABLE employees (
    employee_id BIGSERIAL PRIMARY KEY,
    employee_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    furigana VARCHAR(200),
    birthday DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);
```


### 数据库约束

- 员工编号唯一性约束
- 员工编号格式约束 (字母、数字、下划线、连字符)
- 姓名长度约束 (1-100字符)
- 假名长度约束 (最大200字符)
- 生日约束 (必须是过去的日期)

### 迁移脚本

项目使用Flyway进行数据库版本管理：

- `V1__Create_employees_table.sql` - 创建员工表
- `V2__Insert_initial_employee_data.sql` - 插入初始数据
- `V3__Add_constraints_to_employees_table.sql` - 添加约束
- `V4__Add_soft_delete_columns.sql` - 添加软删除支持

## 🚀 部署

### Docker部署

1. **构建和运行**

   ```bash
   # 开发环境运行（端口9001）
   ./gradlew build
   docker build -t revenue-calculator-employee .
   docker run -p 9001:8080 -e SPRING_PROFILES_ACTIVE=default revenue-calculator-employee
   
   # 生产环境运行（端口8080）
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod revenue-calculator-employee
   ```

### 生产环境部署

#### Google Cloud Run 部署

本节提供Google Cloud Run的完整生产环境部署说明。

##### 前提条件

- 已启用计费的Google Cloud项目
- Cloud SQL PostgreSQL实例
- 具有适当权限的服务账户

##### 方法一：Cloud Run控制台部署

1. **打开Cloud Run控制台**
   - 访问 [Google Cloud Console](https://console.cloud.google.com/)
   - 选择您的项目
   - 导航到 **Cloud Run**

2. **创建新服务**
   - 点击 **"创建服务"**
   - 选择 **"从头开始部署一个容器"**

3. **配置源代码**
   ```
   源代码: 从源代码仓库部署
   仓库类型: GitHub
   仓库: 选择您的GitHub仓库
   分支: main
   构建类型: Dockerfile
   Dockerfile路径: /Dockerfile
   ```

4. **配置服务设置**
   ```
   服务名称: revenue-calculator-employee
   区域: your-region
   CPU分配: CPU仅在有请求时分配
   最小实例数: 1
   最大实例数: 10
   ```

5. **配置容器设置**
   ```
   端口: 9001
   内存: 1 GiB (推荐) 或 2 GiB (如果仍有内存问题)
   CPU: 2
   请求超时: 300秒
   启动超时: 300秒
   ```

6. **配置环境变量**
   ```
   SPRING_PROFILES_ACTIVE: prod
   DB_URL: r2dbc:postgresql://your-db-host:5432/asatex-revenue
   DB_USER: your-db-username
   DB_PASSWORD: your-db-password
   FLYWAY_URL: jdbc:postgresql://your-db-host:5432/asatex-revenue
   DB_POOL_MAX_SIZE: 5
   DB_POOL_MAX_IDLE_TIME: PT5M
   DB_POOL_MAX_LIFE_TIME: PT15M
   ```

7. **配置VPC连接**
   - 在 **"连接"** 部分
   - 点击 **"添加VPC连接器"**
   - 选择用于Cloud SQL访问的VPC连接器

8. **配置身份验证**
   - 在 **"安全"** 部分
   - 服务账户：`your-service-account@your-project.iam.gserviceaccount.com`
   - 允许未通过身份验证的调用：**是**

9. **部署服务**
   - 点击 **"创建"**
   - 等待构建和部署完成（通常需要10-15分钟）

##### 方法二：命令行部署

1. **构建Docker镜像**
   ```bash
   # 构建镜像
   docker build -t gcr.io/your-project-id/revenue-calculator-backend-employee .
   
   # 推送镜像到Google Container Registry
   docker push gcr.io/your-project-id/revenue-calculator-backend-employee
   ```

2. **部署到Cloud Run**
   ```bash
   gcloud run deploy revenue-calculator-employee \
     --image gcr.io/your-project-id/revenue-calculator-backend-employee \
     --platform managed \
     --region your-region \
     --set-env-vars SPRING_PROFILES_ACTIVE="prod" \
     --set-env-vars DB_URL="r2dbc:postgresql://your-db-host:5432/asatex-revenue" \
     --set-env-vars DB_USER="your-db-username" \
     --set-env-vars DB_PASSWORD="your-db-password" \
     --set-env-vars FLYWAY_URL="jdbc:postgresql://your-db-host:5432/asatex-revenue" \
     --set-env-vars DB_POOL_MAX_SIZE="5" \
     --set-env-vars DB_POOL_MAX_IDLE_TIME="PT5M" \
     --set-env-vars DB_POOL_MAX_LIFE_TIME="PT15M" \
     --vpc-connector your-vpc-connector \
     --service-account your-service-account@your-project.iam.gserviceaccount.com \
     --allow-unauthenticated \
     --memory 1Gi \
     --cpu 2 \
     --timeout 300 \
     --port 9001
   ```

##### 环境变量配置

**必需的环境变量：**
```bash
# 应用配置
SPRING_PROFILES_ACTIVE=prod

# 数据库配置（Cloud SQL + VPC连接）
DB_URL=r2dbc:postgresql://your-db-host:5432/asatex-revenue
DB_USER=your-db-username
DB_PASSWORD=your-db-password
FLYWAY_URL=jdbc:postgresql://your-db-host:5432/asatex-revenue

```

**可选的环境变量：**
```bash
# 数据库连接池配置
DB_POOL_MAX_SIZE=5
DB_POOL_MAX_IDLE_TIME=PT5M
DB_POOL_MAX_LIFE_TIME=PT15M
```

##### 数据库配置

**VPC连接设置：**

1. **创建VPC连接器：**
   ```bash
   # 为Cloud Run创建VPC连接器以访问Cloud SQL
   gcloud compute networks vpc-access connectors create your-vpc-connector \
     --region=your-region \
     --subnet=your-subnet \
     --subnet-project=your-project-id \
     --min-instances=2 \
     --max-instances=3
   ```

2. **数据库用户设置：**
   ```sql
   -- 创建带密码认证的数据库用户
   CREATE USER your-db-username WITH PASSWORD 'your-db-password';
   GRANT ALL PRIVILEGES ON DATABASE asatex_revenue TO your-db-username;
   GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your-db-username;
   GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your-db-username;
   ```

##### 部署验证

1. **检查服务状态**
   - 在Cloud Run控制台中，确认服务状态为 **"正在运行"**

2. **测试健康检查**
   ```bash
   # 获取服务URL
   SERVICE_URL=$(gcloud run services describe revenue-calculator-employee \
       --region=your-region \
       --format="value(status.url)")
   
   # 测试健康检查
   curl $SERVICE_URL/actuator/health
   
   # 测试数据库连接
   curl $SERVICE_URL/actuator/health/db
   ```

3. **访问API文档**
   - 打开浏览器：`$SERVICE_URL/swagger-ui.html`

##### 更新部署

1. 在Cloud Run控制台中，点击服务名称
2. 点击 **"编辑和部署新版本"**
3. 在 **"源代码"** 部分，点击 **"重新构建"**
4. 点击 **"部署"**

##### 故障排除

**常见问题：**

1. **构建失败**
   - 检查Dockerfile语法
   - 确认所有依赖都已安装
   - 查看构建日志

2. **部署失败**
   - 检查Cloud SQL连接配置
   - 确认服务账户权限
   - 检查环境变量配置

3. **应用无法启动**
   - 查看Cloud Run日志
   - 检查数据库连接

**有用的命令：**
```bash
# 查看服务日志
gcloud run services logs read revenue-calculator-employee --region=your-region

# 查看服务详情
gcloud run services describe revenue-calculator-employee --region=your-region

# 查看构建日志
gcloud builds list --limit=5
```

##### 安全注意事项

1. **VPC安全**：确保VPC连接器配置正确且安全
2. **环境变量**：在Cloud Run中设置环境变量时，确保敏感信息不会在日志中暴露
3. **网络访问**：确保Cloud Run服务能够通过VPC访问Cloud SQL实例
4. **防火墙规则**：检查Cloud SQL的防火墙规则，确保允许来自VPC的连接
5. **数据库安全**：使用强密码并限制数据库用户权限
6. **VPC连接器**：确保VPC连接器具有适当的网络访问控制

### 生产环境配置

```properties
# 生产环境配置
spring.profiles.active=prod
server.port=9001

# 数据库连接池配置
spring.r2dbc.pool.initial-size=10
spring.r2dbc.pool.max-size=20
spring.r2dbc.pool.max-idle-time=30m

# 日志配置
logging.level.root=WARN
logging.level.jp.asatex.revenue_calculator_backend_employee=INFO
```

## 🔒 安全特性

### API限流

- **一般API**: 100请求/分钟
- **搜索API**: 50请求/分钟
- **写操作API**: 20请求/分钟

### 安全验证

- 输入参数验证
- SQL注入防护
- XSS防护

### 日志安全

- 不记录敏感信息
- 结构化日志便于分析
- 安全事件记录

## 📈 性能优化

### 缓存策略

- **员工信息缓存**: 30分钟TTL
- **员工搜索缓存**: 15分钟TTL
- **分页缓存**: 10分钟TTL
- **自动缓存失效**: 写操作时清除相关缓存

### 响应式编程

- 完全非阻塞I/O
- 背压处理
- 资源高效利用

### 数据库优化

- 连接池配置
- 查询优化
- 索引优化

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

### 编码规范

- 使用Java 21特性
- 遵循Spring Boot最佳实践
- 编写单元测试和集成测试
- 使用响应式编程模式
- 遵循RESTful API设计原则

## 📝 许可证

本项目采用MIT许可证 - 详情请查看 [LICENSE](LICENSE) 文件。

## 🏢 公司信息

- **公司名称**: 株式会社アサテクス (Kabushiki-gaisha Asatex / Asatex Co., Ltd.)
- **开发者**: 牛宇平 (Niuyuping)
- **邮箱**: <niuyuping@asatex.jp>
- **LINE ID**: niuyuping
- **演示网站**: [revenue.asatex.jp](https://revenue.asatex.jp)

## 📞 联系方式

- 项目链接: [https://github.com/niuyuping/revenue-calculator-backend-employee](https://github.com/niuyuping/revenue-calculator-backend-employee)
- 问题报告: [https://github.com/niuyuping/revenue-calculator-backend-employee/issues](https://github.com/niuyuping/revenue-calculator-backend-employee/issues)
- 公司邮箱: <niuyuping@asatex.jp>
- LINE联系: niuyuping

## 🙏 致谢

- 提供优秀框架的Spring Boot团队
- 提供可靠数据库的PostgreSQL社区
- 提供支持和建议的所有贡献者

---

**版本**: v1.0.0  
**最后更新**: 2024年12月  
**兼容性**: Java 21+, Spring Boot 3.5.6+

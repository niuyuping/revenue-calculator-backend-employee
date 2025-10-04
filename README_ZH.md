# 员工管理微服务 (Revenue Calculator Backend Employee)

基于 Spring Boot 3.x、R2DBC、WebFlux 的响应式员工管理系统后端服务。

## 🌐 语言选择 / Language Selection / 言語選択

- 🇨🇳 [中文版 (Chinese)](README_ZH.md) - 完整的中文文档
- 🇺🇸 [English Version](README_EN.md) - Complete English documentation  
- 🇯🇵 [日本語版 (Japanese)](README_JA.md) - 完全な日本語文書

---

## 🚀 技术栈

- **Java 21** - 编程语言
- **Spring Boot 3.5.6** - 应用框架
- **Spring WebFlux** - 响应式Web框架
- **Spring Data R2DBC** - 响应式数据库访问
- **PostgreSQL** - 关系型数据库
- **Redis** - 缓存数据库
- **Flyway** - 数据库迁移工具
- **Jakarta Validation** - 数据验证
- **Spring Boot Actuator** - 应用监控
- **Resilience4j** - 限流和熔断
- **Swagger/OpenAPI 3** - API文档
- **Spring Boot i18n** - 国际化支持
- **Gradle** - 构建工具
- **JUnit 5** - 测试框架
- **Mockito** - Mock测试框架
- **TestContainers** - 集成测试

## 📋 功能特性

### 核心功能

- ✅ **员工CRUD操作** - 员工信息的创建、读取、更新、删除
- ✅ **员工搜索** - 支持按姓名和假名搜索
- ✅ **数据验证** - 完整的输入数据验证和约束
- ✅ **异常处理** - 统一的异常处理和错误响应
- ✅ **响应式编程** - 完全非阻塞响应式架构
- ✅ **缓存支持** - Redis缓存提升性能
- ✅ **API限流** - Resilience4j限流保护
- ✅ **监控指标** - 完整的业务和性能监控
- ✅ **多语言文档** - 支持英、中、日三种语言的API文档

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
│   │   │   ├── InternationalizationConfig.java
│   │   │   ├── LoggingConfig.java
│   │   │   ├── MetricsConfig.java
│   │   │   ├── MultiLanguageOpenApiConfig.java
│   │   │   ├── RateLimitConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── ValidationConfig.java
│   │   ├── controller/       # REST控制器
│   │   │   └── EmployeeController.java
│   │   ├── dto/             # 数据传输对象
│   │   │   └── EmployeeDto.java
│   │   ├── entity/          # 实体类
│   │   │   └── Employee.java
│   │   ├── exception/       # 异常处理
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   └── DuplicateEmployeeNumberException.java
│   │   ├── repository/      # 数据访问层
│   │   │   └── EmployeeRepository.java
│   │   ├── service/         # 业务逻辑层
│   │   │   └── EmployeeService.java
│   │   ├── util/            # 工具类
│   │   │   └── LoggingUtil.java
│   │   └── RevenueCalculatorBackendEmployeeApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       ├── messages.properties          # 英文资源文件
│       ├── messages_zh_CN.properties    # 中文资源文件
│       ├── messages_ja.properties       # 日文资源文件
│       ├── static/
│       │   └── swagger-ui-custom.css    # Swagger UI自定义样式
│       └── db/migration/    # 数据库迁移脚本
│           ├── V1__Create_employees_table.sql
│           ├── V2__Insert_initial_employee_data.sql
│           └── V3__Add_constraints_to_employees_table.sql
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
```text

## 🚀 快速开始

### 前提条件

- Java 21+
- PostgreSQL 12+
- Redis 6+
- Gradle 8.0+

### 安装和运行

1. **克隆项目**

   ```bash
   git clone https://github.com/niuyuping/revenue-calculator-backend-employee.git
   cd revenue-calculator-backend-employee
   ```

1. **数据库设置**

   ```bash
   # 创建PostgreSQL数据库
   createdb asatex-revenue
   ```

1. **Redis设置**

   ```bash
   # 启动Redis服务
   redis-server
   ```

1. **应用配置**

   编辑 `src/main/resources/application.properties`:

   ```properties
   # 数据库配置
   spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
   spring.r2dbc.username=your_username
   spring.r2dbc.password=your_password
   
   # Redis配置
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   
   # Flyway配置
   spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
   spring.flyway.user=your_username
   spring.flyway.password=your_password
   ```

1. **运行应用**

   ```bash
   ./gradlew bootRun
   ```

1. **验证运行**

   ```bash
   curl http://localhost:9001/api/v1/employee/health
   ```

## 📚 API文档

### Swagger UI访问

启动应用后，可以通过以下链接访问Swagger UI：

- **Swagger UI**: <http://localhost:9001/swagger-ui.html>
- **OpenAPI JSON**: <http://localhost:9001/v3/api-docs>
- **Swagger配置**: <http://localhost:9001/v3/api-docs/swagger-config>

### 🌐 多语言支持

API文档支持三种语言，可以通过以下方式切换：

#### 语言切换方式

1. **通过Accept-Language头**：

   ```bash
   # 英文
   curl -H "Accept-Language: en" http://localhost:9001/v3/api-docs
   
   # 中文
   curl -H "Accept-Language: zh-CN" http://localhost:9001/v3/api-docs
   
   # 日文
   curl -H "Accept-Language: ja" http://localhost:9001/v3/api-docs
   ```

1. **通过Swagger UI分组**：
   - **英文文档**: <http://localhost:9001/swagger-ui.html?urls.primaryName=english>
   - **中文文档**: <http://localhost:9001/swagger-ui.html?urls.primaryName=chinese>
   - **日文文档**: <http://localhost:9001/swagger-ui.html?urls.primaryName=japanese>

#### 支持的语言

- 🇺🇸 **English** - 默认语言
- 🇨🇳 **中文 (简体)** - 完整的中文API文档
- 🇯🇵 **日本語** - 完整的日文API文档

#### 多语言测试

通过以下方式测试多语言功能：

```bash
# 测试不同语言的API文档
curl -H "Accept-Language: en" http://localhost:9001/v3/api-docs
curl -H "Accept-Language: zh-CN" http://localhost:9001/v3/api-docs
curl -H "Accept-Language: ja" http://localhost:9001/v3/api-docs
```bash

### 基础URL

```text
http://localhost:9001/api/v1/employee
```text

### 端点列表

#### 1. 获取所有员工

```http
GET /api/v1/employee
```http

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
```json

#### 2. 根据ID获取员工

```http
GET /api/v1/employee/{id}
```http

**参数:**

- `id` (路径参数): 员工ID (必须是正数)

#### 3. 根据员工编号获取员工

```http
GET /api/v1/employee/number/{employeeNumber}
```http

**参数:**

- `employeeNumber` (路径参数): 员工编号 (1-20字符，英数字下划线连字符)

#### 4. 根据姓名搜索员工

```http
GET /api/v1/employee/search/name?name={name}
```http

**参数:**

- `name` (查询参数): 姓名关键词 (1-100字符)

#### 5. 根据假名搜索员工

```http
GET /api/v1/employee/search/furigana?furigana={furigana}
```http

**参数:**

- `furigana` (查询参数): 假名关键词 (1-200字符)

#### 6. 创建员工

```http
POST /api/v1/employee
Content-Type: application/json
```http

**请求体:**

```json
{
  "employeeNumber": "EMP001",
  "name": "田中太郎",
  "furigana": "タナカタロウ",
  "birthday": "1990-05-15"
}
```json

#### 7. 更新员工

```http
PUT /api/v1/employee/{id}
Content-Type: application/json
```http

#### 8. 根据ID删除员工

```http
DELETE /api/v1/employee/{id}
```http

#### 9. 根据员工编号删除员工

```http
DELETE /api/v1/employee/number/{employeeNumber}
```http

#### 10. 健康检查

```http
GET /api/v1/employee/health
```http

### 错误响应格式

```json
{
  "error": "错误类型",
  "message": "错误描述",
  "details": "详细错误信息",
  "status": 400
}
```json

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
```bash

### 运行特定测试类

```bash
./gradlew test --tests "EmployeeServiceTest"
./gradlew test --tests "EmployeeRepositoryTest"
./gradlew test --tests "EmployeeIntegrationTest"
```bash

### 测试覆盖率

项目具有全面的测试覆盖率：

- **Service层测试** - 业务逻辑测试
- **Repository层测试** - 数据访问测试
- **Controller层测试** - API端点测试
- **异常处理测试** - 错误处理测试
- **参数验证测试** - 输入验证测试
- **集成测试** - 端到端测试
- **配置测试** - 配置类测试

## 🔧 配置

### 应用配置 (application.properties)

```properties
# 服务器配置
server.port=9001

# 数据库配置
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
spring.r2dbc.username=${DB_USERNAME:db_user}
spring.r2dbc.password=${DB_PASSWORD:local}

# Redis配置
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Flyway配置
spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
spring.flyway.user=${DB_USERNAME:db_user}
spring.flyway.password=${DB_PASSWORD:local}
spring.flyway.baseline-on-migrate=true

# 缓存配置
spring.cache.type=redis
spring.cache.redis.time-to-live=1800000

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
```properties

### 环境变量

- `DB_USERNAME` - 数据库用户名 (默认: db_user)
- `DB_PASSWORD` - 数据库密码 (默认: local)

## 📊 监控

### Actuator端点

- `GET /actuator/health` - 应用健康状态
- `GET /actuator/info` - 应用信息
- `GET /actuator/flyway` - 数据库迁移状态
- `GET /actuator/metrics` - 系统指标

### 健康检查响应示例

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
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
```json

### 自定义指标

- `employee.operations.total` - 员工操作总数
- `employee.create.total` - 员工创建总数
- `employee.update.total` - 员工更新总数
- `employee.delete.total` - 员工删除总数
- `employee.query.total` - 员工查询总数
- `cache.hits.total` - 缓存命中总数
- `cache.misses.total` - 缓存未命中总数
- `rate.limit.triggered.total` - 限流触发总数

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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```sql

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

## 🚀 部署

### Docker部署

1. **创建Dockerfile**

   ```dockerfile
   FROM openjdk:21-jdk-slim
   COPY build/libs/*.jar app.jar
   EXPOSE 9001
   ENTRYPOINT ["java", "-jar", "/app.jar"]
   ```

1. **构建和运行**

   ```bash
   ./gradlew build
   docker build -t revenue-calculator-employee .
   docker run -p 9001:9001 revenue-calculator-employee
   ```

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
```properties

## 🔒 安全特性

### API限流

- **一般API**: 100请求/分钟
- **搜索API**: 50请求/分钟
- **写操作API**: 20请求/分钟

### 数据验证

- 输入参数验证
- SQL注入防护
- XSS防护

### 日志安全

- 不记录敏感信息
- 结构化日志便于分析
- 安全事件记录

## 📈 性能优化

### 缓存策略

- **员工信息缓存**: 15分钟TTL
- **员工列表缓存**: 5分钟TTL
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
1. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
1. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
1. 推送到分支 (`git push origin feature/AmazingFeature`)
1. 创建Pull Request

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

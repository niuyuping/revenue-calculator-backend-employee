# Employee Management Microservice (Revenue Calculator Backend Employee)

A reactive employee management system backend service based on Spring Boot 3.x, R2DBC, and WebFlux.

## 🚀 Technology Stack

- **Java 21** - Programming Language
- **Spring Boot 3.5.6** - Application Framework
- **Spring WebFlux** - Reactive Web Framework
- **Spring Data R2DBC** - Reactive Database Access
- **PostgreSQL** - Relational Database
- **Redis** - Cache Database
- **Flyway** - Database Migration Tool
- **Jakarta Validation** - Data Validation
- **Spring Boot Actuator** - Application Monitoring
- **Resilience4j** - Rate Limiting and Circuit Breaker
- **Swagger/OpenAPI 3** - API Documentation
- **Spring Boot i18n** - Internationalization Support
- **Gradle** - Build Tool
- **JUnit 5** - Testing Framework
- **Mockito** - Mock Testing Framework
- **TestContainers** - Integration Testing

## 📋 Features

### Core Features

- ✅ **Employee CRUD Operations** - Create, read, update, delete employee information
- ✅ **Employee Search** - Search by name and furigana
- ✅ **Data Validation** - Complete input data validation and constraints
- ✅ **Exception Handling** - Unified exception handling and error responses
- ✅ **Reactive Programming** - Fully non-blocking reactive architecture
- ✅ **Cache Support** - Redis cache for performance enhancement
- ✅ **API Rate Limiting** - Resilience4j rate limiting protection
- ✅ **Monitoring Metrics** - Complete business and performance monitoring
- ✅ **Multi-language Documentation** - API documentation in English, Chinese, and Japanese

### Data Validation

- Employee number format validation (letters, numbers, underscores, hyphens)
- Name length validation (1-100 characters)
- Furigana format validation (hiragana, katakana, Latin letters, spaces, parentheses)
- Birthday validation (must be a past date)
- Path parameter and query parameter validation

### Monitoring and Health Checks

- Spring Boot Actuator integration
- Health check endpoints
- Flyway migration status monitoring
- Application information endpoints
- Custom business metrics
- Performance monitoring metrics

## 🏗️ Project Structure

```text
src/
├── main/
│   ├── java/jp/asatex/revenue_calculator_backend_employee/
│   │   ├── config/           # Configuration classes
│   │   │   ├── CacheConfig.java
│   │   │   ├── InternationalizationConfig.java
│   │   │   ├── LoggingConfig.java
│   │   │   ├── MetricsConfig.java
│   │   │   ├── MultiLanguageOpenApiConfig.java
│   │   │   ├── RateLimitConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── ValidationConfig.java
│   │   ├── controller/       # REST controllers
│   │   │   └── EmployeeController.java
│   │   ├── dto/             # Data Transfer Objects
│   │   │   └── EmployeeDto.java
│   │   ├── entity/          # Entity classes
│   │   │   └── Employee.java
│   │   ├── exception/       # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   └── DuplicateEmployeeNumberException.java
│   │   ├── repository/      # Data access layer
│   │   │   └── EmployeeRepository.java
│   │   ├── service/         # Business logic layer
│   │   │   └── EmployeeService.java
│   │   ├── util/            # Utility classes
│   │   │   └── LoggingUtil.java
│   │   └── RevenueCalculatorBackendEmployeeApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       ├── messages.properties          # English resource file
│       ├── messages_zh_CN.properties    # Chinese resource file
│       ├── messages_ja.properties       # Japanese resource file
│       ├── static/
│       │   └── swagger-ui-custom.css    # Swagger UI custom styles
│       └── db/migration/    # Database migration scripts
│           ├── V1__Create_employees_table.sql
│           ├── V2__Insert_initial_employee_data.sql
│           └── V3__Add_constraints_to_employees_table.sql
└── test/                    # Test code
    ├── java/jp/asatex/revenue_calculator_backend_employee/
    │   ├── config/          # Configuration tests
    │   ├── controller/      # Controller tests
    │   ├── exception/       # Exception handling tests
    │   ├── integration/     # Integration tests
    │   ├── repository/      # Data access tests
    │   ├── service/         # Business logic tests
    │   ├── util/            # Utility class tests
    │   └── validation/      # Validation tests
    └── resources/
        └── application-test.properties
```

## 🚀 Quick Start

### Prerequisites

- Java 21+
- PostgreSQL 12+
- Redis 6+
- Gradle 8.0+

### Installation and Running

1. **Clone the project**

   ```bash
   git clone <repository-url>
   cd revenue-calculator-backend-employee
   ```

2. **Database setup**

   ```bash
   # Create PostgreSQL database
   createdb asatex-revenue
   ```

3. **Redis setup**

   ```bash
   # Start Redis service
   redis-server
   ```

4. **Application configuration**

   Edit `src/main/resources/application.properties`:

   ```properties
   # Database configuration
   spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
   spring.r2dbc.username=your_username
   spring.r2dbc.password=your_password
   
   # Redis configuration
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   
   # Flyway configuration
   spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
   spring.flyway.user=your_username
   spring.flyway.password=your_password
   ```

5. **Run the application**

   ```bash
   ./gradlew bootRun
   ```

6. **Verify the application**

   ```bash
   curl http://localhost:9001/api/v1/employee/health
   ```

## 📚 API Documentation

### Swagger UI Access

After starting the application, you can access Swagger UI through the following links:

- **Swagger UI**: <http://localhost:9001/swagger-ui.html>
- **OpenAPI JSON**: <http://localhost:9001/v3/api-docs>
- **Swagger Configuration**: <http://localhost:9001/v3/api-docs/swagger-config>

### 🌐 Multi-language Support

The API documentation supports three languages and can be switched in the following ways:

#### Language Switching Methods

1. **Through Accept-Language header**:

   ```bash
   # English
   curl -H "Accept-Language: en" http://localhost:9001/v3/api-docs
   
   # Chinese
   curl -H "Accept-Language: zh-CN" http://localhost:9001/v3/api-docs
   
   # Japanese
   curl -H "Accept-Language: ja" http://localhost:9001/v3/api-docs
   ```

2. **Through Swagger UI groups**:
   - **English Documentation**: <http://localhost:9001/swagger-ui.html?urls.primaryName=english>
   - **Chinese Documentation**: <http://localhost:9001/swagger-ui.html?urls.primaryName=chinese>
   - **Japanese Documentation**: <http://localhost:9001/swagger-ui.html?urls.primaryName=japanese>

#### Supported Languages

- 🇺🇸 **English** - Default language
- 🇨🇳 **Chinese (Simplified)** - Complete Chinese API documentation
- 🇯🇵 **Japanese** - Complete Japanese API documentation

### Base URL

```text
http://localhost:9001/api/v1/employee
```

### Endpoint List

#### 1. Get All Employees

```http
GET /api/v1/employee
```

**Response Example:**

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

#### 2. Get Employee by ID

```http
GET /api/v1/employee/{id}
```

**Parameters:**

- `id` (path parameter): Employee ID (must be positive)

#### 3. Get Employee by Employee Number

```http
GET /api/v1/employee/number/{employeeNumber}
```

**Parameters:**

- `employeeNumber` (path parameter): Employee number (1-20 characters, alphanumeric underscore hyphen)

#### 4. Search Employees by Name

```http
GET /api/v1/employee/search/name?name={name}
```

**Parameters:**

- `name` (query parameter): Name keyword (1-100 characters)

#### 5. Search Employees by Furigana

```http
GET /api/v1/employee/search/furigana?furigana={furigana}
```

**Parameters:**

- `furigana` (query parameter): Furigana keyword (1-200 characters)

#### 6. Create Employee

```http
POST /api/v1/employee
Content-Type: application/json
```

**Request Body:**

```json
{
  "employeeNumber": "EMP001",
  "name": "田中太郎",
  "furigana": "タナカタロウ",
  "birthday": "1990-05-15"
}
```

#### 7. Update Employee

```http
PUT /api/v1/employee/{id}
Content-Type: application/json
```

#### 8. Delete Employee by ID

```http
DELETE /api/v1/employee/{id}
```

#### 9. Delete Employee by Employee Number

```http
DELETE /api/v1/employee/number/{employeeNumber}
```

#### 10. Health Check

```http
GET /api/v1/employee/health
```

### Error Response Format

```json
{
  "error": "Error Type",
  "message": "Error Description",
  "details": "Detailed Error Information",
  "status": 400
}
```

### HTTP Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Deletion successful
- `400 Bad Request` - Request parameter error
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource conflict (duplicate employee number, etc.)
- `429 Too Many Requests` - Too many requests (rate limited)
- `500 Internal Server Error` - Internal server error

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test Classes

```bash
./gradlew test --tests "EmployeeServiceTest"
./gradlew test --tests "EmployeeRepositoryTest"
./gradlew test --tests "EmployeeIntegrationTest"
```

### Test Coverage

The project has comprehensive test coverage:

- **Service Layer Tests** - Business logic testing
- **Repository Layer Tests** - Data access testing
- **Controller Layer Tests** - API endpoint testing
- **Exception Handling Tests** - Error handling testing
- **Parameter Validation Tests** - Input validation testing
- **Integration Tests** - End-to-end testing
- **Configuration Tests** - Configuration class testing

## 🔧 Configuration

### Application Configuration (application.properties)

```properties
# Server configuration
server.port=9001

# Database configuration
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
spring.r2dbc.username=${DB_USERNAME:db_user}
spring.r2dbc.password=${DB_PASSWORD:local}

# Redis configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Flyway configuration
spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
spring.flyway.user=${DB_USERNAME:db_user}
spring.flyway.password=${DB_PASSWORD:local}
spring.flyway.baseline-on-migrate=true

# Cache configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=1800000

# Rate limiting configuration
resilience4j.ratelimiter.instances.employee-api.limit-for-period=100
resilience4j.ratelimiter.instances.employee-search.limit-for-period=50
resilience4j.ratelimiter.instances.employee-write.limit-for-period=20

# Actuator configuration
management.endpoints.web.exposure.include=health,info,flyway,metrics
management.endpoint.health.show-details=when-authorized
management.endpoint.flyway.enabled=true

# Logging configuration
logging.level.jp.asatex.revenue_calculator_backend_employee=INFO
logging.file.name=logs/revenue-calculator-employee.log
```

### Environment Variables

- `DB_USERNAME` - Database username (default: db_user)
- `DB_PASSWORD` - Database password (default: local)

## 📊 Monitoring

### Actuator Endpoints

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/flyway` - Database migration status
- `GET /actuator/metrics` - System metrics

### Health Check Response Example

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
```

### Custom Metrics

- `employee.operations.total` - Total employee operations
- `employee.create.total` - Total employee creations
- `employee.update.total` - Total employee updates
- `employee.delete.total` - Total employee deletions
- `employee.query.total` - Total employee queries
- `cache.hits.total` - Total cache hits
- `cache.misses.total` - Total cache misses
- `rate.limit.triggered.total` - Total rate limit triggers

## 🗄️ Database

### Table Structure

#### employees table

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
```

### Database Constraints

- Employee number uniqueness constraint
- Employee number format constraint (letters, numbers, underscores, hyphens)
- Name length constraint (1-100 characters)
- Furigana length constraint (maximum 200 characters)
- Birthday constraint (must be a past date)

### Migration Scripts

The project uses Flyway for database version management:

- `V1__Create_employees_table.sql` - Create employee table
- `V2__Insert_initial_employee_data.sql` - Insert initial data
- `V3__Add_constraints_to_employees_table.sql` - Add constraints

## 🚀 Deployment

### Docker Deployment

1. **Create Dockerfile**

   ```dockerfile
   FROM openjdk:21-jdk-slim
   COPY build/libs/*.jar app.jar
   EXPOSE 9001
   ENTRYPOINT ["java", "-jar", "/app.jar"]
   ```

2. **Build and Run**

   ```bash
   ./gradlew build
   docker build -t revenue-calculator-employee .
   docker run -p 9001:9001 revenue-calculator-employee
   ```

### Production Environment Configuration

```properties
# Production environment configuration
spring.profiles.active=prod
server.port=9001

# Database connection pool configuration
spring.r2dbc.pool.initial-size=10
spring.r2dbc.pool.max-size=20
spring.r2dbc.pool.max-idle-time=30m

# Logging configuration
logging.level.root=WARN
logging.level.jp.asatex.revenue_calculator_backend_employee=INFO
```

## 🔒 Security Features

### API Rate Limiting

- **General API**: 100 requests/minute
- **Search API**: 50 requests/minute
- **Write Operations API**: 20 requests/minute

### Security Data Validation

- Input parameter validation
- SQL injection protection
- XSS protection

### Logging Security

- No sensitive information logging
- Structured logging for analysis
- Security event logging

## 📈 Performance Optimization

### Cache Strategy

- **Employee Information Cache**: 15 minutes TTL
- **Employee List Cache**: 5 minutes TTL
- **Automatic Cache Invalidation**: Clear related cache on write operations

### Reactive Programming

- Fully non-blocking I/O
- Backpressure handling
- Efficient resource utilization

### Database Optimization

- Connection pool configuration
- Query optimization
- Index optimization

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Create a Pull Request

### Coding Standards

- Use Java 21 features
- Follow Spring Boot best practices
- Write unit tests and integration tests
- Use reactive programming patterns
- Follow RESTful API design principles

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏢 Company Information

- **Company Name**: 株式会社アサテクス (Kabushiki-gaisha Asatex / Asatex Co., Ltd.)
- **Developer**: 牛宇平 (Niuyuping)
- **Email**: <niuyuping@asatex.jp>
- **LINE ID**: niuyuping
- **Demo Website**: [revenue.asatex.jp](https://revenue.asatex.jp)

## 📞 Contact

- Project Link: [https://github.com/username/revenue-calculator-backend-employee](https://github.com/username/revenue-calculator-backend-employee)
- Issue Reports: [https://github.com/username/revenue-calculator-backend-employee/issues](https://github.com/username/revenue-calculator-backend-employee/issues)
- Company Email: <niuyuping@asatex.jp>
- LINE Contact: niuyuping

## 🙏 Acknowledgments

- Spring Boot team for providing excellent frameworks
- PostgreSQL community for providing reliable databases
- All contributors for providing support and suggestions

---

**Version**: v1.0.0  
**Last Updated**: December 2024  
**Compatibility**: Java 21+, Spring Boot 3.5.6+

# Employee Management Microservice (Revenue Calculator Backend Employee)

A reactive employee management system backend service based on Spring Boot 3.x, R2DBC, and WebFlux with comprehensive enterprise features.

## 🌐 Language Selection

- 🇨🇳 [中文版 (Chinese)](README_ZH.md) - 完整的中文文档
- 🇺🇸 **English Version** - Complete English documentation (this file)
- 🇯🇵 [日本語版 (Japanese)](README_JA.md) - 完全な日本語文書

---

## 🚀 Technology Stack

- **Java 21** - Programming Language
- **Spring Boot 3.5.6** - Application Framework
- **Spring WebFlux** - Reactive Web Framework
- **Spring Data R2DBC** - Reactive Database Access
- **PostgreSQL** - Relational Database
- **Flyway** - Database Migration Tool
- **Jakarta Validation** - Data Validation
- **Spring Boot Actuator** - Application Monitoring
- **Resilience4j** - Rate Limiting and Circuit Breaker
- **Swagger/OpenAPI 3** - API Documentation
- **Gradle** - Build Tool
- **JUnit 5** - Testing Framework
- **Mockito** - Mock Testing Framework
- **TestContainers** - Integration Testing

## 📋 Features

### Core Features

- ✅ **Employee CRUD Operations** - Create, read, update, delete employee information
- ✅ **Employee Search** - Search by name
- ✅ **Pagination Support** - Paginated and sorted employee list queries
- ✅ **Data Validation** - Complete input data validation and constraints
- ✅ **Exception Handling** - Unified exception handling and error responses
- ✅ **Reactive Programming** - Fully non-blocking reactive architecture
- ✅ **API Rate Limiting** - Resilience4j rate limiting protection
- ✅ **Monitoring Metrics** - Complete business and performance monitoring
- ✅ **API Documentation** - Complete Swagger/OpenAPI documentation

### Enterprise Features

#### 🔄 Cache & Rate Limiting

  - Employee Info Cache: 30 minutes TTL
  - Employee Search Cache: 15 minutes TTL
  - Pagination Cache: 10 minutes TTL
- **Rate Limiting**: Different limits for different operations (20-100 requests/minute)


#### 🔄 Transaction Management

- **ACID Compliance**: R2DBC-based transaction support
- **Automatic Transaction Management**: `@Transactional` annotation support
- **Transaction Monitoring**: Real-time transaction tracking
- **Performance Metrics**: Transaction execution time monitoring


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
│   │   │   ├── JacksonConfig.java
│   │   │   ├── MetricsConfig.java
│   │   │   ├── MultiLanguageOpenApiConfig.java
│   │   │   ├── RateLimitConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   ├── TransactionConfig.java
│   │   │   └── ValidationConfig.java
│   │   ├── controller/       # REST controllers
│   │   │   └── EmployeeController.java
│   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── EmployeeDto.java
│   │   │   ├── PageRequest.java
│   │   │   ├── PageResponse.java
│   │   │   └── SortDirection.java
│   │   ├── entity/          # Entity classes
│   │   │   ├── Employee.java
│   │   ├── exception/       # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   ├── DuplicateEmployeeNumberException.java
│   │   │   └── TransactionException.java
│   │   ├── repository/      # Data access layer
│   │   │   ├── EmployeeRepository.java
│   │   ├── service/         # Business logic layer
│   │   │   ├── EmployeeService.java
│   │   │   ├── CacheMonitoringService.java
│   │   │   └── TransactionMonitoringService.java
│   │   ├── util/            # Utility classes
│   │   │   └── LoggingUtil.java
│   │   └── RevenueCalculatorBackendEmployeeApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       ├── messages.properties          # Internationalization resource file
│       └── db/migration/    # Database migration scripts
│           ├── V1__Create_employees_table.sql
│           ├── V2__Insert_initial_employee_data.sql
│           ├── V3__Add_constraints_to_employees_table.sql
│           ├── V4__Add_soft_delete_columns.sql
│           └── V5__Create_database_audit_logs_table.sql
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
- Gradle 8.0+

### Installation and Running

1. **Clone the project**

   ```bash
   git clone https://github.com/niuyuping/revenue-calculator-backend-employee.git
   cd revenue-calculator-backend-employee
   ```

2. **Database setup**

   ```bash
   # Create PostgreSQL database
   createdb asatex-revenue
   ```


4. **Application configuration**

   Edit `src/main/resources/application.properties`:

   ```properties
   # Database configuration
   spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
   spring.r2dbc.username=your_username
   spring.r2dbc.password=your_password
   
   
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

### 🌐 API Documentation Features

- **Complete Swagger/OpenAPI 3 documentation**
- **Interactive API testing interface**
- **Detailed request/response examples**
- **Parameter validation descriptions**
- **Error code explanations**

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

#### 5. Get Employees with Pagination

```http
GET /api/v1/employee/paged?page={page}&size={size}&sortBy={sortBy}&sortDirection={sortDirection}
```

**Parameters:**

- `page` (query parameter): Page number, starting from 0 (default: 0)
- `size` (query parameter): Page size (default: 10, max: 100)
- `sortBy` (query parameter): Sort field (default: employeeId)
- `sortDirection` (query parameter): Sort direction (ASC/DESC, default: ASC)

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

### Monitoring Endpoints

#### Cache Monitoring

```http
GET /api/v1/monitoring/cache/stats
DELETE /api/v1/monitoring/cache/clear
DELETE /api/v1/monitoring/cache/clear/{cacheName}
```

#### Database Audit

```http
GET /api/v1/audit/database/stats
GET /api/v1/audit/database/logs/operation/{operationType}
GET /api/v1/audit/database/logs/table/{tableName}
GET /api/v1/audit/database/logs/user/{userId}
GET /api/v1/audit/database/logs/time-range?startTime={startTime}&endTime={endTime}
DELETE /api/v1/audit/database/logs/cleanup?retentionDays={retentionDays}
```

#### Transaction Monitoring

```http
GET /api/v1/monitoring/transaction/stats
```

#### Log Monitoring

```http
GET /api/v1/monitoring/logs/stats
GET /api/v1/monitoring/logs/health
POST /api/v1/monitoring/logs/reset
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
- **Cache Tests** - Cache functionality testing
- **Rate Limiting Tests** - Rate limiting testing
- **Transaction Tests** - Transaction management testing
- **Audit Tests** - Database audit testing

## 🔧 Configuration

### Application Configuration (application.properties)

```properties
# Server configuration
# Development environment uses port 9001, production uses port 8080 (via PORT environment variable)
server.port=9001

# Database configuration
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
spring.r2dbc.username=db_user
spring.r2dbc.password=${DB_PASSWORD}


# Flyway configuration
spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
spring.flyway.user=db_user
spring.flyway.password=${DB_PASSWORD}
spring.flyway.baseline-on-migrate=true


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

**Development Environment Variables:**
- `DB_PASSWORD` - Database password

**Production Environment Variables:**
- `PORT` - Server port (default: 8080)
- `DB_URL` - Database connection URL
- `DB_USER` - Database username
- `DB_PASSWORD` - Database password
- `FLYWAY_URL` - Flyway database connection URL
- `DB_POOL_MAX_SIZE` - Database connection pool max size (default: 10)
- `DB_POOL_MAX_IDLE_TIME` - Connection pool max idle time (default: PT10M)
- `DB_POOL_MAX_LIFE_TIME` - Connection pool max life time (default: PT30M)
- `DB_POOL_INITIAL_SIZE` - Connection pool initial size (default: 2)

### Production Environment Configuration

Production environment uses `application-prod.properties` configuration file:

```properties
# Production server configuration
server.port=${PORT:8080}

# Production database configuration
spring.r2dbc.url=${DB_URL}
spring.r2dbc.username=${DB_USER}
spring.r2dbc.password=${DB_PASSWORD}


# Production database connection pool configuration
spring.r2dbc.pool.max-size=${DB_POOL_MAX_SIZE:10}
spring.r2dbc.pool.max-idle-time=${DB_POOL_MAX_IDLE_TIME:PT10M}
spring.r2dbc.pool.max-life-time=${DB_POOL_MAX_LIFE_TIME:PT30M}
spring.r2dbc.pool.initial-size=${DB_POOL_INITIAL_SIZE:2}
```

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
- `transaction.start` - Transaction start count
- `transaction.commit` - Transaction commit count
- `transaction.rollback` - Transaction rollback count
- `transaction.error` - Transaction error count
- `logs.audit` - Audit log count
- `logs.security` - Security log count
- `logs.performance` - Performance log count
- `logs.error` - Error log count

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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);
```

#### database_audit_logs table

```sql
CREATE TABLE database_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(20) NOT NULL,
    table_name VARCHAR(100) NOT NULL,
    record_id VARCHAR(100),
    user_id VARCHAR(100),
    session_id VARCHAR(100),
    request_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    old_values TEXT,
    new_values TEXT,
    sql_statement TEXT,
    execution_time_ms BIGINT,
    affected_rows INTEGER,
    error_message TEXT,
    operation_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100)
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
- `V4__Add_soft_delete_columns.sql` - Add soft delete support
- `V5__Create_database_audit_logs_table.sql` - Create audit logs table

## 🚀 Deployment

### Docker Deployment

1. **Build and Run**

   ```bash
   # Development environment (port 9001)
   ./gradlew build
   docker build -t revenue-calculator-employee .
   docker run -p 9001:8080 -e SPRING_PROFILES_ACTIVE=default revenue-calculator-employee
   
   # Production environment (port 8080)
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod revenue-calculator-employee
   ```

### Production Deployment

#### Google Cloud Run Deployment

This section provides comprehensive production deployment instructions for Google Cloud Run.

##### Prerequisites

- Google Cloud Project with billing enabled
- Cloud SQL PostgreSQL instance
- Service account with appropriate permissions

##### Method 1: Cloud Run Console Deployment

1. **Open Cloud Run Console**
   - Visit [Google Cloud Console](https://console.cloud.google.com/)
   - Select your project
   - Navigate to **Cloud Run**

2. **Create New Service**
   - Click **"Create Service"**
   - Select **"Deploy one container from source"**

3. **Configure Source Code**
   ```
   Source: Deploy from source repository
   Repository type: GitHub
   Repository: Select your GitHub repository
   Branch: main
   Build type: Dockerfile
   Dockerfile path: /Dockerfile
   ```

4. **Configure Service Settings**
   ```
   Service name: revenue-calculator-employee
   Region: your-region
   CPU allocation: CPU is only allocated during request processing
   Minimum instances: 1
   Maximum instances: 10
   ```

5. **Configure Container Settings**
   ```
   Port: 9001
   Memory: 1 GiB (recommended) or 2 GiB (if memory issues persist)
   CPU: 2
   Request timeout: 300 seconds
   Startup timeout: 300 seconds
   ```

6. **Configure Environment Variables**
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

7. **Configure VPC Connection**
   - In **"Connections"** section
   - Click **"Add VPC connector"**
   - Select your VPC connector for Cloud SQL access

8. **Configure Authentication**
   - In **"Security"** section
   - Service account: `your-service-account@your-project.iam.gserviceaccount.com`
   - Allow unauthenticated invocations: **Yes**

9. **Deploy Service**
   - Click **"Create"**
   - Wait for build and deployment to complete (usually 10-15 minutes)

##### Method 2: Command Line Deployment

1. **Build Docker Image**
   ```bash
   # Build image
   docker build -t gcr.io/your-project-id/revenue-calculator-backend-employee .
   
   # Push image to Google Container Registry
   docker push gcr.io/your-project-id/revenue-calculator-backend-employee
   ```

2. **Deploy to Cloud Run**
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

##### Environment Variables Configuration

**Required Environment Variables:**
```bash
# Application configuration
SPRING_PROFILES_ACTIVE=prod

# Database configuration (Cloud SQL + VPC connection)
DB_URL=r2dbc:postgresql://your-db-host:5432/asatex-revenue
DB_USER=your-db-username
DB_PASSWORD=your-db-password
FLYWAY_URL=jdbc:postgresql://your-db-host:5432/asatex-revenue

```

**Optional Environment Variables:**
```bash
# Database connection pool configuration
DB_POOL_MAX_SIZE=5
DB_POOL_MAX_IDLE_TIME=PT5M
DB_POOL_MAX_LIFE_TIME=PT15M
```

##### Database Configuration

**VPC Connection Setup:**

1. **Create VPC Connector:**
   ```bash
   # Create a VPC connector for Cloud Run to access Cloud SQL
   gcloud compute networks vpc-access connectors create your-vpc-connector \
     --region=your-region \
     --subnet=your-subnet \
     --subnet-project=your-project-id \
     --min-instances=2 \
     --max-instances=3
   ```

2. **Database User Setup:**
   ```sql
   -- Create database user with password authentication
   CREATE USER your-db-username WITH PASSWORD 'your-db-password';
   GRANT ALL PRIVILEGES ON DATABASE asatex_revenue TO your-db-username;
   GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your-db-username;
   GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your-db-username;
   ```

##### Deployment Verification

1. **Check Service Status**
   - In Cloud Run console, confirm service status is **"Running"**

2. **Test Health Check**
   ```bash
   # Get service URL
   SERVICE_URL=$(gcloud run services describe revenue-calculator-employee \
       --region=your-region \
       --format="value(status.url)")
   
   # Test health check
   curl $SERVICE_URL/actuator/health
   
   # Test database connection
   curl $SERVICE_URL/actuator/health/db
   ```

3. **Access API Documentation**
   - Open browser: `$SERVICE_URL/swagger-ui.html`

##### Update Deployment

1. In Cloud Run console, click service name
2. Click **"Edit and Deploy New Revision"**
3. In **"Source"** section, click **"Rebuild"**
4. Click **"Deploy"**

##### Troubleshooting

**Common Issues:**

1. **Build Failure**
   - Check Dockerfile syntax
   - Verify all dependencies are installed
   - Review build logs

2. **Deployment Failure**
   - Check Cloud SQL connection configuration
   - Verify service account permissions
   - Check environment variables configuration

3. **Application Won't Start**
   - View Cloud Run logs
   - Check database connection

**Useful Commands:**
```bash
# View service logs
gcloud run services logs read revenue-calculator-employee --region=your-region

# View service details
gcloud run services describe revenue-calculator-employee --region=your-region

# View build logs
gcloud builds list --limit=5
```

##### Security Considerations

1. **VPC Security**: Ensure VPC connector is properly configured and secured
2. **Environment Variables**: When setting environment variables in Cloud Run, ensure sensitive information is not exposed in logs
3. **Network Access**: Ensure Cloud Run service can access Cloud SQL instance through VPC
4. **Firewall Rules**: Check Cloud SQL firewall rules to allow connections from VPC
5. **Database Security**: Use strong passwords and limit database user permissions
6. **VPC Connector**: Ensure VPC connector has appropriate network access controls

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

- **Employee Information Cache**: 30 minutes TTL
- **Employee Search Cache**: 15 minutes TTL
- **Pagination Cache**: 10 minutes TTL
- **Automatic Cache Invalidation**: Clear related cache on write operations

### Reactive Programming

- Fully non-blocking I/O
- Back pressure handling
- Efficient resource utilization

### Database Optimization

- Connection pool configuration
- Query optimization
- Index optimization
- Comprehensive audit logging with minimal performance impact

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

- Project Link: [https://github.com/niuyuping/revenue-calculator-backend-employee](https://github.com/niuyuping/revenue-calculator-backend-employee)
- Issue Reports: [https://github.com/niuyuping/revenue-calculator-backend-employee/issues](https://github.com/niuyuping/revenue-calculator-backend-employee/issues)
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

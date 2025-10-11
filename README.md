# Employee Management Microservice

A reactive employee management system built with Spring Boot 3.x, R2DBC, and WebFlux.

## 🚀 Quick Start

### Prerequisites

- Java 21+
- PostgreSQL 12+
- Gradle 8.0+

### Run the Application

```bash
./gradlew bootRun
```

### Access API Documentation

- **Swagger UI**: <http://localhost:9001/swagger-ui.html>
- **Health Check**: <http://localhost:9001/actuator/health>

## 📋 Features

- ✅ **Employee CRUD Operations** - Complete employee management
- ✅ **Search & Pagination** - Search by name with paginated results
- ✅ **Data Validation** - Comprehensive input validation
- ✅ **Rate Limiting** - API protection with Resilience4j
- ✅ **Caching** - High-performance in-memory caching
- ✅ **Monitoring** - Health checks and custom metrics
- ✅ **API Documentation** - Complete Swagger/OpenAPI docs

## 🏗️ Architecture

```text
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Controller    │───▶│  Application     │───▶│    Service      │
│   (WebFlux)     │    │     Layer        │    │   (Business)    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Validation    │    │   Transaction    │    │   Repository    │
│   & Exception   │    │   Management     │    │    (R2DBC)      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## 🛠️ Technology Stack

- **Java 21** + **Spring Boot 3.5.6**
- **PostgreSQL** + **Flyway** (Database)
- **WebFlux** + **R2DBC** (Reactive)
- **Caffeine** (Caching)
- **Resilience4j** (Rate Limiting)
- **Swagger/OpenAPI 3** (Documentation)
- **JUnit 5** + **TestContainers** (Testing)

## 📊 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/employee` | Get paginated employee list |
| `POST` | `/api/v1/employee` | Create new employee |
| `GET` | `/api/v1/employee/{id}` | Get employee by ID |
| `PUT` | `/api/v1/employee/{id}` | Update employee |
| `DELETE` | `/api/v1/employee/{id}` | Delete employee |
| `GET` | `/api/v1/employee/search/name` | Search by name |
| `GET` | `/api/v1/employee/number/{number}` | Get by employee number |

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

**Test Results**: 232 tests passing (100% success rate)

## 🐳 Docker Deployment

```bash
# Build and run
./gradlew build
docker build -t employee-service .
docker run -p 9001:8080 employee-service
```

## 📈 Monitoring

- **Health**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`
- **Custom Metrics**: Business operation counters and timers

## 🔧 Configuration

### Profiles

- `default` - Development (port 9001)
- `prod` - Production (port 8080)
- `test` - Testing with TestContainers

### Key Properties

```properties
# Database
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/employee_db
spring.flyway.enabled=true

# Server
server.port=9001

# Monitoring
management.endpoints.web.exposure.include=health,info,metrics
```

## 📝 Recent Updates (v1.1.0)

- ✅ Added Application Layer for better architecture
- ✅ Unified monitoring services and controllers
- ✅ Enhanced parameter validation
- ✅ Fixed all Spring configuration warnings
- ✅ Optimized default sorting and pagination
- ✅ Removed unused code and configurations

## 📞 Contact

- **Developer**: 牛宇平 (Niuyuping)
- **Email**: <niuyuping@asatex.jp>
- **Company**: 株式会社アサテクス (Asatex Co., Ltd.)

---

**Version**: v1.1.0 | **Last Updated**: October 2025 | **Java**: 21+ | **Spring Boot**: 3.5.6+

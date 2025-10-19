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

- ✅ **Employee CRUD Operations** - Complete employee management with email and salary
- ✅ **Search & Pagination** - Search by name with paginated results
- ✅ **Data Validation** - Comprehensive input validation including email format
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
| `GET` | `/api/v1/employee/search/furigana` | Search by furigana |
| `GET` | `/api/v1/employee/number/{number}` | Get by employee number |
| `DELETE` | `/api/v1/employee/number/{number}` | Delete by employee number |

## 📝 Employee Data Model

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `employeeId` | Long | Unique identifier | Auto-generated |
| `employeeNumber` | String | Employee number | Required, 1-20 chars, alphanumeric |
| `name` | String | Full name | Required, 1-100 chars |
| `furigana` | String | Japanese pronunciation | Optional, 0-200 chars, hiragana/katakana |
| `birthday` | LocalDate | Date of birth | Optional, past date |
| `email` | String | Email address | Optional, valid email format, max 255 chars |
| `basicSalary` | BigDecimal | Monthly salary in JPY | Optional, non-negative, max 10 digits |
| `dependentCount` | Integer | Number of dependents | Optional, non-negative |
| `healthInsuranceEnrolled` | Boolean | Health insurance enrollment | Optional, true/false |
| `welfarePensionEnrolled` | Boolean | Welfare pension enrollment | Optional, true/false |
| `unitPrice` | BigDecimal | Unit price per hour/day | Optional, positive, max 10 digits |
| `individualBusinessAmount` | BigDecimal | Individual business request amount | Optional, non-negative, max 10 digits |
| `positionAllowance` | BigDecimal | Position allowance amount | Optional, non-negative, max 10 digits |
| `housingAllowance` | BigDecimal | Housing allowance amount | Optional, non-negative, max 10 digits |
| `familyAllowance` | BigDecimal | Family allowance amount | Optional, non-negative, max 10 digits |
| `collectionFeeAmount` | BigDecimal | Collection fee amount | Optional, non-negative, max 10 digits |
| `paymentFeeAmount` | BigDecimal | Payment fee amount | Optional, non-negative, max 10 digits |
| `thirdPartyManagementRate` | BigDecimal | Third party management rate | Optional, 0.00-100.00 |
| `thirdPartyProfitDistributionRate` | BigDecimal | Third party profit distribution rate | Optional, 0.00-100.00 |
| `phoneNumber` | String | Phone number with country code | Optional, max 20 chars, alphanumeric with special chars |
| `consumptionTaxRate` | BigDecimal | Consumption tax rate percentage | Optional, 0.00-100.00 |
| `nonWorkingDeduction` | BigDecimal | Non-working deduction amount in JPY | Optional, non-negative, max 10 digits |
| `overtimeAllowance` | BigDecimal | Overtime allowance amount in JPY | Optional, non-negative, max 10 digits |
| `commutingAllowance` | BigDecimal | Commuting allowance amount in JPY | Optional, non-negative, max 10 digits |
| `remarks` | String | Additional remarks or notes | Optional, max 1000 chars |
| `isDisabled` | Boolean | Whether the employee is disabled (for tax deduction) | Optional, true/false |
| `isSingleParent` | Boolean | Whether the employee is a single parent (for tax deduction) | Optional, true/false |
| `isWidow` | Boolean | Whether the employee is a widow (for tax deduction) | Optional, true/false |
| `isWorkingStudent` | Boolean | Whether the employee is a working student (for tax deduction) | Optional, true/false |
| `disabledDependentCount` | Integer | Number of disabled dependents (for tax deduction) | Optional, non-negative |

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

## 📝 Recent Updates (v1.5.0)

- ✅ Added phone number field with international format support
- ✅ Added consumption tax rate field (0.00-100.00%)
- ✅ Added non-working deduction amount field
- ✅ Added overtime allowance amount field
- ✅ Added commuting allowance amount field
- ✅ Added remarks field for additional notes (max 1000 chars)
- ✅ Enhanced data validation for all new fields
- ✅ Updated database schema with new employee information fields
- ✅ Improved API documentation with detailed field descriptions and examples
- ✅ Added comprehensive test coverage for all new fields
- ✅ Updated Swagger/OpenAPI documentation with new field examples

## 📝 Previous Updates (v1.4.0)

- ✅ Added comprehensive allowance and fee fields (position, housing, family allowances)
- ✅ Added collection and payment fee amount fields
- ✅ Added third party management and profit distribution rate fields
- ✅ Enhanced data validation for all new monetary and rate fields
- ✅ Updated database schema with comprehensive allowance and fee tracking
- ✅ Improved API documentation with detailed field descriptions and examples
- ✅ Added comprehensive test coverage for all new fields
- ✅ Updated Swagger/OpenAPI documentation with new field examples

## 📝 Previous Updates (v1.3.0)

- ✅ Added insurance and payment fields (dependent count, health insurance, welfare pension)
- ✅ Added unit price and individual business amount fields
- ✅ Enhanced data validation for all new monetary fields
- ✅ Updated database schema with comprehensive insurance and payment tracking
- ✅ Improved API documentation with detailed field descriptions
- ✅ Added comprehensive test coverage for all new fields
- ✅ Updated Swagger/OpenAPI documentation with new field examples

## 📝 Recent Updates (v1.6.0)

- ✅ Added 5 new deduction target fields: isDisabled, isSingleParent, isWidow, isWorkingStudent, disabledDependentCount
- ✅ Enhanced tax deduction calculation support for Japanese tax system
- ✅ Updated database schema with V15 migration
- ✅ Improved API documentation with deduction field descriptions
- ✅ Updated Swagger/OpenAPI documentation

## 📝 Previous Updates (v1.2.0)

- ✅ Added email and basic salary fields to employee model
- ✅ Enhanced data validation with email format checking
- ✅ Updated database schema with new fields
- ✅ Improved API documentation with new field descriptions
- ✅ Added comprehensive test coverage for new fields
- ✅ Updated Swagger/OpenAPI documentation

## 📝 Previous Updates (v1.1.0)

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

**Version**: v1.6.0 | **Last Updated**: December 2024 | **Java**: 21+ | **Spring Boot**: 3.5.6+

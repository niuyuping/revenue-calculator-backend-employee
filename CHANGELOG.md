# Changelog

All notable changes to the Employee Management Microservice project.

## [1.1.0] - 2025-10-11

### 🏗️ Architecture
- Added Application Layer (`EmployeeApplicationService`)
- Unified monitoring services and controllers
- Moved common classes to `common` package

### 🧪 Testing & Quality
- 232 tests passing (100% success rate)
- Renamed exception classes to use `Handler` suffix
- Enhanced parameter validation (400 instead of 500 errors)
- Fixed `ValidationConfig` BeanPostProcessor warnings

### 🚀 Performance
- Changed default sort field to `employeeId`
- Unified pagination endpoint (removed `/paged`)
- Improved transaction exception handling
- Optimized Spring context loading

### 🛠️ Technical Debt
- Removed unused rate limiters and configurations
- Added missing Caffeine dependency
- Centralized rate limiting configuration
- Eliminated all Spring warnings

### 🔧 Bug Fixes
- Fixed ApplicationContext loading errors
- Fixed pagination response handling in tests
- Fixed endpoint references in integration tests
- Fixed method signature mismatches

## [1.0.0] - 2024-12-01

### 🎉 Initial Release
- Complete CRUD operations
- Reactive programming with WebFlux + R2DBC
- API documentation with Swagger/OpenAPI 3
- Data validation with Jakarta Validation
- Rate limiting with Resilience4j
- Monitoring with Actuator endpoints
- Comprehensive test suite with TestContainers
- Docker deployment support
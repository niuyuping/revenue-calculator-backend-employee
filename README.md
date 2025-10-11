# Employee Management Microservice (Revenue Calculator Backend Employee)

A reactive employee management system backend service based on Spring Boot 3.x, R2DBC, and WebFlux with comprehensive enterprise features.

## 🌐 Language Selection / 语言选择 / 言語選択

Choose your preferred language to view the complete documentation:

### 🇨🇳 中文版 (Chinese)

[**README_ZH.md**](README_ZH.md) - 完整的中文文档

基于 Spring Boot 3.x、R2DBC、WebFlux 的响应式员工管理系统后端服务。包含完整的CRUD操作、搜索功能、分页查询、数据验证、缓存机制、限流保护、事务管理等企业级特性。

### 🇺🇸 English Version

[**README_EN.md**](README_EN.md) - Complete English documentation

A reactive employee management system backend service based on Spring Boot 3.x, R2DBC, and WebFlux. Features complete CRUD operations, search functionality, pagination support, data validation, caching mechanism, rate limiting, transaction management, and more.

### 🇯🇵 日本語版 (Japanese)

[**README_JA.md**](README_JA.md) - 完全な日本語文書

Spring Boot 3.x、R2DBC、WebFluxをベースとしたリアクティブ従業員管理システムのバックエンドサービス。完全なCRUD操作、検索機能、ページネーション、データ検証、キャッシュ機能、レート制限、トランザクション管理などの機能を提供します。

---

## 🚀 Quick Overview / 快速概览 / クイック概要

### Key Features / 主要特性 / 主要機能

- ✅ **Complete API Documentation** / **完整API文档** / **完全なAPI文書** (Swagger/OpenAPI 3)
- ✅ **Reactive Programming** / **响应式编程** / **リアクティブプログラミング** (WebFlux + R2DBC)
- ✅ **Employee CRUD Operations** / **员工CRUD操作** / **従業員CRUD操作**
- ✅ **Advanced Search** / **高级搜索** / **高度な検索** (Name Search)
- ✅ **Pagination Support** / **分页支持** / **ページネーションサポート** (Sorted & Paginated)
- ✅ **Data Validation** / **数据验证** / **データ検証** (Jakarta Validation)
- ✅ **Rate Limiting** / **限流保护** / **レート制限** (Resilience4j)
- ✅ **Transaction Management** / **事务管理** / **トランザクション管理** (ACID compliance)
- ✅ **Monitoring & Metrics** / **监控指标** / **監視・メトリクス** (Actuator + Custom metrics)

### Technology Stack / 技术栈 / 技術スタック

- **Java 21** + **Spring Boot 3.5.6**
- **PostgreSQL** + **Flyway**
- **Swagger/OpenAPI 3**
- **JUnit 5** + **TestContainers**

### Quick Start / 快速开始 / クイックスタート

1. **Prerequisites / 前提条件 / 前提条件**:
   - Java 21+, PostgreSQL 12+, Gradle 8.0+

2. **Run the application / 运行应用 / アプリケーション実行**:

   ```bash
   ./gradlew bootRun
   ```

3. **Access API Documentation / 访问API文档 / API文書アクセス**:
   - **Swagger UI**: <http://localhost:9001/swagger-ui.html>
   - **Complete API docs**: See language-specific README files above

4. **Test the API / 测试API / APIテスト**:

   ```bash
   curl http://localhost:9001/api/v1/employee/health
   ```

## 🚀 Deployment / 部署 / デプロイ

### Docker Deployment / Docker部署 / Dockerデプロイ

1. **Build and Run / 构建运行 / ビルドと実行**:

   ```bash
   # Development / 开发环境 / 開発環境 (port 9001)
   ./gradlew build
   docker build -t revenue-calculator-employee .
   docker run -p 9001:8080 -e SPRING_PROFILES_ACTIVE=default revenue-calculator-employee
   
   # Production / 生产环境 / 本番環境 (port 8080)
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod revenue-calculator-employee
   ```

### Production Deployment / 生产环境部署 / 本番環境デプロイ

For detailed production deployment instructions, please refer to the language-specific README files:

**详细的生产环境部署说明，请参考对应语言的README文件：**

**詳細な本番環境デプロイ手順については、対応する言語のREADMEファイルを参照してください：**

- 🇺🇸 [English Deployment Guide](README_EN.md#-production-deployment)
- 🇨🇳 [中文部署指南](README_ZH.md#-生产环境部署)
- 🇯🇵 [日本語デプロイガイド](README_JA.md#-本番環境デプロイ)

---

## 📞 Contact / 联系方式 / 連絡先

- **Company / 公司 / 会社**: 株式会社アサテクス (Asatex Co., Ltd.)
- **Developer / 开发者 / 開発者**: 牛宇平 (Niuyuping)
- **Email / 邮箱 / メール**: <niuyuping@asatex.jp>
- **Demo Website / 演示网站 / デモサイト**: [revenue.asatex.jp](https://revenue.asatex.jp)

---

**Version / 版本 / バージョン**: v1.0.0  
**Last Updated / 最后更新 / 最終更新**: December 2024  
**Compatibility / 兼容性 / 互換性**: Java 21+, Spring Boot 3.5.6+

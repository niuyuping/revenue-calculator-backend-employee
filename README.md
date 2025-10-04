# Employee Management Microservice (Revenue Calculator Backend Employee)

A reactive employee management system backend service based on Spring Boot 3.x, R2DBC, and WebFlux.

## 🌐 Language Selection / 语言选择 / 言語選択

Choose your preferred language to view the documentation:

### 🇨🇳 中文版 (Chinese)

[**README_ZH.md**](README_ZH.md) - 完整的中文文档

基于 Spring Boot 3.x、R2DBC、WebFlux 的响应式员工管理系统后端服务。支持英、中、日三种语言的API文档，包含完整的CRUD操作、搜索功能、数据验证、缓存支持、限流保护等特性。

### 🇺🇸 English Version

[**README_EN.md**](README_EN.md) - Complete English documentation

A reactive employee management system backend service based on Spring Boot 3.x, R2DBC, and WebFlux. Features multi-language API documentation (English, Chinese, Japanese), complete CRUD operations, search functionality, data validation, cache support, rate limiting, and more.

### 🇯🇵 日本語版 (Japanese)

[**README_JA.md**](README_JA.md) - 完全な日本語文書

Spring Boot 3.x、R2DBC、WebFluxをベースとしたリアクティブ従業員管理システムのバックエンドサービス。多言語API文書（英語、中国語、日本語）、完全なCRUD操作、検索機能、データ検証、キャッシュサポート、レート制限などの機能を提供します。

---

## 🚀 Quick Overview / 快速概览 / クイック概要

### Key Features / 主要特性 / 主要機能

- ✅ **Multi-language API Documentation** / **多语言API文档** / **多言語API文書** (EN/CN/JA)
- ✅ **Reactive Programming** / **响应式编程** / **リアクティブプログラミング** (WebFlux + R2DBC)
- ✅ **Employee CRUD Operations** / **员工CRUD操作** / **従業員CRUD操作**
- ✅ **Advanced Search** / **高级搜索** / **高度な検索** (Name & Furigana)
- ✅ **Data Validation** / **数据验证** / **データ検証** (Jakarta Validation)
- ✅ **Cache Support** / **缓存支持** / **キャッシュサポート** (Redis)
- ✅ **Rate Limiting** / **限流保护** / **レート制限** (Resilience4j)
- ✅ **Monitoring & Metrics** / **监控指标** / **監視・メトリクス** (Actuator)

### Technology Stack / 技术栈 / 技術スタック

- **Java 21** + **Spring Boot 3.5.6**
- **PostgreSQL** + **Redis** + **Flyway**
- **Swagger/OpenAPI 3** + **Spring Boot i18n**
- **JUnit 5** + **TestContainers**

### Quick Start / 快速开始 / クイックスタート

1. **Prerequisites / 前提条件 / 前提条件**:
   - Java 21+, PostgreSQL 12+, Redis 6+, Gradle 8.0+

2. **Run the application / 运行应用 / アプリケーション実行**:

   ```bash
   ./gradlew bootRun
   ```

3. **Access API Documentation / 访问API文档 / API文書アクセス**:
   - **Swagger UI**: <http://localhost:9001/swagger-ui.html>
   - **Multi-language docs**: See language-specific README files above

4. **Test the API / 测试API / APIテスト**:

   ```bash
   curl http://localhost:9001/api/v1/employee/health
   ```

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

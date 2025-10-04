# 従業員管理マイクロサービス (Revenue Calculator Backend Employee)

Spring Boot 3.x、R2DBC、WebFluxをベースとしたリアクティブ従業員管理システムのバックエンドサービス。

## 🚀 技術スタック

- **Java 21** - プログラミング言語
- **Spring Boot 3.5.6** - アプリケーションフレームワーク
- **Spring WebFlux** - リアクティブWebフレームワーク
- **Spring Data R2DBC** - リアクティブデータベースアクセス
- **PostgreSQL** - リレーショナルデータベース
- **Redis** - キャッシュデータベース
- **Flyway** - データベースマイグレーションツール
- **Jakarta Validation** - データ検証
- **Spring Boot Actuator** - アプリケーション監視
- **Resilience4j** - レート制限とサーキットブレーカー
- **Swagger/OpenAPI 3** - API文書
- **Spring Boot i18n** - 国際化サポート
- **Gradle** - ビルドツール
- **JUnit 5** - テストフレームワーク
- **Mockito** - モックテストフレームワーク
- **TestContainers** - 統合テスト

## 📋 機能

### コア機能
- ✅ **従業員CRUD操作** - 従業員情報の作成、読み取り、更新、削除
- ✅ **従業員検索** - 姓名とふりがなによる検索
- ✅ **データ検証** - 完全な入力データ検証と制約
- ✅ **例外処理** - 統一された例外処理とエラーレスポンス
- ✅ **リアクティブプログラミング** - 完全非ブロッキングリアクティブアーキテクチャ
- ✅ **キャッシュサポート** - パフォーマンス向上のためのRedisキャッシュ
- ✅ **APIレート制限** - Resilience4jレート制限保護
- ✅ **監視メトリクス** - 完全なビジネスとパフォーマンス監視
- ✅ **多言語文書** - 英語、中国語、日本語のAPI文書

### データ検証
- 従業員番号フォーマット検証（英数字、アンダースコア、ハイフン）
- 姓名長さ検証（1-100文字）
- ふりがなフォーマット検証（ひらがな、カタカナ、ラテン文字、スペース、括弧）
- 生年月日検証（過去の日付である必要がある）
- パスパラメータとクエリパラメータ検証

### 監視とヘルスチェック
- Spring Boot Actuator統合
- ヘルスチェックエンドポイント
- Flywayマイグレーション状態監視
- アプリケーション情報エンドポイント
- カスタムビジネスメトリクス
- パフォーマンス監視メトリクス

## 🏗️ プロジェクト構造

```
src/
├── main/
│   ├── java/jp/asatex/revenue_calculator_backend_employee/
│   │   ├── config/           # 設定クラス
│   │   │   ├── CacheConfig.java
│   │   │   ├── InternationalizationConfig.java
│   │   │   ├── LoggingConfig.java
│   │   │   ├── MetricsConfig.java
│   │   │   ├── MultiLanguageOpenApiConfig.java
│   │   │   ├── RateLimitConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── ValidationConfig.java
│   │   ├── controller/       # RESTコントローラー
│   │   │   └── EmployeeController.java
│   │   ├── dto/             # データ転送オブジェクト
│   │   │   └── EmployeeDto.java
│   │   ├── entity/          # エンティティクラス
│   │   │   └── Employee.java
│   │   ├── exception/       # 例外処理
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   └── DuplicateEmployeeNumberException.java
│   │   ├── repository/      # データアクセス層
│   │   │   └── EmployeeRepository.java
│   │   ├── service/         # ビジネスロジック層
│   │   │   └── EmployeeService.java
│   │   ├── util/            # ユーティリティクラス
│   │   │   └── LoggingUtil.java
│   │   └── RevenueCalculatorBackendEmployeeApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       ├── messages.properties          # 英語リソースファイル
│       ├── messages_zh_CN.properties    # 中国語リソースファイル
│       ├── messages_ja.properties       # 日本語リソースファイル
│       ├── static/
│       │   └── swagger-ui-custom.css    # Swagger UIカスタムスタイル
│       └── db/migration/    # データベースマイグレーションスクリプト
│           ├── V1__Create_employees_table.sql
│           ├── V2__Insert_initial_employee_data.sql
│           └── V3__Add_constraints_to_employees_table.sql
└── test/                    # テストコード
    ├── java/jp/asatex/revenue_calculator_backend_employee/
    │   ├── config/          # 設定テスト
    │   ├── controller/      # コントローラーテスト
    │   ├── exception/       # 例外処理テスト
    │   ├── integration/     # 統合テスト
    │   ├── repository/      # データアクセステスト
    │   ├── service/         # ビジネスロジックテスト
    │   ├── util/            # ユーティリティクラステスト
    │   └── validation/      # 検証テスト
    └── resources/
        └── application-test.properties
```

## 🚀 クイックスタート

### 前提条件

- Java 21+
- PostgreSQL 12+
- Redis 6+
- Gradle 8.0+

### インストールと実行

1. **プロジェクトのクローン**
   ```bash
   git clone <repository-url>
   cd revenue-calculator-backend-employee
   ```

2. **データベース設定**
   ```bash
   # PostgreSQLデータベースの作成
   createdb asatex-revenue
   ```

3. **Redis設定**
   ```bash
   # Redisサービスの開始
   redis-server
   ```

4. **アプリケーション設定**
   
   `src/main/resources/application.properties`を編集:
   ```properties
   # データベース設定
   spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
   spring.r2dbc.username=your_username
   spring.r2dbc.password=your_password
   
   # Redis設定
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   
   # Flyway設定
   spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
   spring.flyway.user=your_username
   spring.flyway.password=your_password
   ```

5. **アプリケーションの実行**
   ```bash
   ./gradlew bootRun
   ```

6. **動作確認**
   ```bash
   curl http://localhost:9001/api/v1/employee/health
   ```

## 📚 API文書

### Swagger UIアクセス
アプリケーション起動後、以下のリンクからSwagger UIにアクセスできます：

- **Swagger UI**: http://localhost:9001/swagger-ui.html
- **OpenAPI JSON**: http://localhost:9001/v3/api-docs
- **Swagger設定**: http://localhost:9001/v3/api-docs/swagger-config

### 🌐 多言語サポート
API文書は3つの言語をサポートし、以下の方法で切り替えできます：

#### 言語切り替え方法
1. **Accept-Languageヘッダーを使用**：
   ```bash
   # 英語
   curl -H "Accept-Language: en" http://localhost:9001/v3/api-docs
   
   # 中国語
   curl -H "Accept-Language: zh-CN" http://localhost:9001/v3/api-docs
   
   # 日本語
   curl -H "Accept-Language: ja" http://localhost:9001/v3/api-docs
   ```

2. **Swagger UIグループを使用**：
   - **英語文書**: http://localhost:9001/swagger-ui.html?urls.primaryName=english
   - **中国語文書**: http://localhost:9001/swagger-ui.html?urls.primaryName=chinese
   - **日本語文書**: http://localhost:9001/swagger-ui.html?urls.primaryName=japanese

#### サポート言語
- 🇺🇸 **English** - デフォルト言語
- 🇨🇳 **中国語（簡体字）** - 完全な中国語API文書
- 🇯🇵 **日本語** - 完全な日本語API文書

### ベースURL
```
http://localhost:9001/api/v1/employee
```

### エンドポイント一覧

#### 1. 全従業員取得
```http
GET /api/v1/employee
```

**レスポンス例:**
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

#### 2. IDによる従業員取得
```http
GET /api/v1/employee/{id}
```

**パラメータ:**
- `id` (パスパラメータ): 従業員ID（正数である必要がある）

#### 3. 従業員番号による従業員取得
```http
GET /api/v1/employee/number/{employeeNumber}
```

**パラメータ:**
- `employeeNumber` (パスパラメータ): 従業員番号（1-20文字、英数字アンダースコアハイフン）

#### 4. 姓名による従業員検索
```http
GET /api/v1/employee/search/name?name={name}
```

**パラメータ:**
- `name` (クエリパラメータ): 姓名キーワード（1-100文字）

#### 5. ふりがなによる従業員検索
```http
GET /api/v1/employee/search/furigana?furigana={furigana}
```

**パラメータ:**
- `furigana` (クエリパラメータ): ふりがなキーワード（1-200文字）

#### 6. 従業員作成
```http
POST /api/v1/employee
Content-Type: application/json
```

**リクエストボディ:**
```json
{
  "employeeNumber": "EMP001",
  "name": "田中太郎",
  "furigana": "タナカタロウ",
  "birthday": "1990-05-15"
}
```

#### 7. 従業員更新
```http
PUT /api/v1/employee/{id}
Content-Type: application/json
```

#### 8. IDによる従業員削除
```http
DELETE /api/v1/employee/{id}
```

#### 9. 従業員番号による従業員削除
```http
DELETE /api/v1/employee/number/{employeeNumber}
```

#### 10. ヘルスチェック
```http
GET /api/v1/employee/health
```

### エラーレスポンス形式

```json
{
  "error": "エラータイプ",
  "message": "エラー説明",
  "details": "詳細エラー情報",
  "status": 400
}
```

### HTTPステータスコード

- `200 OK` - リクエスト成功
- `201 Created` - リソース作成成功
- `204 No Content` - 削除成功
- `400 Bad Request` - リクエストパラメータエラー
- `404 Not Found` - リソースが見つからない
- `409 Conflict` - リソース競合（従業員番号重複など）
- `429 Too Many Requests` - リクエスト過多（レート制限）
- `500 Internal Server Error` - サーバー内部エラー

## 🧪 テスト

### 全テスト実行
```bash
./gradlew test
```

### 特定テストクラス実行
```bash
./gradlew test --tests "EmployeeServiceTest"
./gradlew test --tests "EmployeeRepositoryTest"
./gradlew test --tests "EmployeeIntegrationTest"
```

### テストカバレッジ
プロジェクトは包括的なテストカバレッジを持っています：
- **サービス層テスト** - ビジネスロジックテスト
- **リポジトリ層テスト** - データアクセステスト
- **コントローラー層テスト** - APIエンドポイントテスト
- **例外処理テスト** - エラーハンドリングテスト
- **パラメータ検証テスト** - 入力検証テスト
- **統合テスト** - エンドツーエンドテスト
- **設定テスト** - 設定クラステスト

## 🔧 設定

### アプリケーション設定 (application.properties)

```properties
# サーバー設定
server.port=9001

# データベース設定
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
spring.r2dbc.username=${DB_USERNAME:db_user}
spring.r2dbc.password=${DB_PASSWORD:local}

# Redis設定
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Flyway設定
spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
spring.flyway.user=${DB_USERNAME:db_user}
spring.flyway.password=${DB_PASSWORD:local}
spring.flyway.baseline-on-migrate=true

# キャッシュ設定
spring.cache.type=redis
spring.cache.redis.time-to-live=1800000

# レート制限設定
resilience4j.ratelimiter.instances.employee-api.limit-for-period=100
resilience4j.ratelimiter.instances.employee-search.limit-for-period=50
resilience4j.ratelimiter.instances.employee-write.limit-for-period=20

# Actuator設定
management.endpoints.web.exposure.include=health,info,flyway,metrics
management.endpoint.health.show-details=when-authorized
management.endpoint.flyway.enabled=true

# ログ設定
logging.level.jp.asatex.revenue_calculator_backend_employee=INFO
logging.file.name=logs/revenue-calculator-employee.log
```

### 環境変数

- `DB_USERNAME` - データベースユーザー名（デフォルト: db_user）
- `DB_PASSWORD` - データベースパスワード（デフォルト: local）

## 📊 監視

### Actuatorエンドポイント

- `GET /actuator/health` - アプリケーションヘルス状態
- `GET /actuator/info` - アプリケーション情報
- `GET /actuator/flyway` - データベースマイグレーション状態
- `GET /actuator/metrics` - システムメトリクス

### ヘルスチェックレスポンス例

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

### カスタムメトリクス

- `employee.operations.total` - 従業員操作総数
- `employee.create.total` - 従業員作成総数
- `employee.update.total` - 従業員更新総数
- `employee.delete.total` - 従業員削除総数
- `employee.query.total` - 従業員クエリ総数
- `cache.hits.total` - キャッシュヒット総数
- `cache.misses.total` - キャッシュミス総数
- `rate.limit.triggered.total` - レート制限トリガー総数

## 🗄️ データベース

### テーブル構造

#### employeesテーブル
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

### データベース制約

- 従業員番号一意性制約
- 従業員番号フォーマット制約（英数字、アンダースコア、ハイフン）
- 姓名長さ制約（1-100文字）
- ふりがな長さ制約（最大200文字）
- 生年月日制約（過去の日付である必要がある）

### マイグレーションスクリプト

プロジェクトはFlywayを使用してデータベースバージョン管理を行います：

- `V1__Create_employees_table.sql` - 従業員テーブル作成
- `V2__Insert_initial_employee_data.sql` - 初期データ挿入
- `V3__Add_constraints_to_employees_table.sql` - 制約追加

## 🚀 デプロイ

### Dockerデプロイ

1. **Dockerfile作成**
   ```dockerfile
   FROM openjdk:21-jdk-slim
   COPY build/libs/*.jar app.jar
   EXPOSE 9001
   ENTRYPOINT ["java", "-jar", "/app.jar"]
   ```

2. **ビルドと実行**
   ```bash
   ./gradlew build
   docker build -t revenue-calculator-employee .
   docker run -p 9001:9001 revenue-calculator-employee
   ```

### 本番環境設定

```properties
# 本番環境設定
spring.profiles.active=prod
server.port=9001

# データベース接続プール設定
spring.r2dbc.pool.initial-size=10
spring.r2dbc.pool.max-size=20
spring.r2dbc.pool.max-idle-time=30m

# ログ設定
logging.level.root=WARN
logging.level.jp.asatex.revenue_calculator_backend_employee=INFO
```

## 🔒 セキュリティ機能

### APIレート制限
- **一般API**: 100リクエスト/分
- **検索API**: 50リクエスト/分
- **書き込み操作API**: 20リクエスト/分

### データ検証
- 入力パラメータ検証
- SQLインジェクション防止
- XSS防止

### ログセキュリティ
- 機密情報のログ記録なし
- 分析のための構造化ログ
- セキュリティイベントログ

## 📈 パフォーマンス最適化

### キャッシュ戦略
- **従業員情報キャッシュ**: 15分TTL
- **従業員リストキャッシュ**: 5分TTL
- **自動キャッシュ無効化**: 書き込み操作時の関連キャッシュクリア

### リアクティブプログラミング
- 完全非ブロッキングI/O
- バックプレッシャー処理
- 効率的なリソース利用

### データベース最適化
- 接続プール設定
- クエリ最適化
- インデックス最適化

## 🤝 貢献

1. プロジェクトをフォーク
2. 機能ブランチを作成 (`git checkout -b feature/AmazingFeature`)
3. 変更をコミット (`git commit -m 'Add some AmazingFeature'`)
4. ブランチにプッシュ (`git push origin feature/AmazingFeature`)
5. プルリクエストを作成

### コーディング規約

- Java 21機能を使用
- Spring Bootベストプラクティスに従う
- 単体テストと統合テストを記述
- リアクティブプログラミングパターンを使用
- RESTful API設計原則に従う

## 📝 ライセンス

このプロジェクトはMITライセンスの下でライセンスされています - 詳細は[LICENSE](LICENSE)ファイルを参照してください。

## 🏢 会社情報

- **会社名**: 株式会社アサテクス (Kabushiki-gaisha Asatex / Asatex Co., Ltd.)
- **開発者**: 牛宇平 (Niuyuping)
- **メール**: niuyuping@asatex.jp
- **LINE ID**: niuyuping
- **デモサイト**: [revenue.asatex.jp](https://revenue.asatex.jp)

## 📞 連絡先

- プロジェクトリンク: [https://github.com/username/revenue-calculator-backend-employee](https://github.com/username/revenue-calculator-backend-employee)
- 問題報告: [https://github.com/username/revenue-calculator-backend-employee/issues](https://github.com/username/revenue-calculator-backend-employee/issues)
- 会社メール: niuyuping@asatex.jp
- LINE連絡: niuyuping

## 🙏 謝辞

- 優れたフレームワークを提供してくれたSpring Bootチーム
- 信頼性の高いデータベースを提供してくれたPostgreSQLコミュニティ
- サポートと提案を提供してくれたすべての貢献者

---

**バージョン**: v1.0.0  
**最終更新**: 2024年12月  
**互換性**: Java 21+, Spring Boot 3.5.6+

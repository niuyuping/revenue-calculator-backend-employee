# 従業員管理マイクロサービス (Revenue Calculator Backend Employee)

Spring Boot 3.x、R2DBC、WebFluxをベースとしたリアクティブ従業員管理システムのバックエンドサービス。包括的なエンタープライズ機能を提供します。

## 🌐 言語選択

- 🇨🇳 [中文版 (Chinese)](README_ZH.md) - 完整的中文文档
- 🇺🇸 [English Version](README_EN.md) - Complete English documentation
- 🇯🇵 **日本語版** - 完全な日本語文書（このファイル）

---

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
- **Gradle** - ビルドツール
- **JUnit 5** - テストフレームワーク
- **Mockito** - モックテストフレームワーク
- **TestContainers** - 統合テスト

## 📋 機能

### コア機能

- ✅ **従業員CRUD操作** - 従業員情報の作成、読み取り、更新、削除
- ✅ **従業員検索** - 姓名による検索
- ✅ **ページネーション** - ページネーションとソート機能付き従業員リストクエリ
- ✅ **データ検証** - 完全な入力データ検証と制約
- ✅ **例外処理** - 統一された例外処理とエラーレスポンス
- ✅ **リアクティブプログラミング** - 完全非ブロッキングリアクティブアーキテクチャ
- ✅ **キャッシュサポート** - パフォーマンス向上のためのRedisキャッシュ
- ✅ **APIレート制限** - Resilience4jレート制限保護
- ✅ **監視メトリクス** - 完全なビジネスとパフォーマンス監視
- ✅ **API文書** - 完全なSwagger/OpenAPI文書

### エンタープライズ機能

#### 🔄 キャッシュとレート制限

- **Redisキャッシュマネージャー**: マルチレベルキャッシュ戦略
- **キャッシュ戦略**:
  - 従業員情報キャッシュ: 30分TTL
  - 従業員検索キャッシュ: 15分TTL
  - ページネーションキャッシュ: 10分TTL
- **レート制限**: 異なる操作タイプに異なる制限（20-100リクエスト/分）

#### 🗄️ データベース監査

- **包括的監査トレイル**: INSERT、UPDATE、DELETE、SELECT操作
- **コンテキスト追跡**: ユーザーID、セッションID、リクエストID、IPアドレス
- **データ変更追跡**: 旧値、新値、フィールドレベル変更
- **パフォーマンス監視**: 実行時間、影響行数
- **エラー追跡**: 失敗操作の詳細エラーメッセージ

#### 🔄 トランザクション管理

- **ACID準拠**: R2DBCベースのトランザクションサポート
- **自動トランザクション管理**: `@Transactional`アノテーションサポート
- **トランザクション監視**: リアルタイムトランザクション追跡
- **パフォーマンスメトリクス**: トランザクション実行時間監視

#### 📊 包括的ログ

- **ログカテゴリ**: アプリケーション、監査、セキュリティ、パフォーマンス、エラーログ
- **構造化ログ**: コンテキスト情報を含むJSON形式
- **ログローテーション**: 自動ログローテーションと圧縮
- **保持ポリシー**: 異なるログタイプに異なる保持期間

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

```text
src/
├── main/
│   ├── java/jp/asatex/revenue_calculator_backend_employee/
│   │   ├── config/           # 設定クラス
│   │   │   ├── CacheConfig.java
│   │   │   ├── JacksonConfig.java
│   │   │   ├── MetricsConfig.java
│   │   │   ├── MultiLanguageOpenApiConfig.java
│   │   │   ├── RateLimitConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   ├── TransactionConfig.java
│   │   │   └── ValidationConfig.java
│   │   ├── controller/       # RESTコントローラー
│   │   │   └── EmployeeController.java
│   │   ├── dto/             # データ転送オブジェクト
│   │   │   ├── EmployeeDto.java
│   │   │   ├── PageRequest.java
│   │   │   ├── PageResponse.java
│   │   │   └── SortDirection.java
│   │   ├── entity/          # エンティティクラス
│   │   │   ├── Employee.java
│   │   ├── exception/       # 例外処理
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   ├── DuplicateEmployeeNumberException.java
│   │   │   └── TransactionException.java
│   │   ├── repository/      # データアクセス層
│   │   │   ├── EmployeeRepository.java
│   │   ├── service/         # ビジネスロジック層
│   │   │   ├── EmployeeService.java
│   │   │   ├── CacheMonitoringService.java
│   │   │   └── TransactionMonitoringService.java
│   │   ├── util/            # ユーティリティクラス
│   │   │   └── LoggingUtil.java
│   │   └── RevenueCalculatorBackendEmployeeApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       ├── messages.properties          # 国際化リソースファイル
│       └── db/migration/    # データベースマイグレーションスクリプト
│           ├── V1__Create_employees_table.sql
│           ├── V2__Insert_initial_employee_data.sql
│           ├── V3__Add_constraints_to_employees_table.sql
│           ├── V4__Add_soft_delete_columns.sql
│           └── V5__Create_database_audit_logs_table.sql
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
   git clone https://github.com/niuyuping/revenue-calculator-backend-employee.git
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

- **Swagger UI**: <http://localhost:9001/swagger-ui.html>
- **OpenAPI JSON**: <http://localhost:9001/v3/api-docs>
- **Swagger設定**: <http://localhost:9001/v3/api-docs/swagger-config>

### 🌐 API文書機能

- **完全なSwagger/OpenAPI 3文書**
- **インタラクティブAPIテストインターフェース**
- **詳細なリクエスト/レスポンス例**
- **パラメータ検証説明**
- **エラーコード説明**

### ベースURL

```text
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

#### 5. ページネーション付き従業員リスト取得

```http
GET /api/v1/employee/paged?page={page}&size={size}&sortBy={sortBy}&sortDirection={sortDirection}
```

**パラメータ:**

- `page` (クエリパラメータ): ページ番号、0から開始（デフォルト: 0）
- `size` (クエリパラメータ): ページサイズ（デフォルト: 10、最大: 100）
- `sortBy` (クエリパラメータ): ソートフィールド（デフォルト: employeeId）
- `sortDirection` (クエリパラメータ): ソート方向（ASC/DESC、デフォルト: ASC）

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

### 監視エンドポイント

#### キャッシュ監視

```http
GET /api/v1/monitoring/cache/stats
DELETE /api/v1/monitoring/cache/clear
DELETE /api/v1/monitoring/cache/clear/{cacheName}
```

#### データベース監査

```http
GET /api/v1/audit/database/stats
GET /api/v1/audit/database/logs/operation/{operationType}
GET /api/v1/audit/database/logs/table/{tableName}
GET /api/v1/audit/database/logs/user/{userId}
GET /api/v1/audit/database/logs/time-range?startTime={startTime}&endTime={endTime}
DELETE /api/v1/audit/database/logs/cleanup?retentionDays={retentionDays}
```

#### トランザクション監視

```http
GET /api/v1/monitoring/transaction/stats
```

#### ログ監視

```http
GET /api/v1/monitoring/logs/stats
GET /api/v1/monitoring/logs/health
POST /api/v1/monitoring/logs/reset
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
- **キャッシュテスト** - キャッシュ機能テスト
- **レート制限テスト** - レート制限機能テスト
- **トランザクションテスト** - トランザクション管理テスト
- **監査テスト** - データベース監査テスト

## 🔧 設定

### アプリケーション設定 (application.properties)

```properties
# サーバー設定
# 開発環境は9001ポート、本番環境は8080ポート（PORT環境変数で設定）
server.port=9001

# データベース設定
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/asatex-revenue
spring.r2dbc.username=db_user
spring.r2dbc.password=${DB_PASSWORD}

# Redis設定
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Flyway設定
spring.flyway.url=jdbc:postgresql://localhost:5432/asatex-revenue
spring.flyway.user=db_user
spring.flyway.password=${DB_PASSWORD}
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

**開発環境環境変数:**
- `DB_PASSWORD` - データベースパスワード

**本番環境環境変数:**
- `PORT` - サーバーポート（デフォルト: 8080）
- `DB_URL` - データベース接続URL
- `DB_USER` - データベースユーザー名
- `DB_PASSWORD` - データベースパスワード
- `FLYWAY_URL` - Flywayデータベース接続URL
- `REDIS_HOST` - Redisホストアドレス（デフォルト: localhost）
- `REDIS_PORT` - Redisポート（デフォルト: 6379）
- `REDIS_DATABASE` - Redisデータベース番号（デフォルト: 0）
- `REDIS_TIMEOUT` - Redisタイムアウト（デフォルト: 2000ms）
- `CACHE_TTL` - キャッシュ生存時間（デフォルト: 1800000ms）
- `DB_POOL_MAX_SIZE` - データベース接続プール最大サイズ（デフォルト: 10）
- `DB_POOL_MAX_IDLE_TIME` - 接続プール最大アイドル時間（デフォルト: PT10M）
- `DB_POOL_MAX_LIFE_TIME` - 接続プール最大生存時間（デフォルト: PT30M）
- `DB_POOL_INITIAL_SIZE` - 接続プール初期サイズ（デフォルト: 2）

### 本番環境設定

本番環境は `application-prod.properties` 設定ファイルを使用：

```properties
# 本番環境サーバー設定
server.port=${PORT:8080}

# 本番環境データベース設定
spring.r2dbc.url=${DB_URL}
spring.r2dbc.username=${DB_USER}
spring.r2dbc.password=${DB_PASSWORD}

# 本番環境Redis設定
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
spring.data.redis.database=${REDIS_DATABASE:0}
spring.data.redis.timeout=${REDIS_TIMEOUT:2000ms}

# 本番環境キャッシュ設定
spring.cache.redis.time-to-live=${CACHE_TTL:1800000}

# 本番環境データベース接続プール設定
spring.r2dbc.pool.max-size=${DB_POOL_MAX_SIZE:10}
spring.r2dbc.pool.max-idle-time=${DB_POOL_MAX_IDLE_TIME:PT10M}
spring.r2dbc.pool.max-life-time=${DB_POOL_MAX_LIFE_TIME:PT30M}
spring.r2dbc.pool.initial-size=${DB_POOL_INITIAL_SIZE:2}
```

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
- `transaction.start` - トランザクション開始回数
- `transaction.commit` - トランザクションコミット回数
- `transaction.rollback` - トランザクションロールバック回数
- `transaction.error` - トランザクションエラー回数
- `logs.audit` - 監査ログ数
- `logs.security` - セキュリティログ数
- `logs.performance` - パフォーマンスログ数
- `logs.error` - エラーログ数

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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);
```

#### database_audit_logsテーブル

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
- `V4__Add_soft_delete_columns.sql` - ソフト削除サポート追加
- `V5__Create_database_audit_logs_table.sql` - 監査ログテーブル作成

## 🚀 デプロイ

### Dockerデプロイ

1. **ビルドと実行**

   ```bash
   # 開発環境（ポート9001）
   ./gradlew build
   docker build -t revenue-calculator-employee .
   docker run -p 9001:8080 -e SPRING_PROFILES_ACTIVE=default revenue-calculator-employee
   
   # 本番環境（ポート8080）
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod revenue-calculator-employee
   ```

### 本番環境デプロイ

#### Google Cloud Run デプロイ

このセクションでは、Google Cloud Runの包括的な本番環境デプロイ手順を提供します。

##### 前提条件

- 課金が有効なGoogle Cloudプロジェクト
- Cloud SQL PostgreSQLインスタンス
- Redisインスタンス（Cloud Memorystoreまたは外部）
- 適切な権限を持つサービスアカウント

##### 方法1：Cloud Runコンソールデプロイ

1. **Cloud Runコンソールを開く**
   - [Google Cloud Console](https://console.cloud.google.com/)にアクセス
   - プロジェクトを選択
   - **Cloud Run**にナビゲート

2. **新しいサービスを作成**
   - **「サービスを作成」**をクリック
   - **「ソースからコンテナを1つデプロイ」**を選択

3. **ソースコードを設定**
   ```
   ソース: ソースリポジトリからデプロイ
   リポジトリタイプ: GitHub
   リポジトリ: GitHubリポジトリを選択
   ブランチ: main
   ビルドタイプ: Dockerfile
   Dockerfileパス: /Dockerfile
   ```

4. **サービス設定を設定**
   ```
   サービス名: revenue-calculator-employee
   リージョン: your-region
   CPU割り当て: リクエスト処理中のみCPUを割り当て
   最小インスタンス数: 1
   最大インスタンス数: 10
   ```

5. **コンテナ設定を設定**
   ```
   ポート: 9001
   メモリ: 1 GiB（推奨）または2 GiB（メモリ問題が続く場合）
   CPU: 2
   リクエストタイムアウト: 300秒
   起動タイムアウト: 300秒
   ```

6. **環境変数を設定**
   ```
   SPRING_PROFILES_ACTIVE: prod
   DB_URL: r2dbc:postgresql://your-db-host:5432/asatex-revenue
   DB_USER: your-db-username
   DB_PASSWORD: your-db-password
   FLYWAY_URL: jdbc:postgresql://your-db-host:5432/asatex-revenue
   REDIS_HOST: your-redis-host
   REDIS_PORT: 6379
   REDIS_DATABASE: 0
   CACHE_TTL: 1800000
   DB_POOL_MAX_SIZE: 5
   DB_POOL_MAX_IDLE_TIME: PT5M
   DB_POOL_MAX_LIFE_TIME: PT15M
   ```

7. **VPC接続を設定**
   - **「接続」**セクションで
   - **「VPCコネクタを追加」**をクリック
   - Cloud SQLアクセス用のVPCコネクタを選択

8. **認証を設定**
   - **「セキュリティ」**セクションで
   - サービスアカウント：`your-service-account@your-project.iam.gserviceaccount.com`
   - 未認証の呼び出しを許可：**はい**

9. **サービスをデプロイ**
   - **「作成」**をクリック
   - ビルドとデプロイの完了を待つ（通常10-15分）

##### 方法2：コマンドラインデプロイ

1. **Dockerイメージをビルド**
   ```bash
   # イメージをビルド
   docker build -t gcr.io/your-project-id/revenue-calculator-backend-employee .
   
   # イメージをGoogle Container Registryにプッシュ
   docker push gcr.io/your-project-id/revenue-calculator-backend-employee
   ```

2. **Cloud Runにデプロイ**
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
     --set-env-vars REDIS_HOST="your-redis-host" \
     --set-env-vars REDIS_PORT="6379" \
     --set-env-vars REDIS_DATABASE="0" \
     --set-env-vars CACHE_TTL="1800000" \
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

##### 環境変数設定

**必要な環境変数：**
```bash
# アプリケーション設定
SPRING_PROFILES_ACTIVE=prod

# データベース設定（Cloud SQL + VPC接続）
DB_URL=r2dbc:postgresql://your-db-host:5432/asatex-revenue
DB_USER=your-db-username
DB_PASSWORD=your-db-password
FLYWAY_URL=jdbc:postgresql://your-db-host:5432/asatex-revenue

# Redis設定
REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_DATABASE=0
CACHE_TTL=1800000
```

**オプションの環境変数：**
```bash
# データベース接続プール設定
DB_POOL_MAX_SIZE=5
DB_POOL_MAX_IDLE_TIME=PT5M
DB_POOL_MAX_LIFE_TIME=PT15M
```

##### データベース設定

**VPC接続設定：**

1. **VPCコネクタの作成：**
   ```bash
   # Cloud RunがCloud SQLにアクセスするためのVPCコネクタを作成
   gcloud compute networks vpc-access connectors create your-vpc-connector \
     --region=your-region \
     --subnet=your-subnet \
     --subnet-project=your-project-id \
     --min-instances=2 \
     --max-instances=3
   ```

2. **データベースユーザー設定：**
   ```sql
   -- パスワード認証でデータベースユーザーを作成
   CREATE USER your-db-username WITH PASSWORD 'your-db-password';
   GRANT ALL PRIVILEGES ON DATABASE asatex_revenue TO your-db-username;
   GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your-db-username;
   GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your-db-username;
   ```

##### デプロイ検証

1. **サービス状態を確認**
   - Cloud Runコンソールで、サービス状態が**「実行中」**であることを確認

2. **ヘルスチェックをテスト**
   ```bash
   # サービスURLを取得
   SERVICE_URL=$(gcloud run services describe revenue-calculator-employee \
       --region=your-region \
       --format="value(status.url)")
   
   # ヘルスチェックをテスト
   curl $SERVICE_URL/actuator/health
   
   # データベース接続をテスト
   curl $SERVICE_URL/actuator/health/db
   ```

3. **API文書にアクセス**
   - ブラウザを開く：`$SERVICE_URL/swagger-ui.html`

##### デプロイ更新

1. Cloud Runコンソールで、サービス名をクリック
2. **「編集して新しいリビジョンをデプロイ」**をクリック
3. **「ソース」**セクションで、**「再ビルド」**をクリック
4. **「デプロイ」**をクリック

##### トラブルシューティング

**一般的な問題：**

1. **ビルド失敗**
   - Dockerfileの構文を確認
   - すべての依存関係がインストールされていることを確認
   - ビルドログを確認

2. **デプロイ失敗**
   - Cloud SQL接続設定を確認
   - サービスアカウント権限を確認
   - 環境変数設定を確認

3. **アプリケーションが起動しない**
   - Cloud Runログを確認
   - データベース接続を確認
   - Redis接続を確認

**便利なコマンド：**
```bash
# サービスログを表示
gcloud run services logs read revenue-calculator-employee --region=your-region

# サービス詳細を表示
gcloud run services describe revenue-calculator-employee --region=your-region

# ビルドログを表示
gcloud builds list --limit=5
```

##### セキュリティ考慮事項

1. **VPCセキュリティ**：VPCコネクタが適切に設定され、セキュアであることを確認
2. **環境変数**：Cloud Runで環境変数を設定する際、機密情報がログに露出しないことを確認
3. **ネットワークアクセス**：Cloud RunサービスがVPCを通じてCloud SQLインスタンスにアクセスできることを確認
4. **ファイアウォールルール**：Cloud SQLのファイアウォールルールを確認し、VPCからの接続を許可
5. **データベースセキュリティ**：強力なパスワードを使用し、データベースユーザー権限を制限
6. **VPCコネクタ**：VPCコネクタが適切なネットワークアクセス制御を持っていることを確認

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

### セキュリティ検証

- 入力パラメータ検証
- SQLインジェクション防止
- XSS防止

### ログセキュリティ

- 機密情報のログ記録なし
- 分析のための構造化ログ
- セキュリティイベントログ

## 📈 パフォーマンス最適化

### キャッシュ戦略

- **従業員情報キャッシュ**: 30分TTL
- **従業員検索キャッシュ**: 15分TTL
- **ページネーションキャッシュ**: 10分TTL
- **自動キャッシュ無効化**: 書き込み操作時の関連キャッシュクリア

### リアクティブプログラミング

- 完全非ブロッキングI/O
- バックプレッシャー処理
- 効率的なリソース利用

### データベース最適化

- 接続プール設定
- クエリ最適化
- インデックス最適化
- 包括的監査ログ、最小限のパフォーマンス影響

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
- **メール**: <niuyuping@asatex.jp>
- **LINE ID**: niuyuping
- **デモサイト**: [revenue.asatex.jp](https://revenue.asatex.jp)

## 📞 連絡先

- プロジェクトリンク: [https://github.com/niuyuping/revenue-calculator-backend-employee](https://github.com/niuyuping/revenue-calculator-backend-employee)
- 問題報告: [https://github.com/niuyuping/revenue-calculator-backend-employee/issues](https://github.com/niuyuping/revenue-calculator-backend-employee/issues)
- 会社メール: <niuyuping@asatex.jp>
- LINE連絡: niuyuping

## 🙏 謝辞

- 優れたフレームワークを提供してくれたSpring Bootチーム
- 信頼性の高いデータベースを提供してくれたPostgreSQLコミュニティ
- サポートと提案を提供してくれたすべての貢献者

---

**バージョン**: v1.0.0  
**最終更新**: 2024年12月  
**互換性**: Java 21+, Spring Boot 3.5.6+

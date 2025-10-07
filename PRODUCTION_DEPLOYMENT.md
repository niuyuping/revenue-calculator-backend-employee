# Revenue Calculator Backend Employee - 生产环境部署指南

本文档提供完整的生产环境部署指南，包括Cloud Run控制台部署和命令行部署两种方式。

## 📋 **项目信息**

- **项目ID**: `gen-lang-client-0889947961`
- **区域**: `asia-northeast1`
- **服务名称**: `revenue-calculator-employee`
- **端口**: `8080`
- **认证方式**: 传统用户名密码认证

## 🚀 **部署方式一：Cloud Run控制台部署**

### 1. **准备代码**

确保您的代码已上传到GitHub仓库。

### 2. **在Cloud Run控制台部署**

#### 2.1 打开Cloud Run控制台

1. 访问 [Google Cloud Console](https://console.cloud.google.com/)
2. 选择项目：`gen-lang-client-0889947961`
3. 导航到 **Cloud Run**

#### 2.2 创建新服务

1. 点击 **"创建服务"**
2. 选择 **"从头开始部署一个容器"**

#### 2.3 配置源代码

```
源代码: 从源代码仓库部署
仓库类型: GitHub
仓库: 选择您的GitHub仓库
分支: main
构建类型: Dockerfile
Dockerfile路径: /Dockerfile
```

#### 2.4 配置服务设置

```
服务名称: revenue-calculator-employee
区域: asia-northeast1
CPU分配: CPU仅在有请求时分配
最小实例数: 1
最大实例数: 10
```

#### 2.5 配置容器设置

```
端口: 8080
内存: 1 GiB (推荐) 或 2 GiB (如果仍有内存问题)
CPU: 2
请求超时: 300秒
启动超时: 300秒
```

#### 2.6 配置环境变量

```
SPRING_PROFILES_ACTIVE: prod
DB_URL: r2dbc:postgresql:///asatex-revenue?unixSocketPath=/cloudsql/gen-lang-client-0889947961:asia-northeast1:asatex-revenue-calculator-database
DB_USER: your_database_username
DB_PASSWORD: your_database_password
REDIS_HOST: 10.13.121.67
REDIS_PORT: 6379
REDIS_DATABASE: 0
CACHE_TTL: 1800000
DB_POOL_MAX_SIZE: 5
DB_POOL_MAX_IDLE_TIME: PT5M
DB_POOL_MAX_LIFE_TIME: PT15M
```

#### 2.7 配置连接

1. 在 **"连接"** 部分
2. 点击 **"添加Cloud SQL连接"**
3. 选择：`gen-lang-client-0889947961:asia-northeast1:asatex-revenue-calculator-database`

#### 2.8 配置身份验证

1. 在 **"安全"** 部分
2. 服务账户：`revenue-calculator-sa@gen-lang-client-0889947961.iam.gserviceaccount.com`
3. 允许未通过身份验证的调用：**是**

#### 2.9 部署服务

1. 点击 **"创建"**
2. 等待构建和部署完成（通常需要10-15分钟）

## 🚀 **部署方式二：命令行部署**

### 1. **构建Docker镜像**

```bash
# 构建镜像
docker build -t gcr.io/gen-lang-client-0889947961/revenue-calculator-backend-employee .

# 推送镜像到Google Container Registry
docker push gcr.io/gen-lang-client-0889947961/revenue-calculator-backend-employee
```

### 2. **部署到Cloud Run**

```bash
gcloud run deploy revenue-calculator-employee \
  --image gcr.io/gen-lang-client-0889947961/revenue-calculator-backend-employee \
  --platform managed \
  --region asia-northeast1 \
  --set-env-vars SPRING_PROFILES_ACTIVE="prod" \
  --set-env-vars DB_URL="r2dbc:postgresql:///asatex-revenue?unixSocketPath=/cloudsql/gen-lang-client-0889947961:asia-northeast1:asatex-revenue-calculator-database" \
  --set-env-vars DB_USER="your_database_username" \
  --set-env-vars DB_PASSWORD="your_database_password" \
  --set-env-vars REDIS_HOST="10.13.121.67" \
  --set-env-vars REDIS_PORT="6379" \
  --set-env-vars REDIS_DATABASE="0" \
  --set-env-vars CACHE_TTL="1800000" \
  --set-env-vars DB_POOL_MAX_SIZE="5" \
  --set-env-vars DB_POOL_MAX_IDLE_TIME="PT5M" \
  --set-env-vars DB_POOL_MAX_LIFE_TIME="PT15M" \
  --add-cloudsql-instances gen-lang-client-0889947961:asia-northeast1:asatex-revenue-calculator-database \
  --service-account revenue-calculator-sa@gen-lang-client-0889947961.iam.gserviceaccount.com \
  --allow-unauthenticated \
  --memory 1Gi \
  --cpu 2 \
  --timeout 300 \
  --port 8080
```

## 🔧 **环境变量配置详解**

### 必需的环境变量

```bash
# 应用配置
SPRING_PROFILES_ACTIVE=prod

# 数据库配置（传统用户名密码认证）
DB_URL=r2dbc:postgresql:///asatex-revenue?unixSocketPath=/cloudsql/gen-lang-client-0889947961:asia-northeast1:asatex-revenue-calculator-database
DB_USER=your_database_username
DB_PASSWORD=your_database_password

# Redis配置
REDIS_HOST=10.13.121.67
REDIS_PORT=6379
REDIS_DATABASE=0
CACHE_TTL=1800000
```

### 可选的环境变量

```bash
# 数据库连接池配置
DB_POOL_MAX_SIZE=5
DB_POOL_MAX_IDLE_TIME=PT5M
DB_POOL_MAX_LIFE_TIME=PT15M
```

## 🗄️ **数据库配置**

### 数据库用户权限设置

确保数据库用户具有以下权限：

```sql
-- 创建用户（如果不存在）
CREATE USER your_database_username WITH PASSWORD 'your_database_password';

-- 授予必要权限
GRANT CONNECT ON DATABASE asatex_revenue TO your_database_username;
GRANT USAGE ON SCHEMA public TO your_database_username;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO your_database_username;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO your_database_username;

-- 对于新创建的表，自动授予权限
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO your_database_username;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO your_database_username;
```

## 🔍 **验证部署**

### 1. 检查服务状态

在Cloud Run控制台中，确认服务状态为 **"正在运行"**

### 2. 测试健康检查

```bash
# 获取服务URL
SERVICE_URL=$(gcloud run services describe revenue-calculator-employee \
    --region=asia-northeast1 \
    --format="value(status.url)")

# 测试健康检查
curl $SERVICE_URL/actuator/health

# 测试数据库连接
curl $SERVICE_URL/actuator/health/db
```

### 3. 访问API文档

打开浏览器访问：`$SERVICE_URL/swagger-ui.html`

## 🔄 **更新部署**

### 重新部署

1. 在Cloud Run控制台中，点击服务名称
2. 点击 **"编辑和部署新版本"**
3. 在 **"源代码"** 部分，点击 **"重新构建"**
4. 点击 **"部署"**

## 🛠️ **故障排除**

### 常见问题

1. **构建失败**
   - 检查Dockerfile语法
   - 确认所有依赖都已安装
   - 查看构建日志

2. **部署失败**
   - 检查Cloud SQL连接配置
   - 确认服务账户权限
   - 检查环境变量配置

3. **应用无法启动**
   - 查看Cloud Run日志
   - 检查数据库连接
   - 验证Redis连接

### 有用的命令

```bash
# 查看服务日志
gcloud run services logs read revenue-calculator-employee --region=asia-northeast1

# 查看服务详情
gcloud run services describe revenue-calculator-employee --region=asia-northeast1

# 查看构建日志
gcloud builds list --limit=5
```

## 🔒 **安全注意事项**

1. **密码安全**：使用强密码，建议包含大小写字母、数字和特殊字符
2. **环境变量**：在Cloud Run中设置环境变量时，确保密码不会在日志中暴露
3. **网络访问**：确保Cloud Run服务能够访问Cloud SQL实例
4. **防火墙规则**：检查Cloud SQL的防火墙规则，确保允许来自Cloud Run的连接
5. **服务账户权限**：确保服务账户具有必要的Cloud SQL和Redis访问权限

## 📚 **API文档访问**

### **Swagger UI**
```bash
# 生产环境 (只显示员工管理API)
https://your-service-url/swagger-ui.html

# 开发环境 (显示所有API)
http://localhost:9001/swagger-ui.html
```

### **OpenAPI JSON**
```bash
# 生产环境
https://your-service-url/v3/api-docs

# 开发环境
http://localhost:9001/v3/api-docs
```

**注意**: 生产环境中，监控和审计端点已从Swagger文档中隐藏，只显示核心的员工管理API，提高安全性和用户体验。

# Cloud Run控制台部署指南

本文档说明如何在Google Cloud Run控制台中直接构建和部署应用。

## 🚀 **部署步骤**

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
DB_URL: r2dbc:postgresql://localhost/asatex-revenue?unixSocketPath=/cloudsql/gen-lang-client-0889947961:asia-northeast1:asatex-revenue-calculator-database
DB_USERNAME: db_user
REDIS_HOST: 10.13.121.67
REDIS_PORT: 6379
REDIS_DATABASE: 0
CACHE_TTL: 1800000
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
```

## 📋 **配置总结**

- **项目ID**: `gen-lang-client-0889947961`
- **区域**: `asia-northeast1`
- **服务名称**: `revenue-calculator-employee`
- **端口**: `8080`
- **内存**: `1 GiB (推荐) 或 2 GiB (如果仍有内存问题)`
- **CPU**: `2`
- **Redis**: `10.13.121.67:6379`
- **数据库**: Cloud SQL (IAM身份验证)

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

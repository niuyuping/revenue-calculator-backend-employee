# 使用多阶段构建优化镜像大小
FROM gradle:8.5-jdk21-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制构建文件
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/
COPY gradlew ./

# 下载依赖（利用Docker缓存层）
RUN ./gradlew dependencies --no-daemon

# 复制源代码
COPY src/ src/

# 构建应用
RUN ./gradlew build -x test --no-daemon

# 生产阶段
FROM openjdk:21-jre-slim

# 安装必要的工具
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 创建应用用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /app/build/libs/*.jar app.jar

# 创建日志目录
RUN mkdir -p /var/log/revenue-calculator && \
    chown -R appuser:appuser /var/log/revenue-calculator

# 切换到应用用户
USER appuser

# 暴露端口
EXPOSE 9001

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:9001/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=prod", \
    "-Xmx512m", \
    "-Xms256m", \
    "-XX:+UseG1GC", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", \
    "app.jar"]

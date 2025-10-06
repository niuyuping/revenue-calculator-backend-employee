# 使用Spring Boot官方镜像
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

# 生产阶段 - 使用Spring Boot官方镜像
FROM openjdk:21-jdk-slim

# 安装必要的工具
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /app/build/libs/*.jar app.jar

# 暴露端口（Cloud Run会动态设置PORT环境变量）
EXPOSE $PORT

# 健康检查（使用动态端口）
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT:-9001}/actuator/health || exit 1

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

package jp.asatex.revenue_calculator_backend_employee.config;

import jp.asatex.revenue_calculator_backend_employee.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 请求日志过滤器
 * 记录所有HTTP请求的详细信息
 */
@Component
@Order(1)
public class RequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final Logger requestLogger = LoggerFactory.getLogger("REQUEST");

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 生成请求ID
        String requestId = UUID.randomUUID().toString();
        final String sessionId = request.getHeaders().getFirst("X-Session-ID") != null ? 
                request.getHeaders().getFirst("X-Session-ID") : UUID.randomUUID().toString();

        // 设置MDC
        MDC.put("requestId", requestId);
        MDC.put("sessionId", sessionId);
        MDC.put("ipAddress", getClientIpAddress(request));
        MDC.put("userAgent", request.getHeaders().getFirst("User-Agent"));

        long startTime = System.currentTimeMillis();

        // 记录请求开始
        logRequestStart(request, requestId, sessionId);

        // 添加请求ID到响应头
        response.getHeaders().add("X-Request-ID", requestId);
        response.getHeaders().add("X-Session-ID", sessionId);

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logRequestEnd(request, response, requestId, sessionId, duration, true, null);
                })
                .doOnError(throwable -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logRequestEnd(request, response, requestId, sessionId, duration, false, throwable);
                })
                .doFinally(signalType -> {
                    // 清理MDC
                    MDC.clear();
                });
    }

    private void logRequestStart(ServerHttpRequest request, String requestId, String sessionId) {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("timestamp", Instant.now().toString());
            requestData.put("requestId", requestId);
            requestData.put("sessionId", sessionId);
            requestData.put("method", request.getMethod().name());
            requestData.put("uri", request.getURI().toString());
            requestData.put("path", request.getPath().value());
            requestData.put("queryParams", request.getQueryParams().toSingleValueMap());
            requestData.put("headers", getFilteredHeaders(request));
            requestData.put("ipAddress", getClientIpAddress(request));
            requestData.put("userAgent", request.getHeaders().getFirst("User-Agent"));

            requestLogger.info("请求开始: {}", requestData);
        } catch (Exception e) {
            logger.error("记录请求开始日志失败", e);
        }
    }

    private void logRequestEnd(ServerHttpRequest request, ServerHttpResponse response, 
                              String requestId, String sessionId, long duration, 
                              boolean success, Throwable error) {
        try {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("timestamp", Instant.now().toString());
            responseData.put("requestId", requestId);
            responseData.put("sessionId", sessionId);
            responseData.put("method", request.getMethod().name());
            responseData.put("uri", request.getURI().toString());
            responseData.put("path", request.getPath().value());
            responseData.put("statusCode", response.getStatusCode() != null ? response.getStatusCode().value() : 0);
            responseData.put("duration", duration);
            responseData.put("success", success);
            responseData.put("ipAddress", getClientIpAddress(request));
            responseData.put("userAgent", request.getHeaders().getFirst("User-Agent"));

            if (error != null) {
                responseData.put("error", error.getMessage());
                responseData.put("errorType", error.getClass().getSimpleName());
            }

            requestLogger.info("请求结束: {}", responseData);

            // 记录API调用审计日志
            auditLogService.logApiCall(
                    request.getMethod().name(),
                    request.getURI().toString(),
                    response.getStatusCode() != null ? response.getStatusCode().value() : 0,
                    duration,
                    getCurrentUserId(request),
                    0, // 请求大小（需要额外实现）
                    0  // 响应大小（需要额外实现）
            );
        } catch (Exception e) {
            logger.error("记录请求结束日志失败", e);
        }
    }

    private String getClientIpAddress(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddress() != null && request.getRemoteAddress().getAddress() != null ? 
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    private String getCurrentUserId(ServerHttpRequest request) {
        // 从请求头或JWT token中获取用户ID
        String userId = request.getHeaders().getFirst("X-User-ID");
        if (userId == null) {
            userId = request.getHeaders().getFirst("Authorization");
            // 这里可以解析JWT token获取用户ID
        }
        return userId != null ? userId : "anonymous";
    }

    private Map<String, String> getFilteredHeaders(ServerHttpRequest request) {
        Map<String, String> filteredHeaders = new HashMap<>();
        request.getHeaders().forEach((name, values) -> {
            // 过滤敏感信息
            if (!isSensitiveHeader(name)) {
                filteredHeaders.put(name, String.join(", ", values));
            }
        });
        return filteredHeaders;
    }

    private boolean isSensitiveHeader(String headerName) {
        String lowerCase = headerName.toLowerCase();
        return lowerCase.contains("authorization") ||
               lowerCase.contains("cookie") ||
               lowerCase.contains("x-api-key") ||
               lowerCase.contains("x-auth-token");
    }
}

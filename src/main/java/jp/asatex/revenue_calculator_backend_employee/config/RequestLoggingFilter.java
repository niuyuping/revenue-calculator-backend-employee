package jp.asatex.revenue_calculator_backend_employee.config;

import jp.asatex.revenue_calculator_backend_employee.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
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
 * Request logging filter
 * Records detailed information for all HTTP requests
 */
@Component
@Order(1)
public class RequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final Logger requestLogger = LoggerFactory.getLogger("REQUEST");

    @Autowired
    private AuditLogService auditLogService;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Generate request ID
        String requestId = UUID.randomUUID().toString();
        final String sessionId = request.getHeaders().getFirst("X-Session-ID") != null ? 
                request.getHeaders().getFirst("X-Session-ID") : UUID.randomUUID().toString();

        // Set MDC
        MDC.put("requestId", requestId);
        MDC.put("sessionId", sessionId);
        MDC.put("ipAddress", getClientIpAddress(request));
        MDC.put("userAgent", request.getHeaders().getFirst("User-Agent"));

        long startTime = System.currentTimeMillis();

        // Log request start
        logRequestStart(request, requestId, sessionId);

        // Add request ID to response headers
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
                    // Clear MDC
                    MDC.clear();
                })
                .onErrorResume(throwable -> {
                    // Ensure we always return a non-null Mono<Void>
                    logger.error("Error in request filter", throwable);
                    return Mono.empty();
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

            requestLogger.info("Request started: {}", requestData);
        } catch (Exception e) {
            logger.error("Failed to log request start", e);
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
            var statusCode = response.getStatusCode();
            responseData.put("statusCode", statusCode != null ? statusCode.value() : 0);
            responseData.put("duration", duration);
            responseData.put("success", success);
            responseData.put("ipAddress", getClientIpAddress(request));
            responseData.put("userAgent", request.getHeaders().getFirst("User-Agent"));

            if (error != null) {
                responseData.put("error", error.getMessage());
                responseData.put("errorType", error.getClass().getSimpleName());
            }

            requestLogger.info("Request completed: {}", responseData);

            // Log API call audit
            auditLogService.logApiCall(
                    request.getMethod().name(),
                    request.getURI().toString(),
                    statusCode != null ? statusCode.value() : 0,
                    duration,
                    getCurrentUserId(request),
                    0, // Request size (needs additional implementation)
                    0  // Response size (needs additional implementation)
            );
        } catch (Exception e) {
            logger.error("Failed to log request completion", e);
        }
    }

    private String getClientIpAddress(ServerHttpRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        var remoteAddress = request.getRemoteAddress();
        return remoteAddress != null && remoteAddress.getAddress() != null ? 
                remoteAddress.getAddress().getHostAddress() : "unknown";
    }

    private String getCurrentUserId(ServerHttpRequest request) {
        if (request == null) {
            return "anonymous";
        }
        
        // Get user ID from request headers or JWT token
        String userId = request.getHeaders().getFirst("X-User-ID");
        if (userId == null) {
            userId = request.getHeaders().getFirst("Authorization");
            // Here you can parse JWT token to get user ID
        }
        return userId != null ? userId : "anonymous";
    }

    private Map<String, String> getFilteredHeaders(ServerHttpRequest request) {
        Map<String, String> filteredHeaders = new HashMap<>();
        if (request != null && request.getHeaders() != null) {
            request.getHeaders().forEach((name, values) -> {
                // Filter sensitive information
                if (name != null && values != null && !isSensitiveHeader(name)) {
                    filteredHeaders.put(name, String.join(", ", values));
                }
            });
        }
        return filteredHeaders;
    }

    private boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        String lowerCase = headerName.toLowerCase();
        return lowerCase.contains("authorization") ||
               lowerCase.contains("cookie") ||
               lowerCase.contains("x-api-key") ||
               lowerCase.contains("x-auth-token");
    }
}

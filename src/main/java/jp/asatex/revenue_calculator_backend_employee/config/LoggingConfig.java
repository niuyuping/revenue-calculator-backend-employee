package jp.asatex.revenue_calculator_backend_employee.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * 日志配置类
 * 提供请求日志、响应日志和分布式追踪支持
 */
@Configuration
public class LoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);


    /**
     * HTTP客户端请求日志过滤器
     * 用于记录对外部服务的HTTP请求
     */
    @Bean
    public ExchangeFilterFunction clientRequestLoggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            String requestId = UUID.randomUUID().toString().substring(0, 8);
            String method = clientRequest.method().name();
            String url = clientRequest.url().toString();
            
            logger.debug("Outgoing HTTP request: {} {} [Request-ID: {}]", method, url, requestId);
            
            return Mono.just(clientRequest)
                    .contextWrite(Context.of("clientRequestId", requestId));
        });
    }

    /**
     * HTTP客户端响应日志过滤器
     */
    @Bean
    public ExchangeFilterFunction clientResponseLoggingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.debug("HTTP response received - Status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}

package jp.asatex.revenue_calculator_backend_employee.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

/**
 * Logging configuration class
 * Provides request logging, response logging and distributed tracing support
 */
@Configuration
public class LoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);


    /**
     * HTTP client request logging filter
     * Used to log HTTP requests to external services
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
     * HTTP client response logging filter
     */
    @Bean
    public ExchangeFilterFunction clientResponseLoggingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.debug("HTTP response received - Status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}

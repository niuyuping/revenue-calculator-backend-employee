package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * System exception handler for handling common errors like favicon.ico requests
 */
@Component("systemExceptionHandler")
@Order(-2)
public class SystemExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Handle favicon.ico requests gracefully - just return empty response to prevent 500 errors
        if (exchange.getRequest().getURI().getPath().equals("/favicon.ico")) {
            return Mono.empty();
        }

        // For other errors, just return empty to prevent 500 errors
        return Mono.empty();
    }
}
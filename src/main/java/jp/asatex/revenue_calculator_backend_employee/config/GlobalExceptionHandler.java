package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Global exception handler for handling common errors like favicon.ico requests
 */
@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ServerCodecConfigurer serverCodecConfigurer;

    public GlobalExceptionHandler(ServerCodecConfigurer serverCodecConfigurer) {
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Handle favicon.ico requests gracefully
        if (exchange.getRequest().getURI().getPath().equals("/favicon.ico")) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Map.of(
                            "error", "Not Found",
                            "message", "Favicon not found",
                            "path", "/favicon.ico"
                    )))
                    .flatMap(response -> response.writeTo(exchange, serverCodecConfigurer));
        }

        // For other errors, return a generic error response
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of(
                        "error", "Internal Server Error",
                        "message", "An unexpected error occurred",
                        "timestamp", System.currentTimeMillis()
                )))
                .flatMap(response -> response.writeTo(exchange, serverCodecConfigurer));
    }
}

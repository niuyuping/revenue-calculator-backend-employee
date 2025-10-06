package jp.asatex.revenue_calculator_backend_employee.controller;

import jp.asatex.revenue_calculator_backend_employee.service.TransactionMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Transaction monitoring controller
 * Provides API endpoints for transaction statistics
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "Transaction Monitoring", description = "Transaction monitoring and statistics API")
public class TransactionMonitoringController {

    @Autowired
    private TransactionMonitoringService transactionMonitoringService;

    /**
     * Get transaction statistics
     * @return Transaction statistics information
     */
    @GetMapping(value = "/transaction/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "Get transaction statistics", description = "Returns current system transaction statistics including start, commit, rollback and error counts")
    public Mono<TransactionMonitoringService.TransactionStats> getTransactionStats() {
        return Mono.just(transactionMonitoringService.getTransactionStats());
    }
}

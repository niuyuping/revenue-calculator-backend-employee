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
 * 事务监控控制器
 * 提供事务统计信息的API端点
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "事务监控", description = "事务监控和统计信息API")
public class TransactionMonitoringController {

    @Autowired
    private TransactionMonitoringService transactionMonitoringService;

    /**
     * 获取事务统计信息
     * @return 事务统计信息
     */
    @GetMapping(value = "/transaction/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "monitoring-api")
    @Operation(summary = "获取事务统计信息", description = "返回当前系统的事务统计信息，包括开始、提交、回滚和错误次数")
    public Mono<TransactionMonitoringService.TransactionStats> getTransactionStats() {
        return Mono.just(transactionMonitoringService.getTransactionStats());
    }
}

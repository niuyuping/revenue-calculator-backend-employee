package jp.asatex.revenue_calculator_backend_employee.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 日志配置测试
 * 验证日志配置和请求日志功能
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
class LoggingConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testRequestLoggingFilter() {
        // 测试请求日志过滤器是否正常工作
        webTestClient.get()
                .uri("/api/v1/employee/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Request-ID");
    }

    @Test
    void testEmployeeApiLogging() {
        // 测试员工API的日志记录
        webTestClient.get()
                .uri("/api/v1/employee")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testErrorLogging() {
        // 测试错误日志记录
        webTestClient.get()
                .uri("/api/v1/employee/999")
                .exchange()
                .expectStatus().isNotFound();
    }
}


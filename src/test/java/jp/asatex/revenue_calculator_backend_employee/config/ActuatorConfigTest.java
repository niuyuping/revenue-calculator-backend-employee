package jp.asatex.revenue_calculator_backend_employee.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Actuator配置测试
 * 验证监控端点是否正常工作
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import({jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class, 
         jp.asatex.revenue_calculator_backend_employee.config.TestContainersConfig.class})
class ActuatorConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testHealthEndpoint() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").exists()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void testInfoEndpoint() {
        webTestClient.get()
                .uri("/actuator/info")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testMetricsEndpoint() {
        webTestClient.get()
                .uri("/actuator/metrics")
                .exchange()
                .expectStatus().isOk();
    }

}
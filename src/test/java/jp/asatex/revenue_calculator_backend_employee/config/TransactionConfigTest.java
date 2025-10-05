package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TransactionConfig 测试类
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {"spring.flyway.enabled=false"})
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("TransactionConfig テスト")
class TransactionConfigTest {

    @Autowired
    private ReactiveTransactionManager transactionManager;

    @Autowired
    private TransactionalOperator transactionalOperator;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    @DisplayName("事务管理器应该正确配置")
    void testTransactionManagerConfiguration() {
        assertThat(transactionManager).isNotNull();
        assertThat(transactionManager).isInstanceOf(R2dbcTransactionManager.class);
    }

    @Test
    @DisplayName("事务操作器应该正确配置")
    void testTransactionalOperatorConfiguration() {
        assertThat(transactionalOperator).isNotNull();
    }

    @Test
    @DisplayName("连接工厂应该正确注入")
    void testConnectionFactoryInjection() {
        assertThat(connectionFactory).isNotNull();
    }

    @Test
    @DisplayName("事务管理器应该使用正确的连接工厂")
    void testTransactionManagerUsesCorrectConnectionFactory() {
        R2dbcTransactionManager r2dbcTransactionManager = (R2dbcTransactionManager) transactionManager;
        assertThat(r2dbcTransactionManager.getConnectionFactory()).isSameAs(connectionFactory);
    }
}

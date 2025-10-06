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
 * TransactionConfig test class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {"spring.flyway.enabled=false"})
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("TransactionConfig Test")
class TransactionConfigTest {

    @Autowired
    private ReactiveTransactionManager transactionManager;

    @Autowired
    private TransactionalOperator transactionalOperator;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    @DisplayName("Transaction manager should be configured correctly")
    void testTransactionManagerConfiguration() {
        assertThat(transactionManager).isNotNull();
        assertThat(transactionManager).isInstanceOf(R2dbcTransactionManager.class);
    }

    @Test
    @DisplayName("Transaction operator should be configured correctly")
    void testTransactionalOperatorConfiguration() {
        assertThat(transactionalOperator).isNotNull();
    }

    @Test
    @DisplayName("Connection factory should be injected correctly")
    void testConnectionFactoryInjection() {
        assertThat(connectionFactory).isNotNull();
    }

    @Test
    @DisplayName("Transaction manager should use correct connection factory")
    void testTransactionManagerUsesCorrectConnectionFactory() {
        R2dbcTransactionManager r2dbcTransactionManager = (R2dbcTransactionManager) transactionManager;
        assertThat(r2dbcTransactionManager.getConnectionFactory()).isSameAs(connectionFactory);
    }
}

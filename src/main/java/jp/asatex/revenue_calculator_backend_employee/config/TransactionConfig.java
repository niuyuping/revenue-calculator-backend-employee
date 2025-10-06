package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

/**
 * Transaction configuration class
 * Configures R2DBC reactive transaction manager
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    /**
     * Configure R2DBC reactive transaction manager
     * @param connectionFactory R2DBC connection factory
     * @return ReactiveTransactionManager
     */
    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    /**
     * Configure transaction operator
     * @param transactionManager Transaction manager
     * @return TransactionalOperator
     */
    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}

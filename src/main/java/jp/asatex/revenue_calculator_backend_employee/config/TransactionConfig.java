package jp.asatex.revenue_calculator_backend_employee.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

/**
 * 事务配置类
 * 配置 R2DBC 响应式事务管理器
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    /**
     * 配置 R2DBC 响应式事务管理器
     * @param connectionFactory R2DBC 连接工厂
     * @return ReactiveTransactionManager
     */
    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    /**
     * 配置事务操作器
     * @param transactionManager 事务管理器
     * @return TransactionalOperator
     */
    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}

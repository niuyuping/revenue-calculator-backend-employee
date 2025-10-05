package jp.asatex.revenue_calculator_backend_employee.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TransactionException 测试类
 */
@DisplayName("TransactionException テスト")
class TransactionExceptionTest {

    @Test
    @DisplayName("使用消息创建事务异常应该正确")
    void testCreateTransactionExceptionWithMessage() {
        String message = "事务处理失败";
        TransactionException exception = new TransactionException(message);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("使用消息和原因创建事务异常应该正确")
    void testCreateTransactionExceptionWithMessageAndCause() {
        String message = "事务处理失败";
        RuntimeException cause = new RuntimeException("数据库连接失败");
        TransactionException exception = new TransactionException(message, cause);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("使用原因创建事务异常应该正确")
    void testCreateTransactionExceptionWithCause() {
        RuntimeException cause = new RuntimeException("数据库连接失败");
        TransactionException exception = new TransactionException(cause);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("事务异常应该继承RuntimeException")
    void testTransactionExceptionInheritsRuntimeException() {
        TransactionException exception = new TransactionException("测试异常");
        
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("事务异常应该有正确的序列化版本ID")
    void testTransactionExceptionSerialVersionUID() {
        // 通过反射检查序列化版本ID
        try {
            java.lang.reflect.Field field = TransactionException.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long serialVersionUID = field.getLong(null);
            assertThat(serialVersionUID).isEqualTo(1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("无法访问序列化版本ID字段", e);
        }
    }
}

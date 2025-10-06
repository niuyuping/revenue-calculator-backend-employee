package jp.asatex.revenue_calculator_backend_employee.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TransactionException test class
 */
@DisplayName("TransactionException Test")
class TransactionExceptionTest {

    @Test
    @DisplayName("Creating transaction exception with message should be correct")
    void testCreateTransactionExceptionWithMessage() {
        String message = "Transaction processing failed";
        TransactionException exception = new TransactionException(message);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Creating transaction exception with message and cause should be correct")
    void testCreateTransactionExceptionWithMessageAndCause() {
        String message = "Transaction processing failed";
        RuntimeException cause = new RuntimeException("Database connection failed");
        TransactionException exception = new TransactionException(message, cause);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("Creating transaction exception with cause should be correct")
    void testCreateTransactionExceptionWithCause() {
        RuntimeException cause = new RuntimeException("Database connection failed");
        TransactionException exception = new TransactionException(cause);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("Transaction exception should inherit RuntimeException")
    void testTransactionExceptionInheritsRuntimeException() {
        TransactionException exception = new TransactionException("Test exception");
        
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Transaction exception should have correct serial version UID")
    void testTransactionExceptionSerialVersionUID() {
        // Check serial version UID through reflection
        try {
            java.lang.reflect.Field field = TransactionException.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long serialVersionUID = field.getLong(null);
            assertThat(serialVersionUID).isEqualTo(1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Cannot access serial version UID field", e);
        }
    }
}

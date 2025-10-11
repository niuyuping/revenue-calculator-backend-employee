package jp.asatex.revenue_calculator_backend_employee.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TransactionHandler test class
 */
@DisplayName("TransactionHandler Test")
class TransactionHandlerTest {

    @Test
    @DisplayName("Creating transaction handler with message should be correct")
    void testCreateTransactionHandlerWithMessage() {
        String message = "Transaction processing failed";
        TransactionHandler handler = new TransactionHandler(message);
        
        assertThat(handler).isNotNull();
        assertThat(handler.getMessage()).isEqualTo(message);
        assertThat(handler.getCause()).isNull();
    }

    @Test
    @DisplayName("Creating transaction handler with message and cause should be correct")
    void testCreateTransactionHandlerWithMessageAndCause() {
        String message = "Transaction processing failed";
        RuntimeException cause = new RuntimeException("Database connection failed");
        TransactionHandler handler = new TransactionHandler(message, cause);
        
        assertThat(handler).isNotNull();
        assertThat(handler.getMessage()).isEqualTo(message);
        assertThat(handler.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("Creating transaction handler with cause should be correct")
    void testCreateTransactionHandlerWithCause() {
        RuntimeException cause = new RuntimeException("Database connection failed");
        TransactionHandler handler = new TransactionHandler(cause);
        
        assertThat(handler).isNotNull();
        assertThat(handler.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("Transaction handler should inherit RuntimeException")
    void testTransactionHandlerInheritsRuntimeException() {
        TransactionHandler handler = new TransactionHandler("Test exception");
        
        assertThat(handler).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Transaction handler should have correct serial version UID")
    void testTransactionHandlerSerialVersionUID() {
        // Check serial version UID through reflection
        try {
            java.lang.reflect.Field field = TransactionHandler.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long serialVersionUID = field.getLong(null);
            assertThat(serialVersionUID).isEqualTo(1L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Cannot access serial version UID field", e);
        }
    }
}

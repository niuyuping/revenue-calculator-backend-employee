package jp.asatex.revenue_calculator_backend_employee.exception;

/**
 * Transaction exception class
 * Used to handle transaction-related exception situations
 */
public class TransactionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param message Exception message
     */
    public TransactionException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param message Exception message
     * @param cause Cause exception
     */
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     * @param cause Cause exception
     */
    public TransactionException(Throwable cause) {
        super(cause);
    }
}

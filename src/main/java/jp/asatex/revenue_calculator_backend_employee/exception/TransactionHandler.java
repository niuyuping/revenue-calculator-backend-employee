package jp.asatex.revenue_calculator_backend_employee.exception;

/**
 * Transaction exception handler class
 * Used to handle transaction-related exception situations
 */
public class TransactionHandler extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param message Exception message
     */
    public TransactionHandler(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param message Exception message
     * @param cause Cause exception
     */
    public TransactionHandler(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     * @param cause Cause exception
     */
    public TransactionHandler(Throwable cause) {
        super(cause);
    }
}

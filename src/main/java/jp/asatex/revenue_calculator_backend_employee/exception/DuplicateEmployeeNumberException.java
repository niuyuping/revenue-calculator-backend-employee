package jp.asatex.revenue_calculator_backend_employee.exception;

/**
 * Duplicate employee number exception
 */
public class DuplicateEmployeeNumberException extends RuntimeException {
    
    public DuplicateEmployeeNumberException(String message) {
        super(message);
    }
    
    public DuplicateEmployeeNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}

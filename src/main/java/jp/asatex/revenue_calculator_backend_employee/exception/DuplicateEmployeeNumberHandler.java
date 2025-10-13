package jp.asatex.revenue_calculator_backend_employee.exception;

/**
 * Duplicate employee number exception handler
 */
public class DuplicateEmployeeNumberHandler extends RuntimeException {
    
    public DuplicateEmployeeNumberHandler(String message) {
        super(message);
    }
    
    public DuplicateEmployeeNumberHandler(String message, Throwable cause) {
        super(message, cause);
    }
}

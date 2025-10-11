package jp.asatex.revenue_calculator_backend_employee.exception;

/**
 * Employee not found exception handler
 */
public class EmployeeNotFoundHandler extends RuntimeException {
    
    public EmployeeNotFoundHandler(String message) {
        super(message);
    }
    
    public EmployeeNotFoundHandler(String message, Throwable cause) {
        super(message, cause);
    }
}

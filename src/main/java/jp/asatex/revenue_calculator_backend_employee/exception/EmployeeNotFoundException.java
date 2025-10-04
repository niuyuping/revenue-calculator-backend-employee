package jp.asatex.revenue_calculator_backend_employee.exception;

/**
 * 従業員未発見例外
 */
public class EmployeeNotFoundException extends RuntimeException {
    
    public EmployeeNotFoundException(String message) {
        super(message);
    }
    
    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

package jp.asatex.revenue_calculator_backend_employee.exception;

/**
 * 従業員番号重複例外
 */
public class DuplicateEmployeeNumberException extends RuntimeException {
    
    public DuplicateEmployeeNumberException(String message) {
        super(message);
    }
    
    public DuplicateEmployeeNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}

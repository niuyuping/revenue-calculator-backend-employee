package jp.asatex.revenue_calculator_backend_employee.exception;

/**
 * 事务异常类
 * 用于处理事务相关的异常情况
 */
public class TransactionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param message 异常消息
     */
    public TransactionException(String message) {
        super(message);
    }

    /**
     * 构造函数
     * @param message 异常消息
     * @param cause 原因异常
     */
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     * @param cause 原因异常
     */
    public TransactionException(Throwable cause) {
        super(cause);
    }
}

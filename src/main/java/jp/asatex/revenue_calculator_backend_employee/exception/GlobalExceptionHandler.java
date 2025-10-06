package jp.asatex.revenue_calculator_backend_employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler
 * Handles validation errors and other exceptions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(WebExchangeBindException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation Failed",
                        (existing, replacement) -> existing
                ));
        
        response.put("error", "Validation Failed");
        response.put("message", "Request parameter validation failed");
        response.put("details", errors);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        return Mono.just(ResponseEntity.badRequest().body(response));
    }


    /**
     * Handle employee not found exception
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Employee not found");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    /**
     * Handle duplicate employee number exception
     */
    @ExceptionHandler(DuplicateEmployeeNumberException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleDuplicateEmployeeNumberException(DuplicateEmployeeNumberException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Duplicate employee number");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.CONFLICT.value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(response));
    }

    /**
     * Handle constraint violation exception (path parameter and query parameter validation)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        
        if (ex.getConstraintViolations() != null && !ex.getConstraintViolations().isEmpty()) {
            Map<String, String> errors = ex.getConstraintViolations()
                    .stream()
                    .collect(Collectors.toMap(
                            violation -> violation.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (existing, replacement) -> existing
                    ));
            response.put("details", errors);
        } else {
            response.put("details", "Parameter validation failed");
        }

        response.put("error", "Parameter validation failed");
        response.put("message", "Request parameters do not meet validation rules");
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    /**
     * Handle server input exceptions (missing required parameters, etc.)
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleServerWebInputException(ServerWebInputException ex) {
        Map<String, Object> response = new HashMap<>();
        
        String message = ex.getMessage();
        if (message != null && message.contains("Required query parameter")) {
            response.put("error", "Parameter validation failed");
            response.put("message", "Required query parameters are missing");
            response.put("details", "Please check required parameters");
        } else if (message != null && message.contains("Required request parameter")) {
            response.put("error", "Parameter validation failed");
            response.put("message", "Required request parameter is missing");
            response.put("details", "Please check required parameters");
        } else {
            response.put("error", "Parameter validation failed");
            response.put("message", "Request parameters do not meet validation rules");
            response.put("details", message != null ? message : "Parameter validation failed");
        }
        
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    /**
     * Handle transaction exceptions
     */
    @ExceptionHandler(TransactionException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleTransactionException(TransactionException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Transaction processing failed");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if it's a validation-related exception
        if (ex.getMessage() != null && ex.getMessage().contains("Validation failure")) {
            response.put("error", "Validation Failed");
            response.put("message", "Request data does not conform to validation rules");
            response.put("details", "Please check input data format and length");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return Mono.just(ResponseEntity.badRequest().body(response));
        }
        
        // Check for other types of validation exceptions
        if (ex.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) ex.getCause();
            
            if (cve.getConstraintViolations() != null && !cve.getConstraintViolations().isEmpty()) {
                Map<String, String> errors = cve.getConstraintViolations()
                        .stream()
                        .collect(Collectors.toMap(
                                violation -> violation.getPropertyPath().toString(),
                                ConstraintViolation::getMessage,
                                (existing, replacement) -> existing
                        ));
                response.put("details", errors);
            } else {
                response.put("details", "Parameter validation failed");
            }
            
            response.put("error", "Validation Failed");
            response.put("message", "Request data does not conform to validation rules");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return Mono.just(ResponseEntity.badRequest().body(response));
        }
        
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "Unknown error");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}

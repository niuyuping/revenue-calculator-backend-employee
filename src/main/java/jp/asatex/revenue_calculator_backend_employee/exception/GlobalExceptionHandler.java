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
 * グローバル例外ハンドラー
 * 検証エラーとその他の例外を処理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 検証エラーの処理
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(WebExchangeBindException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "検証失敗",
                        (existing, replacement) -> existing
                ));
        
        response.put("error", "検証失敗");
        response.put("message", "リクエストパラメータ検証失敗");
        response.put("details", errors);
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        return Mono.just(ResponseEntity.badRequest().body(response));
    }


    /**
     * 従業員未発見例外の処理
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "従業員未発見");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    /**
     * 従業員番号重複例外の処理
     */
    @ExceptionHandler(DuplicateEmployeeNumberException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleDuplicateEmployeeNumberException(DuplicateEmployeeNumberException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "従業員番号重複");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.CONFLICT.value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(response));
    }

    /**
     * 制約違反例外の処理（パスパラメータとクエリパラメータ検証）
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
            response.put("details", "パラメータ検証失敗");
        }

        response.put("error", "パラメータ検証失敗");
        response.put("message", "リクエストパラメータが検証ルールに適合しません");
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    /**
     * サーバー入力例外の処理（必須パラメータ不足など）
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleServerWebInputException(ServerWebInputException ex) {
        Map<String, Object> response = new HashMap<>();
        
        String message = ex.getMessage();
        if (message != null && message.contains("Required query parameter")) {
            response.put("error", "パラメータ検証失敗");
            response.put("message", "必須のクエリパラメータが不足しています");
            response.put("details", "必要なパラメータを確認してください");
        } else if (message != null && message.contains("Required request parameter")) {
            response.put("error", "パラメータ検証失敗");
            response.put("message", "必須のリクエストパラメータが不足しています");
            response.put("details", "必要なパラメータを確認してください");
        } else {
            response.put("error", "パラメータ検証失敗");
            response.put("message", "リクエストパラメータが検証ルールに適合しません");
            response.put("details", message != null ? message : "パラメータ検証失敗");
        }
        
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    /**
     * 汎用例外の処理
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        
        // 検証関連の例外かチェック
        if (ex.getMessage() != null && ex.getMessage().contains("Validation failure")) {
            response.put("error", "検証失敗");
            response.put("message", "リクエストデータが検証ルールに適合しません");
            response.put("details", "入力データのフォーマットと長さを確認してください");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return Mono.just(ResponseEntity.badRequest().body(response));
        }
        
        // その他のタイプの検証例外かチェック
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
                response.put("details", "パラメータ検証失敗");
            }
            
            response.put("error", "検証失敗");
            response.put("message", "リクエストデータが検証ルールに適合しません");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return Mono.just(ResponseEntity.badRequest().body(response));
        }
        
        response.put("error", "サーバー内部エラー");
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "不明なエラー");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}

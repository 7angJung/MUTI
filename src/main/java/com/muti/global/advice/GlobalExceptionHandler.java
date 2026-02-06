package com.muti.global.advice;

import com.muti.global.common.ApiResponse;
import com.muti.global.error.BusinessException;
import com.muti.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 전역 예외 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred: code={}, message={}", e.getErrorCode().getCode(), e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Void> response = ApiResponse.error(errorCode.getCode(), e.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    /**
     * Bean Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getBindingResult().getAllErrors());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE.getCode(), message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * @ModelAttribute Validation 예외 처리
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        log.warn("Binding failed: {}", e.getBindingResult().getAllErrors());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE.getCode(), message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Type mismatch: parameter={}, value={}, requiredType={}",
                e.getName(), e.getValue(), e.getRequiredType());
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INVALID_TYPE_VALUE.getCode(),
                ErrorCode.INVALID_TYPE_VALUE.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * HTTP 요청 본문 파싱 실패 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Message not readable: {}", e.getMessage());
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INVALID_INPUT_VALUE.getCode(),
                "요청 본문을 읽을 수 없습니다.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * HTTP 메서드 미지원 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported: {}", e.getMethod());
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                ErrorCode.METHOD_NOT_ALLOWED.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(response);
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected exception occurred", e);
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
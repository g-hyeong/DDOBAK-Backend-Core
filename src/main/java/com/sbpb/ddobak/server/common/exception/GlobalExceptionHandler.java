package com.sbpb.ddobak.server.common.exception;

import com.sbpb.ddobak.server.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 전역 예외 처리기
 * 
 * 모든 예외를 중앙에서 처리하여 일관된 에러 응답 제공
 * 
 * 처리하는 예외 유형:
 * - 비즈니스 예외 (BusinessException)
 * - 검증 예외 (Validation)
 * - 시스템 예외 (RuntimeException, Exception)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     * 애플리케이션에서 의도적으로 발생시킨 예외들을 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred: {}", e.getLoggingMessage());

        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    /**
     * 검증 예외 처리 (@Valid 어노테이션)
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationException(BindException e) {
        log.warn("Validation error occurred: {}", e.getMessage());

        String message = e.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("Validation failed");

        return ResponseEntity
            .status(ErrorCode.INVALID_INPUT.getHttpStatus())
            .body(ApiResponse.error(ErrorCode.INVALID_INPUT, message));
    }

    /**
     * 파일 업로드 크기 초과 예외 처리
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("File upload size exceeded: {}", e.getMessage());

        return ResponseEntity
            .status(ErrorCode.OCR_FILE_TOO_LARGE.getHttpStatus())
            .body(ApiResponse.error(ErrorCode.OCR_FILE_TOO_LARGE, "File size exceeds the allowed limit"));
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());

        return ResponseEntity
            .status(ErrorCode.INVALID_INPUT.getHttpStatus())
            .body(ApiResponse.error(ErrorCode.INVALID_INPUT, e.getMessage()));
    }

    /**
     * 예상치 못한 모든 예외 처리
     * 위에서 처리되지 않은 모든 예외의 최종 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception e) {
        log.error("Unexpected error occurred", e);

        return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
            .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
    }
}
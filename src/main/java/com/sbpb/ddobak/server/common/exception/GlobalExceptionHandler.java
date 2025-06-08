package com.sbpb.ddobak.server.common.exception;

import com.sbpb.ddobak.server.common.response.ApiResponse;

/**
 * 전역 예외 처리기
 * 
 * 모든 예외를 중앙에서 처리하여 일관된 에러 응답 제공
 * 
 * 처리하는 예외 유형:
 * - 비즈니스 예외 (BusinessException)
 * - 시스템 예외 (RuntimeException, Exception)
 * 
 * 참고: Spring Web 의존성 추가 후 @RestControllerAdvice와 @ExceptionHandler 사용 가능
 */
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     * 애플리케이션에서 의도적으로 발생시킨 예외들을 처리
     */
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        // WARN 레벨로 로깅 (비즈니스 로직상 예상 가능한 예외)
        System.out.println("WARN: Business exception occurred - " + e.getLoggingMessage());

        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 검증 예외 처리
     */
    public ApiResponse<Void> handleValidationException(ValidationException e) {
        System.out.println("WARN: Validation error - " + e.getMessage());

        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 리소스 없음 예외 처리
     */
    public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException e) {
        System.out.println("WARN: Resource not found - " + e.getMessage());

        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 중복 리소스 예외 처리
     */
    public ApiResponse<Void> handleDuplicateResourceException(DuplicateResourceException e) {
        System.out.println("WARN: Duplicate resource - " + e.getMessage());

        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 외부 서비스 예외 처리
     */
    public ApiResponse<Void> handleExternalServiceException(ExternalServiceException e) {
        System.out.println("WARN: External service error - " + e.getMessage());

        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 예상치 못한 모든 예외 처리
     * 위에서 처리되지 않은 모든 예외의 최종 처리
     */
    public ApiResponse<Void> handleUnexpectedException(Exception e) {
        // ERROR 레벨로 로깅 (예상치 못한 시스템 오류)
        System.out.println("ERROR: Unexpected error occurred - " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace(); // 스택 트레이스 출력 (개발 환경에서만)

        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}
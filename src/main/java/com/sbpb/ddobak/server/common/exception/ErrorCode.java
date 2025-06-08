package com.sbpb.ddobak.server.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시스템 전반에서 사용되는 에러 코드 정의
 * 
 * 에러 코드 체계:
 * - 2xxx: 성공 관련 (향후 확장용)
 * - 4xxx: 클라이언트 에러 (400번대 HTTP 상태와 매핑)
 * - 5xxx: 서버 에러 (500번대 HTTP 상태와 매핑)
 * 
 * 각 영역별 세부 분류:
 * - 4000-4099: 일반적인 클라이언트 에러
 * - 4100-4199: 인증/인가 에러
 * - 4200-4299: 비즈니스 로직 에러
 * - 4300-4399: 리소스 관련 에러
 * - 5000-5099: 일반적인 서버 에러
 * - 5100-5199: 데이터베이스 에러
 * - 5200-5299: 외부 서비스 에러
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== 4000-4099: 일반적인 클라이언트 에러 =====
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 4000, "Invalid input provided"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 4001, "Invalid type value"),
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, 4002, "Required input value is missing"),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, 4003, "Invalid JSON format"),
    INVALID_PARAMETER_FORMAT(HttpStatus.BAD_REQUEST, 4004, "Invalid parameter format"),
    INVALID_HEADER_VALUE(HttpStatus.BAD_REQUEST, 4005, "Invalid header value"),
    INVALID_QUERY_PARAMETER(HttpStatus.BAD_REQUEST, 4006, "Invalid query parameter"),
    REQUEST_BODY_MISSING(HttpStatus.BAD_REQUEST, 4007, "Request body is missing"),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, 4008, "Invalid content type"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 4050, "HTTP method not allowed"),

    // ===== 4100-4199: 인증/인가 에러 =====
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 4100, "Authentication required"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, 4101, "Invalid credentials provided"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 4102, "Token has expired"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 4103, "Invalid token provided"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 4104, "Token not found"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 4105, "Refresh token has expired"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 4106, "Invalid refresh token"),
    TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, 4107, "Token has been revoked"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, 4150, "Access denied"),
    INSUFFICIENT_PRIVILEGES(HttpStatus.FORBIDDEN, 4151, "Insufficient privileges"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, 4152, "Account is locked"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, 4153, "Account is disabled"),

    // ===== 4200-4299: 비즈니스 로직 에러 =====
    BUSINESS_RULE_VIOLATION(HttpStatus.BAD_REQUEST, 4200, "Business rule violation"),
    OPERATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, 4201, "Operation not allowed"),
    INVALID_STATE_TRANSITION(HttpStatus.BAD_REQUEST, 4202, "Invalid state transition"),
    PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED, 4220, "Precondition failed"),
    RESOURCE_LOCKED(HttpStatus.CONFLICT, 4250, "Resource is locked"),
    QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, 4290, "Quota exceeded"),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, 4291, "Rate limit exceeded"),

    // ===== 4300-4399: 리소스 관련 에러 =====
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, 4300, "Resource not found"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, 4301, "Entity not found"),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, 4350, "Resource already exists"),
    DUPLICATE_KEY_VALUE(HttpStatus.CONFLICT, 4351, "Duplicate key value"),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, 4352, "Resource already exists"),
    VERSION_CONFLICT(HttpStatus.CONFLICT, 4360, "Version conflict detected"),
    OPTIMISTIC_LOCK_FAILURE(HttpStatus.CONFLICT, 4361, "Optimistic lock failure"),

    // ===== 5000-5099: 일반적인 서버 에러 =====
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "Internal server error occurred"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 5001, "Service temporarily unavailable"),
    CONFIGURATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "Configuration error"),
    INITIALIZATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5003, "Initialization error"),
    PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5004, "Processing error occurred"),

    // ===== 5100-5199: 데이터베이스 에러 =====
    DATABASE_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, 5100, "Database connection error"),
    DATABASE_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, 5101, "Database operation timeout"),
    TRANSACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 5102, "Transaction failed"),
    CONSTRAINT_VIOLATION(HttpStatus.CONFLICT, 5103, "Database constraint violation"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, 5104, "Data integrity violation"),
    SQL_SYNTAX_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5105, "SQL syntax error"),
    DATABASE_LOCK_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, 5106, "Database lock timeout"),

    // ===== 5200-5299: 외부 서비스 에러 =====
    EXTERNAL_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 5200, "External service unavailable"),
    EXTERNAL_SERVICE_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, 5201, "External service timeout"),
    EXTERNAL_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, 5202, "External service error"),
    EXTERNAL_API_RATE_LIMIT(HttpStatus.TOO_MANY_REQUESTS, 5203, "External API rate limit exceeded"),
    EXTERNAL_SERVICE_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, 5204, "External service authentication failed"),
    EXTERNAL_SERVICE_AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, 5205, "External service authorization failed"),
    CIRCUIT_BREAKER_OPEN(HttpStatus.SERVICE_UNAVAILABLE, 5250, "Circuit breaker is open");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    /**
     * HTTP 상태 코드 반환
     */
    public int getStatusCode() {
        return httpStatus.value();
    }

    /**
     * 문자열 형태의 에러 코드 반환
     */
    public String getCodeAsString() {
        return String.valueOf(code);
    }
}
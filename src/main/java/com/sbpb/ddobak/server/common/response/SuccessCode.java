package com.sbpb.ddobak.server.common.response;

/**
 * 성공 응답에 사용되는 코드 정의
 * 
 * 성공 코드 체계:
 * - 2000-2099: 일반적인 성공 응답
 * - 2100-2199: 생성 관련 성공
 * - 2200-2299: 수정/업데이트 관련 성공
 * - 2300-2399: 삭제 관련 성공
 * - 2400-2499: 조회 관련 성공
 * - 2500-2599: 인증/인가 관련 성공
 */
public enum SuccessCode {

    // ===== 2000-2099: 일반적인 성공 응답 =====
    SUCCESS(2000, "Request processed successfully"),
    OPERATION_COMPLETED(2001, "Operation completed successfully"),

    // ===== 2100-2199: 생성 관련 성공 =====
    CREATED(2100, "Resource created successfully"),
    USER_CREATED(2101, "User created successfully"),
    ACCOUNT_CREATED(2102, "Account created successfully"),
    DOCUMENT_CREATED(2103, "Document created successfully"),
    CONTENT_CREATED(2104, "Content created successfully"),

    // ===== 2200-2299: 수정/업데이트 관련 성공 =====
    UPDATED(2200, "Resource updated successfully"),
    USER_UPDATED(2201, "User updated successfully"),
    PROFILE_UPDATED(2202, "Profile updated successfully"),
    DOCUMENT_UPDATED(2203, "Document updated successfully"),
    CONTENT_UPDATED(2204, "Content updated successfully"),
    STATUS_UPDATED(2205, "Status updated successfully"),

    // ===== 2300-2399: 삭제 관련 성공 =====
    DELETED(2300, "Resource deleted successfully"),
    USER_DELETED(2301, "User deleted successfully"),
    DOCUMENT_DELETED(2302, "Document deleted successfully"),
    CONTENT_DELETED(2303, "Content deleted successfully"),

    // ===== 2400-2499: 조회 관련 성공 =====
    DATA_RETRIEVED(2400, "Data retrieved successfully"),
    LIST_RETRIEVED(2401, "List retrieved successfully"),
    USER_RETRIEVED(2402, "User information retrieved successfully"),
    DOCUMENT_RETRIEVED(2403, "Document retrieved successfully"),
    CONTENT_RETRIEVED(2404, "Content retrieved successfully"),
    SEARCH_COMPLETED(2450, "Search completed successfully"),

    // ===== 2500-2599: 인증/인가 관련 성공 =====
    LOGIN_SUCCESS(2500, "Login completed successfully"),
    LOGOUT_SUCCESS(2501, "Logout completed successfully"),
    TOKEN_REFRESHED(2502, "Token refreshed successfully"),
    PASSWORD_CHANGED(2503, "Password changed successfully"),
    EMAIL_VERIFIED(2504, "Email verified successfully"),
    ACCOUNT_ACTIVATED(2505, "Account activated successfully"),
    PERMISSION_GRANTED(2506, "Permission granted successfully");

    private final int code;
    private final String message;

    SuccessCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 성공 코드 반환
     */
    public int getCode() {
        return code;
    }

    /**
     * 성공 메시지 반환
     */
    public String getMessage() {
        return message;
    }

    /**
     * 문자열 형태의 성공 코드 반환
     */
    public String getCodeAsString() {
        return String.valueOf(code);
    }
}
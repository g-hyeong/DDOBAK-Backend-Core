package com.sbpb.ddobak.server.common.response;

/**
 * 공통 시스템 성공 코드 정의 (5xxx 범위)
 * 
 * 새로운 도메인별 코드 체계:
 * - 1xxx: Auth 도메인 (인증/인가)
 * - 2xxx: User 도메인 (사용자 관리)
 * - 3xxx: DocumentProcess 도메인 (문서 처리)
 * - 4xxx: ExternalContent 도메인 (외부 컨텐츠)
 * - 5xxx: Common/System (공통 시스템)
 * 
 * 각 도메인 내 분류:
 * - x000~x099: 정상 또는 경고 수준
 * - x100~x999: 에러
 * 
 * Common/System 성공 코드 체계:
 * - 5000-5099: 일반적인 성공 응답
 */
public enum SuccessCode implements BaseSuccessCode {

    // ===== 5000-5099: 일반적인 성공 응답 =====
    SUCCESS(5000, "Request processed successfully"),
    OPERATION_COMPLETED(5001, "Operation completed successfully"),
    CREATED(5010, "Resource created successfully"),
    UPDATED(5020, "Resource updated successfully"),
    DELETED(5030, "Resource deleted successfully"),
    DATA_RETRIEVED(5040, "Data retrieved successfully"),
    LIST_RETRIEVED(5041, "List retrieved successfully"),
    SEARCH_COMPLETED(5050, "Search completed successfully");

    private final int code;
    private final String message;

    SuccessCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 성공 코드 반환
     */
    @Override
    public int getCode() {
        return code;
    }

    /**
     * 성공 메시지 반환
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 문자열 형태의 성공 코드 반환
     */
    @Override
    public String getCodeAsString() {
        return String.valueOf(code);
    }
}
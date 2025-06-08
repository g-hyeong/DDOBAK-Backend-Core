package com.sbpb.ddobak.server.common.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 모든 비즈니스 예외의 기반 클래스
 * 
 * 특징:
 * - 에러 코드와 HTTP 상태를 자동으로 매핑
 * - 추적 ID를 통한 로그 연관성 제공
 * - 디버깅을 위한 추가 속성 저장 기능
 * - 구조화된 로그 메시지 생성
 */
public abstract class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String traceId;
    private final Map<String, Object> properties;

    /**
     * 기본 생성자 - 에러 코드의 기본 메시지 사용
     */
    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.traceId = UUID.randomUUID().toString();
        this.properties = new HashMap<>();
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     */
    protected BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.traceId = UUID.randomUUID().toString();
        this.properties = new HashMap<>();
    }

    /**
     * 원인 예외를 포함하는 생성자
     */
    protected BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.traceId = UUID.randomUUID().toString();
        this.properties = new HashMap<>();
    }

    /**
     * 에러 코드 반환
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 추적 ID 반환
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * 추가 속성 맵 반환 (읽기 전용)
     */
    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }

    /**
     * 디버깅용 추가 속성 설정
     * 
     * @param key   속성 키
     * @param value 속성 값
     * @return 체이닝을 위한 자기 자신 반환
     */
    public BusinessException addProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    /**
     * 로깅용 구조화된 메시지 생성
     * 형식: [에러코드] 메시지 - TraceId: xxx - Properties: {key=value}
     */
    public String getLoggingMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%d] %s", errorCode.getCode(), getMessage()));
        sb.append(String.format(" - TraceId: %s", traceId));

        if (!properties.isEmpty()) {
            sb.append(" - Properties: ").append(properties);
        }

        return sb.toString();
    }

    /**
     * 간단한 정보 반환 (민감하지 않은 정보만)
     */
    public String getSimpleMessage() {
        return String.format("Error %d: %s", errorCode.getCode(), getMessage());
    }
}
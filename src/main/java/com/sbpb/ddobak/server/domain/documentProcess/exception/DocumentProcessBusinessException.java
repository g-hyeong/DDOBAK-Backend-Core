package com.sbpb.ddobak.server.domain.documentProcess.exception;

import java.util.HashMap;
import java.util.Map;

import com.sbpb.ddobak.server.common.utils.IdGenerator;

/**
 * DocumentProcess 도메인 전용 비즈니스 예외
 */
public abstract class DocumentProcessBusinessException extends RuntimeException {

    private final DocumentProcessErrorCode errorCode;
    private final String traceId;
    private final Map<String, Object> properties;

    /**
     * 기본 생성자 - 에러 코드의 기본 메시지 사용
     */
    protected DocumentProcessBusinessException(DocumentProcessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.traceId = IdGenerator.generateTraceId();
        this.properties = new HashMap<>();
    }

    /**
     * 커스텀 메시지를 사용하는 생성자
     */
    protected DocumentProcessBusinessException(DocumentProcessErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.traceId = IdGenerator.generateTraceId();
        this.properties = new HashMap<>();
    }

    /**
     * 원인 예외를 포함하는 생성자
     */
    protected DocumentProcessBusinessException(DocumentProcessErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.traceId = IdGenerator.generateTraceId();
        this.properties = new HashMap<>();
    }

    /**
     * 에러 코드 반환
     */
    public DocumentProcessErrorCode getErrorCode() {
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
     */
    public DocumentProcessBusinessException addProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    /**
     * 로깅용 구조화된 메시지 생성
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
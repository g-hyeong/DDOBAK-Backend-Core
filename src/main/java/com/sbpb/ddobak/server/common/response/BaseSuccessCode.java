package com.sbpb.ddobak.server.common.response;

/**
 * 모든 성공 코드가 구현해야 하는 공통 인터페이스
 */
public interface BaseSuccessCode {
    
    /**
     * 성공 코드 반환
     */
    int getCode();
    
    /**
     * 성공 메시지 반환
     */
    String getMessage();
    
    /**
     * 문자열 형태의 성공 코드 반환
     */
    String getCodeAsString();
} 
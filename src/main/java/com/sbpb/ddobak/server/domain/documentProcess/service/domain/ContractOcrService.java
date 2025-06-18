package com.sbpb.ddobak.server.domain.documentProcess.service.domain;

import com.sbpb.ddobak.server.domain.documentProcess.dto.ContractOcrRequest;
import com.sbpb.ddobak.server.domain.documentProcess.dto.ContractOcrResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 계약서 OCR 도메인 서비스 (Domain Layer)
 * OCR 관련 핵심 비즈니스 로직을 담당
 */
public interface ContractOcrService {
    
    /**
     * OCR 처리 수행
     */
    ContractOcrResponse processOcr(ContractOcrRequest request, String userId);
    
    /**
     * OCR 파일 검증
     */
    void validateOcrFile(MultipartFile file);
} 
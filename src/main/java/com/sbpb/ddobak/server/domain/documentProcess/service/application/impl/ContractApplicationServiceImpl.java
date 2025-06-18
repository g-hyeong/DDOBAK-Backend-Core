package com.sbpb.ddobak.server.domain.documentProcess.service.application.impl;

import com.sbpb.ddobak.server.domain.documentProcess.dto.*;
import com.sbpb.ddobak.server.domain.documentProcess.service.application.ContractApplicationService;
import com.sbpb.ddobak.server.domain.documentProcess.service.domain.ContractOcrService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 계약서 처리 애플리케이션 서비스 구현체 (Application Layer)
 * Use Case를 조정하고 도메인 서비스들을 조합하여 전체 비즈니스 플로우를 관리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractApplicationServiceImpl implements ContractApplicationService {

    private final ContractOcrService contractOcrService;

    /**
     * OCR 처리 요청
     * OCR 도메인 서비스에 위임
     */
    @Override
    public ContractOcrResponse processOcr(ContractOcrRequest request, String userId) {
        log.info("Application: Processing OCR request for user: {}", userId);
        return contractOcrService.processOcr(request, userId);
    }
} 
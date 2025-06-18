package com.sbpb.ddobak.server.domain.documentProcess.service.domain.impl;

import com.sbpb.ddobak.server.domain.documentProcess.dto.ContractOcrRequest;
import com.sbpb.ddobak.server.domain.documentProcess.dto.ContractOcrResponse;
import com.sbpb.ddobak.server.domain.documentProcess.service.domain.ContractOcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 계약서 OCR 도메인 서비스 구현체 (Domain Layer)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ContractOcrServiceImpl implements ContractOcrService {

    @Override
    public ContractOcrResponse processOcr(ContractOcrRequest request, String userId) {
        // TODO 
        return null;
    }

    @Override
    public void validateOcrFile(MultipartFile file) {
        // TODO Auto-generated method stub
    }
}
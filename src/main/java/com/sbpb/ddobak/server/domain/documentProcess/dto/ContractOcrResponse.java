package com.sbpb.ddobak.server.domain.documentProcess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 계약서 OCR 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractOcrResponse {

    private String contractId;
} 
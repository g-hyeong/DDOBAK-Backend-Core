package com.sbpb.ddobak.server.domain.documentProcess.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 계약서 OCR 결과 엔티티
 */
@Entity
@Table(name = "contract_ocr_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractOcrResult {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "contract_id", nullable = false)
    private String contractId;

    @Column(name = "origin_content", columnDefinition = "TEXT")
    private String originContent; // 저장 형식 확정되고 최종 결정

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @Builder
    public ContractOcrResult(String id, String contractId, String originContent, LocalDateTime createdAt) {
        this.id = id;
        this.contractId = contractId;
        this.originContent = originContent;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
} 
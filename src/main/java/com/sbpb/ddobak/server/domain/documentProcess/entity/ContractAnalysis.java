package com.sbpb.ddobak.server.domain.documentProcess.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 계약서 분석 결과 엔티티
 */
@Entity
@Table(name = "contract_analyses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractAnalysis {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "contract_id", nullable = false)
    private String contractId;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @Builder
    public ContractAnalysis(String id, String contractId, String summary, 
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contractId = contractId;
        this.summary = summary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 분석 요약 업데이트
     */
    public void updateSummary(String summary) {
        this.summary = summary;
        this.updatedAt = LocalDateTime.now();
    }
} 
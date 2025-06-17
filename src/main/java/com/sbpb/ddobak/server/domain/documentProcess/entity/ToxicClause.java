package com.sbpb.ddobak.server.domain.documentProcess.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 독소 조항 엔티티
 */
@Entity
@Table(name = "toxic_clauses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToxicClause {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "analysis_id", nullable = false)
    private String analysisId;

    @Column(name = "clause", columnDefinition = "TEXT")
    private String clause;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "source_contract_tag")
    private String sourceContractTag; // 저장 형식 정해지고

    @Column(name = "warn_level", nullable = false)
    private Integer warnLevel; // 일단 저장해 둬야 나중에 유연한 대응 가능할 듯

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", insertable = false, updatable = false)
    private ContractAnalysis contractAnalysis;

    @Builder
    public ToxicClause(String id, String analysisId, String clause, String reason, 
                      String sourceContractTag, Integer warnLevel) {
        this.id = id;
        this.analysisId = analysisId;
        this.clause = clause;
        this.reason = reason;
        this.sourceContractTag = sourceContractTag;
        this.warnLevel = warnLevel;
    }
} 
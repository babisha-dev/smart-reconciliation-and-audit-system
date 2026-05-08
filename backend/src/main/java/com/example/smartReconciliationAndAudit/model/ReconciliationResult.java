package com.example.smartReconciliationAndAudit.model;

import jakarta.persistence.*;
import com.example.smartReconciliationAndAudit.enums.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name="results" , indexes={
        @Index(name="idx_recon_txn", columnList = "transactionId"),
        @Index(name = "idx_recon_job", columnList = "uploadJobId"),
        @Index(name = "idx_recon_status", columnList = "matchStatus")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationResult {
    private long id;
    private long transactionId;
    private long uploadJobId;
    private long uploadRecordId;
    private long systemRecordId;

    private BigDecimal uploadedAmount;
    private BigDecimal systemRecord;
    private BigDecimal amountVariance;

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    private String manualResolveBy;
    private LocalDateTime resolvedAt;
    private String manualNote;

    @Column(columnDefinition = "TEXT")
    private String mismatchedFields;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate(){createdAt= LocalDateTime.now();}

    @PreUpdate
    protected void onUpdate(){
        updateAt=LocalDateTime.now();
    }

}

package com.example.smartReconciliationAndAudit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "records", indexes = {
        @Index(name="idx_txn_id",  columnList = "transactionId"),
        @Index(name = "idx_ref_num", columnList = "referenceNumber"),
        @Index(name = "idx_job_id", columnList = "uploadJobId")

})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
     private long id;
    private long transactionId;
    private LocalDate transactionDate;
private BigDecimal amount;
private String referenceNumber;
private  String concurrency;
private String description;
private String accountNumber;
    private Long uploadJobId;
    private Boolean isSystemRecord = false;

    @Column(columnDefinition = "TEXT")
    private String additionalData;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}

package com.example.smartReconciliationAndAudit.repository;

import com.example.smartReconciliationAndAudit.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord,Long> {
    List<TransactionRecord> findByUploadJobId(Long uploadJobId);

    @Query("SELECT r FROM TransactionRecord r WHERE r.isSystemRecord = true AND r.transactionId = ?1")
    Optional<TransactionRecord> findSystemByUploadJobId(Long uploadJobId);

    @Query("SELECT r FROM TransactionRecord r WHERE r.isSystemRecord = true AND r.referenceNumber = ?1")
    Optional<TransactionRecord> findSystemByReferenceNumber(String referenceNumber);

}

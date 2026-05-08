package com.example.smartReconciliationAndAudit.repository;

import com.example.smartReconciliationAndAudit.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord,Long> {
}

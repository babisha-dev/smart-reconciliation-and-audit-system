package com.example.smartReconciliationAndAudit.repository;

import com.example.smartReconciliationAndAudit.model.ReconciliationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationResultRepository extends JpaRepository<ReconciliationResult, Long> {
}

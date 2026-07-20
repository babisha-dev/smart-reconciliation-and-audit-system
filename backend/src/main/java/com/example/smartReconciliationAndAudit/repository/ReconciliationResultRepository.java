package com.example.smartReconciliationAndAudit.repository;

import com.example.smartReconciliationAndAudit.enums.MatchStatus;
import com.example.smartReconciliationAndAudit.model.ReconciliationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReconciliationResultRepository extends JpaRepository<ReconciliationResult, Long> {
    long countByMatchStatus(MatchStatus status);


    List<ReconciliationResult> findByUploadJobId(Long uploadJobId);
    List<ReconciliationResult> findByMatchStatus(MatchStatus status);
}

package com.example.smartReconciliationAndAudit.repository;

import com.example.smartReconciliationAndAudit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

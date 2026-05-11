package com.example.smartReconciliationAndAudit.repository;

import com.example.smartReconciliationAndAudit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByOrderByTimestampDesc();
    List<AuditLog> findByEntityTypeAndEntityIDOrderByTimestampDesc(String entityType,String entityId);
}

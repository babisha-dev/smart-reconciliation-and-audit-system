package com.example.smartReconciliationAndAudit.service;

import com.example.smartReconciliationAndAudit.model.AuditLog;
import com.example.smartReconciliationAndAudit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {
   final AuditLogRepository auditLogRepository;

   public void log(String entityType , Long entityId, String action, String performedBy, String performedByUsername, String oldValue, String newValue, String source){

      auditLogRepository.save(AuditLog.builder()
                      .entityType(entityType).entityId(entityId)
                      .performedBy(performedBy).performedByUsername(performedByUsername)
                      .source(source).oldValue(oldValue).newValue(newValue).build()
              );
   }


}

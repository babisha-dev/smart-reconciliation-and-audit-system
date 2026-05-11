package com.example.smartReconciliationAndAudit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name="audit_logs")
@Data  // to automate getter and setter
@Builder // object creation avoiding constructor explosion ,parameter ordering
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String entityType;
    private long entityId;
    private String performedBy;
    private String performedByUsername;
    @Column(columnDefinition = "TEXT")
    private String oldValue;
    @Column(columnDefinition = "TEXT")
    private String newValue;
    private String source;
    private String ipAddress;

    @Column(updatable = false, nullable = false)
    private LocalDateTime timestamp;
    @PrePersist
    protected  void  onCreate(){
        timestamp=LocalDateTime.now();
    }
}

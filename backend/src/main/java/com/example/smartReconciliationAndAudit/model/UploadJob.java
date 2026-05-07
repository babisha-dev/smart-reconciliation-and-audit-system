package com.example.smartReconciliationAndAudit.model;

import com.example.smartReconciliationAndAudit.enums.UploadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity @Table(name = "upload_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fileName;
    private String fileHash;
    private long uploadedBy;
    private String uploadedByName;
    private Integer totalRecords;
    private Integer processedRecords;
    @Enumerated(EnumType.STRING)
     private UploadStatus status;
     private String errorMessage;
    private LocalDateTime  startedAt;
    private LocalDateTime completedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){createdAt=LocalDateTime.now();}

}

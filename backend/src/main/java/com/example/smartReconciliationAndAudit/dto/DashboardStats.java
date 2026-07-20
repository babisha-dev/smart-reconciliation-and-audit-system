package com.example.smartReconciliationAndAudit.dto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DashboardStats {
    private  long totalRecords;
    private long matchedRecords;
    private  long totalUnmatchedRecords;
    private  long duplicateRecords;
    private  long totalPartiallyMatchedRecords;
    private long totalJobs;
    private double reconciliationAccuracy;


}

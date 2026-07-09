package com.example.smartReconciliationAndAudit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.responses.ApiResponse;
import  com.example.smartReconciliationAndAudit.enums.MatchStatus;
@RestController
@RequestMapping("api/reconciliation")
@Tag(name="3-reconciliation")
@SecurityRequirement(name="bearerAuth")
public class ReconciliationController {
    private final ReconciliationResultRepository resultRepo;
    private final UploadJobRepository uploadJob;

    @GetMapping("/dashboard")
    @Operation(summary = "dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStats>> dashboard(){
       long total= resultRepo.count();
       long matched=resultRepo.countByMatchStatus(MatchStatus.MATCHED);
        long partial=resultRepo.countByMatchStatus(MatchStatus.PARTIALLY_MATCHED);
        long mismatched=resultRepo.countByMatchStatus(MatchStatus.MISMATCHED);
        long duplicate=resultRepo.countByMatchStatus(MatchStatus.DUPLICATE);
        double accuracy = total > 0 ? Math.round(((double)(matched + partial) / total) * 10000.0) / 100.0 : 0;
        return ResponseEntity.ok(ApiResponse.ok(DashboardStats.builder().totalRecords(total)
                .matchedRecords(matched).duplicateRecords(duplicate).reconciliationAccuracy(accuracy)
                .totalJobs(uploadJob.count()).build()));



    }
}

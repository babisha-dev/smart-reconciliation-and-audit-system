package com.example.smartReconciliationAndAudit.controller;

import com.example.smartReconciliationAndAudit.dto.DashboardStats;
import com.example.smartReconciliationAndAudit.dto.ApiResponse;
import com.example.smartReconciliationAndAudit.enums.MatchStatus;
import com.example.smartReconciliationAndAudit.model.ReconciliationResult;
import com.example.smartReconciliationAndAudit.repository.ReconciliationResultRepository;
import com.example.smartReconciliationAndAudit.repository.UploadJobRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.models.responses.ApiResponse;
import  com.example.smartReconciliationAndAudit.enums.MatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.List;

import static org.apache.poi.ss.formula.functions.StatsLib.var;

@RestController
@RequestMapping("api/reconciliation")
@Tag(name="3-reconciliation")
@SecurityRequirement(name="bearerAuth")

public class ReconciliationController {
    private final ReconciliationResultRepository resultRepo;
    private final UploadJobRepository uploadJob;

    public ReconciliationController(ReconciliationResultRepository resultRepo, UploadJobRepository uploadJob) {
        this.resultRepo = resultRepo;
        this.uploadJob = uploadJob;
    }

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
    @GetMapping("/results/{jobId}")
    @Operation(summary="results of specific upload job")
    public  ResponseEntity<ApiResponse<List<ReconciliationResult>>> resultByID(@PathVariable Long jobId){
        return ResponseEntity.ok(ApiResponse.ok(resultRepo.findByUploadJobId(jobId)));
    }
    @GetMapping("/results")
    @Operation(summary = "All results, optionally filtered by status")
    public ResponseEntity<ApiResponse<List<ReconciliationResult>>> results(@RequestParam(required = false) String status){
     var data=(status!=null) ? resultRepo.findByMatchStatus(MatchStatus.valueOf(status.toUpperCase()) ): resultRepo.findAll();
     return  ResponseEntity.ok(ApiResponse.ok(data));

    }
}

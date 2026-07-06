package com.example.smartReconciliationAndAudit.controller;

import com.example.smartReconciliationAndAudit.dto.ApiResponse;
import com.example.smartReconciliationAndAudit.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name="2-File upload")
@SecurityRequirement(name="bearerAuth")
public class UploadController {
    private  final  FileProcessingService fps;

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<List<Map<String,String>>>> preview(@RequestParam("file") MultipartFile file){
        try{
            return ResponseEntity.ok(ApiResponse.ok(fps.preview(file)));
        }
        catch(Exception e){
             return ResponseEntity.badRequest().body(ApiResponse.error("Preview failed" +e.getMessage()));
        }
    }
}

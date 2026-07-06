package com.example.smartReconciliationAndAudit.controller;

import com.example.smartReconciliationAndAudit.dto.ApiResponse;
import com.example.smartReconciliationAndAudit.enums.UploadStatus;
import com.example.smartReconciliationAndAudit.model.UploadJob;
import com.example.smartReconciliationAndAudit.repository.UploadJobRepository;
import com.example.smartReconciliationAndAudit.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name="2-File upload")
@SecurityRequirement(name="bearerAuth")
public class UploadController {
    private  final  FileProcessingService fps;
    private  final UploadJobRepository uploadJobRepo;

    @PostMapping("/preview")
    @Operation(summary="preview first 20 rows of the file")
    public ResponseEntity<ApiResponse<List<Map<String,String>>>> preview(@RequestParam("file") MultipartFile file){
        try{
            return ResponseEntity.ok(ApiResponse.ok(fps.preview(file)));
        }
        catch(Exception e){
             return ResponseEntity.badRequest().body(ApiResponse.error("Preview failed" +e.getMessage()));
        }
    }
    @PostMapping("/submit")
    @Operation(summary = "submit file for async backend processing")
    public ResponseEntity<ApiResponse<UploadJob>> submit(@RequestParam("file") MultipartFile file, @RequestParam Map<String,String> params, Authentication auth){
try{
    String hash=fps.hash(file);
    var existing =uploadJobRepo.findByHash(hash);
if(existing.isPresent() && existing.get().getStatus()== UploadStatus.COMPLETED){
    return ResponseEntity.ok(ApiResponse.ok("File already Processed",existing.get()));
}
Map<String ,String> mapping=new HashMap<>();
params.forEach((k,v)->{
    if(k.startsWith("mapping_"))
        mapping.put(k.substring(8),v);
});
var job=UploadJob.builder()
        .fileName(file.getOriginalFilename())
        .fileHash(hash)
        .uploadedByName(auth.getName())
        .uploadedBy(0L)
        .status(UploadStatus.PROCESSING)
        .startedAt(LocalDateTime.now())
        .build();
uploadJobRepo.save(job);

byte[] fileBytes=fps.readBytes(file);
fps.processAsync(job.getId(), fileBytes,  mapping, auth.getName(), auth.getName());
return ResponseEntity.ok(ApiResponse.ok("Processing started", job));
}catch (Exception e){
    return ResponseEntity.badRequest().body(ApiResponse.error("Upload failed"+e.getMessage()));
}
    }
    @GetMapping("/jobs")
    @Operation(summary="List all upload jobs")
    public ResponseEntity<ApiResponse<List<UploadJob>>> jobList(){
return ResponseEntity.ok(ApiResponse.ok(uploadJobRepo.findByOrderByCreatedAtDesc()));
    }
}

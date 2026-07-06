package com.example.smartReconciliationAndAudit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name="2-File upload")
@SecurityRequirement(name="bearerAuth")
public class UploadController {
}

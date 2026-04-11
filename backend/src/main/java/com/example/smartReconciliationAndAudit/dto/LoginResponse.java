package com.example.smartReconciliationAndAudit.dto;

import lombok.Builder;
import lombok.Data;

import javax.management.relation.Role;

@Data
@Builder
public class LoginResponse {

    private String token;
    private String name;
    private String role;
}

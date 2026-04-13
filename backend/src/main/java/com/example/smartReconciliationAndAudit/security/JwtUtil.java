package com.example.smartReconciliationAndAudit.security;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    private  String secret;
    public Key key(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}

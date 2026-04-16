package com.example.smartReconciliationAndAudit.controller;


import com.example.smartReconciliationAndAudit.config.SecurityConfig;
import com.example.smartReconciliationAndAudit.dto.ApiResponse;
import com.example.smartReconciliationAndAudit.dto.LoginRequest;
import com.example.smartReconciliationAndAudit.dto.LoginResponse;
import com.example.smartReconciliationAndAudit.model.User;
import com.example.smartReconciliationAndAudit.repository.UserRepository;
import com.example.smartReconciliationAndAudit.security.JwtUtil;
import com.example.smartReconciliationAndAudit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
private final AuthenticationManager authManager;
private final UserDetailsService uds;
private final UserRepository repo;
private final JwtUtil jwtUtil;
    @PostMapping("/login")

    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req){
        try{
authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getName(),req.getPassword()));
UserDetails u=uds.loadUserByUsername(req.getName());
User user=repo.findByName(req.getName()).orElseThrow();
String token=jwtUtil.generate(u,user.getRole().name());

return ResponseEntity.ok(ApiResponse.ok(LoginResponse.builder().token(token).name(user.getName()).role(user.getRole().name()).build()));
    }
    catch(BadCredentialsException e){
return ResponseEntity.status(401).body(ApiResponse.error("Invalid username or password"));
        }
    }
}

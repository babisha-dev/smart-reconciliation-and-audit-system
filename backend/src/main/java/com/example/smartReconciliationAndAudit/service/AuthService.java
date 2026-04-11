package com.example.smartReconciliationAndAudit.service;

import com.example.smartReconciliationAndAudit.dto.LoginRequest;
import com.example.smartReconciliationAndAudit.dto.LoginResponse;
import com.example.smartReconciliationAndAudit.enums.Role;
import com.example.smartReconciliationAndAudit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import  com.example.smartReconciliationAndAudit.model.User;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public String register(User user){
if(userRepository.existsByEmail(user.getEmail())){
 throw new RuntimeException("User already exists");
}
User user1=User.builder()
        .name(user.getName())
        .email(user.getEmail())
        //.password(passwordEncoder.encode(user.getPassword()))
        .role(Role.ANALYST)
        .active(true)
        .build();

     userRepository.save(user1);
return "User successfully registered.";

    }
    public LoginResponse login(LoginRequest req){
       User user=userRepository.findByEmail(req.getEmail()).orElseThrow(()->new RuntimeException("User not found"));
      // if(!passwordEncoder.matches(req.getPassword(),user.getPassword())){
          // throw new RuntimeException("Password didnot match");
     //  }
      //  String token=jwtUtil.generateToken(user.getName(),user.getRole().name());
        return LoginResponse.builder()
             //   .token(token)
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}

package com.example.smartReconciliationAndAudit.config;

import com.example.smartReconciliationAndAudit.enums.Role;
import com.example.smartReconciliationAndAudit.model.User;
import com.example.smartReconciliationAndAudit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner { //spring runs this after application starts
    private final UserRepository userRepository;

   @Override
    public void run(String... args) { //This is executed once at startup
       seed("admin", "admin@recon.com", "admin123", Role.ADMIN);
       seed("analyst1", "analyst@recon.com", "analyst123", Role.ANALYST);
       seed("viewer1", "viewer@recon.com", "viewer123", Role.VIEWER);
     log.info("Users created");
    }

    public void seed(String name, String email, String password, Role role){
       if(!userRepository.existsByName(name)){
          userRepository.save(User.builder()
                  .name(name)
                          .email(email)
                          .password(password)
                          .role(role)
                          .active(true)
                  .build());
       }
    }
}

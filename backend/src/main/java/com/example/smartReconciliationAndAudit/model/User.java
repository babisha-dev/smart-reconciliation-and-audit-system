package com.example.smartReconciliationAndAudit.model;


import jakarta.validation.constraints.Email;
import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import com.example.smartReconciliationAndAudit.enums.Role;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String name;
    @Getter
    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;
    @NotBlank
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    private boolean active ;
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected  void createdAt(){
        createdAt=LocalDateTime.now();
    }

    public String getEmail(){
        return email;
    }


}

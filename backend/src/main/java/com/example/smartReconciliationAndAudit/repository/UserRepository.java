package com.example.smartReconciliationAndAudit.repository;


import com.example.smartReconciliationAndAudit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Boolean existsByName(String name);
    Optional<User> findByName(String name);
}

package com.example.smartReconciliationAndAudit.repository;

import com.example.smartReconciliationAndAudit.model.UploadJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadJobRepository extends JpaRepository<UploadJob,Long> {
Optional<UploadJob> findByHash(String hash);
}

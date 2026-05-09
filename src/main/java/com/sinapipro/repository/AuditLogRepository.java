package com.sinapipro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

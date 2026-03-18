package br.edu.ifrn.sinapiPRO.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

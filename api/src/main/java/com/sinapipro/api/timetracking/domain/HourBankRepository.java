package com.sinapipro.api.timetracking.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface HourBankRepository extends JpaRepository<HourBank, UUID> {
    List<HourBank> findByEmployeeIdAndProjectIdOrderByReferenceDateDesc(UUID employeeId, UUID projectId);

    @Query("SELECT COALESCE(SUM(CASE WHEN h.type = 'CREDIT' THEN h.hours ELSE -h.hours END), 0) FROM HourBank h WHERE h.employeeId = :employeeId AND h.projectId = :projectId")
    BigDecimal getBalance(UUID employeeId, UUID projectId);
}

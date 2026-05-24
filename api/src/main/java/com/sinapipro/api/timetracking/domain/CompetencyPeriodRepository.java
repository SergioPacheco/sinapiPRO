package com.sinapipro.api.timetracking.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompetencyPeriodRepository extends JpaRepository<CompetencyPeriod, UUID> {
    List<CompetencyPeriod> findByProjectIdOrderByYearMonthDesc(UUID projectId);
    Optional<CompetencyPeriod> findByProjectIdAndYearMonth(UUID projectId, LocalDate yearMonth);
}

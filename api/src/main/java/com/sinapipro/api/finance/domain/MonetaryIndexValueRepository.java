package com.sinapipro.api.finance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MonetaryIndexValueRepository extends JpaRepository<MonetaryIndexValue, UUID> {
    Optional<MonetaryIndexValue> findByIndexIdAndReferenceMonth(UUID indexId, LocalDate referenceMonth);
}

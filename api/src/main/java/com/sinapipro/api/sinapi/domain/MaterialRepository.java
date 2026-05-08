package com.sinapipro.api.sinapi.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialRepository extends JpaRepository<Material, UUID> {

    Optional<Material> findBySinapiCode(String sinapiCode);

    @Query(value = """
            SELECT m.* FROM material m
            WHERE m.search_vector @@ plainto_tsquery('portuguese', :query)
            ORDER BY ts_rank(m.search_vector, plainto_tsquery('portuguese', :query)) DESC
            """, nativeQuery = true)
    Page<Material> fullTextSearch(String query, Pageable pageable);

    @Query("""
            SELECT mp.price FROM MaterialPrice mp
            WHERE mp.material.id = :materialId
              AND mp.state = :state
              AND mp.referenceMonth = :referenceMonth
            """)
    Optional<BigDecimal> findPrice(UUID materialId, String state, LocalDate referenceMonth);

    @Query("""
            SELECT mp FROM MaterialPrice mp
            WHERE mp.material.id IN :materialIds
              AND mp.state = :state
              AND mp.referenceMonth = :referenceMonth
            """)
    List<MaterialPrice> findPricesBatch(List<UUID> materialIds, String state, LocalDate referenceMonth);
}

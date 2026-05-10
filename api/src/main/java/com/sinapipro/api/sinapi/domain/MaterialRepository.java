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
            WHERE (:query IS NULL OR m.search_vector @@ plainto_tsquery('portuguese', cast(:query as text)))
              AND (:origin IS NULL OR m.origin = cast(:origin as text))
              AND (:unit IS NULL OR m.unit = cast(:unit as text))
            ORDER BY CASE WHEN :query IS NOT NULL THEN ts_rank(m.search_vector, plainto_tsquery('portuguese', cast(:query as text))) ELSE 0 END DESC, m.sinapi_code
            """, countQuery = """
            SELECT count(*) FROM material m
            WHERE (:query IS NULL OR m.search_vector @@ plainto_tsquery('portuguese', cast(:query as text)))
              AND (:origin IS NULL OR m.origin = cast(:origin as text))
              AND (:unit IS NULL OR m.unit = cast(:unit as text))
            """, nativeQuery = true)
    Page<Material> findFiltered(String query, String origin, String unit, Pageable pageable);

    @Query(value = "SELECT DISTINCT m.unit FROM material m ORDER BY m.unit", nativeQuery = true)
    List<String> findDistinctUnits();

    @Query(value = "SELECT DISTINCT m.origin FROM material m ORDER BY m.origin", nativeQuery = true)
    List<String> findDistinctOrigins();

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

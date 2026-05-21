package com.sinapipro.api.sinapi.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompositionRepository extends JpaRepository<Composition, UUID> {

    Optional<Composition> findBySinapiCode(String sinapiCode);

    @Query(value = """
            SELECT c.* FROM composition c
            WHERE c.is_current = true
              AND (:query IS NULL OR c.search_vector @@ plainto_tsquery('portuguese', cast(:query as text)))
              AND (:origin IS NULL OR c.origin = cast(:origin as text))
              AND (:unit IS NULL OR c.unit = cast(:unit as text))
              AND (:groupName IS NULL OR c.group_name = cast(:groupName as text))
            ORDER BY CASE WHEN :query IS NOT NULL THEN ts_rank(c.search_vector, plainto_tsquery('portuguese', cast(:query as text))) ELSE 0 END DESC, c.sinapi_code
            """, countQuery = """
            SELECT count(*) FROM composition c
            WHERE c.is_current = true
              AND (:query IS NULL OR c.search_vector @@ plainto_tsquery('portuguese', cast(:query as text)))
              AND (:origin IS NULL OR c.origin = cast(:origin as text))
              AND (:unit IS NULL OR c.unit = cast(:unit as text))
              AND (:groupName IS NULL OR c.group_name = cast(:groupName as text))
            """, nativeQuery = true)
    Page<Composition> findFiltered(String query, String origin, String unit, String groupName, Pageable pageable);

    @Query(value = """
            SELECT c.* FROM composition c
            WHERE c.is_current = true
              AND c.search_vector @@ plainto_tsquery('portuguese', cast(:query as text))
            ORDER BY ts_rank(c.search_vector, plainto_tsquery('portuguese', cast(:query as text))) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Composition> searchCurrentByText(@Param("query") String query, @Param("limit") int limit);

    @Query(value = "SELECT DISTINCT c.unit FROM composition c ORDER BY c.unit", nativeQuery = true)
    List<String> findDistinctUnits();

    @Query(value = "SELECT DISTINCT c.group_name FROM composition c WHERE c.group_name IS NOT NULL AND c.group_name != '' ORDER BY c.group_name", nativeQuery = true)
    List<String> findDistinctGroups();

    @Query(value = "SELECT DISTINCT c.origin FROM composition c ORDER BY c.origin", nativeQuery = true)
    List<String> findDistinctOrigins();
}

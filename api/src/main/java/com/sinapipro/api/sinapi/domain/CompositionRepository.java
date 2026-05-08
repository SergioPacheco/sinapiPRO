package com.sinapipro.api.sinapi.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CompositionRepository extends JpaRepository<Composition, UUID> {

    Optional<Composition> findBySinapiCode(String sinapiCode);

    @Query(value = """
            SELECT c.* FROM composition c
            WHERE c.search_vector @@ plainto_tsquery('portuguese', :query)
            ORDER BY ts_rank(c.search_vector, plainto_tsquery('portuguese', :query)) DESC
            """, nativeQuery = true)
    Page<Composition> fullTextSearch(String query, Pageable pageable);

    @Query("""
            SELECT c FROM Composition c
            WHERE (:groupName IS NULL OR c.groupName = :groupName)
            ORDER BY c.sinapiCode
            """)
    Page<Composition> findFiltered(String groupName, Pageable pageable);
}

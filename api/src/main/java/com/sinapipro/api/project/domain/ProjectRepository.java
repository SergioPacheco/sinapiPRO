package com.sinapipro.api.project.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByCode(String code);

    boolean existsByCode(String code);

    @Query(value = """
            SELECT p.* FROM project p
            WHERE (:query IS NULL OR (p.code ILIKE '%' || cast(:query as text) || '%' OR p.name ILIKE '%' || cast(:query as text) || '%' OR p.customer_name ILIKE '%' || cast(:query as text) || '%'))
              AND (:status IS NULL OR p.status = cast(:status as text))
            ORDER BY p.created_at DESC
            """, countQuery = """
            SELECT count(*) FROM project p
            WHERE (:query IS NULL OR (p.code ILIKE '%' || cast(:query as text) || '%' OR p.name ILIKE '%' || cast(:query as text) || '%' OR p.customer_name ILIKE '%' || cast(:query as text) || '%'))
              AND (:status IS NULL OR p.status = cast(:status as text))
            """, nativeQuery = true)
    Page<Project> findFiltered(String query, String status, Pageable pageable);
}
